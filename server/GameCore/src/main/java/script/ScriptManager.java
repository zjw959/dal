package script;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import security.MD5;
import utils.ExceptionEx;
import utils.StrEx;

/**
 * 脚本管理器，处理脚本的热加载和编译
 */
public class ScriptManager {

    private static final Logger LOGGER = Logger.getLogger(ScriptManager.class);

    /**
     * 单件接口
     * 
     * @return
     */
    public static ScriptManager getInstance() {
        return Singleton.INSTANCE.getManager();
    }


    public String getJavaClassPath() {
        return _javaClassPath;
    }

    /**
     * 初始化java脚本路径
     * 
     * @param javaFilePath java文件路径
     * @param javaClassPath 编译后的class文件路径
     * @param isDebug 是否是debug模式
     * @throws Exception
     */
    public void initialize(String javaFilePath, String javaClassPath, boolean isDebug)
            throws Exception {
        LOGGER.info("begin scriptManager initialize");
        if (_isInitialize) {
            throw new Exception("ScriptManager has bean initialized");
        }
        
        
        
        // Java脚本编译器初始化
        if (_compiler == null) {
            _compiler = new JavaScriptCompiler(javaFilePath, javaClassPath, isDebug);
        }

        _javaFilePath = javaFilePath;
        _javaClassPath = javaClassPath;

        // 先加载一次所有脚本
        loadAllScript();

        _isInitialize = true;

        LOGGER.info("end scriptManager initialize");

    }

    /**
     * 根据Id找到脚本
     * 
     * @param scriptId
     * @return
     */
    public IScript getScript(int scriptId) {
        ScriptBean script = _scriptIdBeans.get(scriptId);
        if (script == null) {
            LOGGER.error("找不到需要的脚本: " + scriptId);
            LOGGER.error(ExceptionEx.currentThreadTraces());
            return null;
        }
        return script.getScript();
    }

    /**
     * 根据name找到脚本
     * 
     * @param scriptName
     * @return
     */
    public IDynamicScript getScriptDy(String scriptName) {
        ScriptDynamic script = _scriptNameDys.get(scriptName);
        if (script == null) {
            LOGGER.error("找不到需要的脚本: " + scriptName);
            LOGGER.error(ExceptionEx.currentThreadTraces());
            return null;
        }
        return script.getScript();
    }

    /**
     * 加载所有脚本 注意,这里会强制全编译所有脚本,包括所有非IScript实现的脚本. 并且也同时会实例化他,但并没有使用,相当于做了一次强制编译检查.
     * 
     * 扩充实现 可以缓存下非IScript的实例缓存
     * 
     * @throws Exception
     */
    public void loadAllScript() throws Exception {
        synchronized (_loadLock) {
            LOGGER.info("begin loadAllScript");

            ArrayList<String> allJavaFile = _getAllJavaFiles(_javaFilePath);
            if (allJavaFile == null || allJavaFile.isEmpty()) {
                LOGGER.warn("指定的脚本路径[" + _javaFilePath + "]下没有 '*.java' 文件!");
                return;
            }

            ConcurrentHashMap<Integer, ScriptBean> scriptIds = new ConcurrentHashMap<>();
            ConcurrentHashMap<String, ScriptDynamic> scriptDys = new ConcurrentHashMap<>();

            // 开启多线程加载
            ExecutorService cachedExecutors =
                    Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

            List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();

            for (int i = 0; i < allJavaFile.size(); ++i) {
                String fileName = allJavaFile.get(i);

                Future<Boolean> future = cachedExecutors.submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        AbsScriptBean script = _loadScript(fileName);
                        if (script == null) {
                            throw new Exception("初始化Script为null, file:" + fileName);
                        }

                        if (ScriptDynamic.class.isAssignableFrom(script.getClass())) {
                            ScriptDynamic bean = (ScriptDynamic) script;
                            scriptDys.put(bean.getName(), bean);
                            return true;
                        }

                        ScriptBean bean = (ScriptBean) script;
                        if (scriptIds.containsKey(bean.getId())) {
                            throw new Exception("脚本中包含重复id的脚本! file:" + fileName);
                        }
                        if (bean.getId() == 0) {
                            throw new Exception("脚本id不能为0!" + fileName);
                        }
                        scriptIds.put(bean.getId(), bean);

                        return true;
                    }
                });

                futures.add(future);
            }

            cachedExecutors.shutdown();

            while (!cachedExecutors.isTerminated()) {
                LOGGER.info("wait load scripts...");
                Thread.sleep(1000);
            }

            for (int i = 0; i < futures.size(); i++) {
                Future<Boolean> future = futures.get(i);
                try {
                    if (!future.get()) {
                        throw new Exception("reload return false. unknow Excetion.");
                    }
                } catch (Exception e) {
                    throw e;
                }
            }

            LOGGER.info("replace scripts");

            // 进行对象替换
            ConcurrentHashMap<String, ScriptBean> scriptNames = new ConcurrentHashMap<>();

            Set<Entry<Integer, ScriptBean>> entries = scriptIds.entrySet();
            for (Entry<Integer, ScriptBean> entry : entries) {
                ScriptBean sb = entry.getValue();
                scriptNames.put(sb.getName(), sb);
                // 处理生命周期
                // 未处理新脚本新增ILifeCycle的情况
                try {
                    if (sb.getScript() instanceof ILifeCycle) {
                        if (sb.getIsNew()) {
                            ScriptBean oldBean = _scriptNameBeans.get(sb.getName());
                            if (oldBean != null && oldBean.getScript() instanceof ILifeCycle) {
                                ((ILifeCycle) oldBean.getScript()).destroy();
                            }
                            ((ILifeCycle) sb.getScript()).init();
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }

            // 处理生命周期
            Set<Entry<String, ScriptDynamic>> dySets = scriptDys.entrySet();
            for (Entry<String, ScriptDynamic> entry : dySets) {
                ScriptDynamic sd = entry.getValue();
                try {
                    if (sd.getScript() instanceof ILifeCycle) {
                        if (sd.getIsNew()) {
                            ScriptDynamic oldDynamic = _scriptNameDys.get(sd.getName());
                            if (oldDynamic != null
                                    && oldDynamic.getScript() instanceof ILifeCycle) {
                                ((ILifeCycle) oldDynamic.getScript()).destroy();
                            }
                            ((ILifeCycle) sd.getScript()).init();
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }

            _scriptIdBeans = scriptIds;
            _scriptNameBeans = scriptNames;

            _scriptNameDys = scriptDys;

            LOGGER.info("end loadAllScript");
        }
    }


    /**
     * 重新加载指定Id的脚本
     * 
     * @param scriptId
     * @throws Exception
     */
    public boolean loadScriptBean(int scriptId) throws Exception {
        synchronized (_loadLock) {
            try {
                ScriptBean oldBean = _scriptIdBeans.get(scriptId);
                if (oldBean == null) {
                    LOGGER.error("找不到脚本: " + scriptId
                            + " 无法加载. 如果是新脚本请使用loadScript(String name) 通过名字进行加载!");
                    return false;
                }
                _replaceScript(oldBean);
                return true;
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
                return false;
            }
        }
    }

    /**
     * 重新加载指定名字的脚本 新脚本只能通过类名的方式进行获取
     * 
     * @param scriptName
     * @throws Exception
     */
    public boolean loadScript(String scriptName) throws Exception {
        synchronized (_loadLock) {
            try {
                AbsScriptBean scriptBean = _scriptNameBeans.get(scriptName);
                AbsScriptBean scriptDy = _scriptNameDys.get(scriptName);
                if (scriptBean == null && scriptDy == null) {
                    LOGGER.info("没找到脚本,准备开始新增脚本. name:" + scriptName);
                    return _putNewScript(_loadScript(scriptName));
                } else {
                    LOGGER.info("找到脚本,准备开始替换脚本. name:" + scriptName);
                    return _replaceScript(scriptBean != null ? scriptBean : scriptDy);
                }
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
                return false;
            }
        }
    }

    /**
     * 通过类名加载一个脚本 如果脚本已经存在且未发生改变,不会重新加载,返回当前的对象 否则 返回当前最新的script
     * 
     * @param name
     * @return
     * @throws Exception
     */
    private AbsScriptBean _loadScript(String name) throws Exception {
        byte[] bytes = _readJavaSourceFile(name);
        if (bytes == null || bytes.length == 0) {
            throw new Exception("读取java文件异常: " + name);
        }

        String sign = null;
        sign = MD5.MD5Bytes(bytes);
        if (sign == null || StrEx.isEmpty(sign)) {
            throw new Exception("MD5 计算失败 脚本名: " + name);
        }

        // 确定已经存在的脚本是正常的
        ScriptBean scriptBean = _scriptNameBeans.get(name);
        if (scriptBean != null) {
            ScriptBean idBean = _scriptIdBeans.get(scriptBean.getId());
            if (idBean == null || !idBean.getSign().equalsIgnoreCase(scriptBean.getSign())) {
                LOGGER.error("脚本缓存中的对象不一致 idBean: " + idBean.getId() + " " + idBean.getName() + " "
                        + idBean.getSign() + " nameBean: " + scriptBean.getId() + " "
                        + scriptBean.getName() + " " + scriptBean.getSign() + " name: " + name);
            }

            // 只有在md5签名正常,且两次签名计算正确的情况下才跳过重新加载脚本
            if (scriptBean.getSign().equalsIgnoreCase(sign)) {
                // LOGGER.info(
                // "脚本未发生改变, 跳过初始化: " + scriptBean.getName() + " id: " + scriptBean.getId());
                scriptBean.setNotNew();
                return scriptBean;
            } else {
                LOGGER.info("脚本发生变化, 进行初始化: " + idBean.getName() + " id: " + idBean.getId() + " "
                        + idBean.getSign() + " name: " + name);
            }
        } else {
            ScriptDynamic dynamic = _scriptNameDys.get(name);
            if (dynamic != null) {
                // 只有在md5签名正常,且两次签名计算正确的情况下才跳过重新加载脚本
                if (dynamic.getSign().equalsIgnoreCase(sign)) {
                    // LOGGER.info("脚本未发生改变, 跳过初始化: " + dynamic.getName());
                    dynamic.setNotNew();
                    return dynamic;
                } else {
                    LOGGER.info("脚本发生变化, 进行初始化: " + dynamic.getName() + " " + dynamic.getSign()
                            + " name: " + name);
                }
            } else {
                LOGGER.info("init scripts: " + name);
            }
        }

        // 脚本不存在, 或者md5不一致, 需要重新加载
        Class<?> clazz = null;
        try {
            clazz = _compiler.buildScript(name, bytes);
        } catch (Exception e) {
            throw new Exception("编译脚本异常. className:" + name + "\n" + ExceptionEx.e2s(e));
        }

        if (clazz == null) {
            throw new Exception("脚本编译失败,返回class对象为null. name:" + name);
        }

        Object obj = clazz.newInstance();
        if (IDynamicScript.class.isAssignableFrom(clazz)) {
            IDynamicScript dynamic = (IDynamicScript) obj;

            return new ScriptDynamic(name, dynamic, sign);
        } else if (IScript.class.isAssignableFrom(clazz)) {
            IScript script = null;
            try {
                script = (IScript) obj;
            } catch (Exception e) {
                throw new Exception("初始化实例异常. name: " + name + ExceptionEx.e2s(e));
            }
            if (script == null) {
                throw new Exception("初始化script为null. name: " + name);
            }

            return new ScriptBean(name, script, sign);
        } else {
            throw new Exception("未知接口的脚本 name: " + name);
        }
    }

    private byte[] _readJavaSourceFile(String name) {
        String fullJavaFilePath = _javaFilePath + "/" + _parseJavaFilePathByClassname(name);

        File file = new File(fullJavaFilePath);
        if (!file.isFile() || !file.canRead()) {
            LOGGER.error("java源文件异常，可能不是一个有效的文件路径或者该文件当前不可读:" + fullJavaFilePath);
            return null;
        }

        try (InputStream stream = new FileInputStream(fullJavaFilePath)) {
            byte[] bytes = new byte[(int) file.length()];
            stream.read(bytes);
            return bytes;
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 替换已经存在的script
     * 
     * @param oldScript
     * @throws Exception
     */
    private boolean _replaceScript(AbsScriptBean oldScript) throws Exception {
        AbsScriptBean newScript = null;
        String fileName = oldScript.getName();
        if (oldScript instanceof ScriptBean || oldScript instanceof ScriptDynamic) {
            newScript = _loadScript(fileName);

            if (newScript instanceof ScriptBean) {
                ScriptBean newsb = (ScriptBean) newScript;
                ScriptBean oldsb = (ScriptBean) oldScript;
                if (newsb.getId() == 0) {
                    throw new Exception("脚本id不能为0! fileName:" + fileName);
                }

                if (oldsb.getId() != newsb.getId()) {
                    LOGGER.warn("脚本id不一致. name:" + fileName + ",oldId:" + oldsb.getId() + " newId:"
                            + newsb.getId());
                }
            }
        } else {
            LOGGER.error("未知的Script类型, name:" + oldScript.getName() + ", class:"
                    + oldScript.getClass().getName());
            return false;
        }

        if (oldScript.getSign().equalsIgnoreCase(newScript.getSign())) {
            LOGGER.warn("脚本未产生变化,替换脚本失败. name:" + fileName);
            return false;
        } else {
            _destroyOldScript(oldScript);
            _putNewScript(newScript);
            LOGGER.info("替换脚本成功. name:" + fileName);
            return true;
        }
    }

    private void _destroyOldScript(AbsScriptBean oldScript) {
        if (oldScript instanceof ScriptBean) {
            ScriptBean sb = (ScriptBean) oldScript;
            if (sb.getScript() instanceof ILifeCycle) {
                try {
                    ((ILifeCycle) sb.getScript()).destroy();
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }
        } else if (oldScript instanceof ScriptDynamic) {
            ScriptDynamic sd = (ScriptDynamic) oldScript;
            if (sd.getScript() instanceof ILifeCycle) {
                try {
                    ((ILifeCycle) sd.getScript()).destroy();
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }
        }
    }

    private boolean _putNewScript(AbsScriptBean newScript) {
        if (newScript instanceof ScriptBean) {
            ScriptBean sb = (ScriptBean) newScript;
            if (sb.getScript() instanceof ILifeCycle) {
                try {
                    ((ILifeCycle) sb.getScript()).init();
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }
            _scriptNameBeans.put(sb.getName(), sb);
            _scriptIdBeans.put(sb.getId(), sb);
            return true;
        } else if (newScript instanceof ScriptDynamic) {
            ScriptDynamic sd = (ScriptDynamic) newScript;
            if (sd.getScript() instanceof ILifeCycle) {
                try {
                    ((ILifeCycle) sd.getScript()).init();
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }
            _scriptNameDys.put(sd.getName(), sd);
            return true;
        } else {
            LOGGER.error("未知的Script类型, name:" + newScript.getName() + ", class:"
                    + newScript.getClass().getName());
            return false;
        }
    }


    /**
     * 从脚本类的全名解析脚本文件路径
     * 
     * @param name
     * @return
     */
    private String _parseJavaFilePathByClassname(String name) {
        return name.replace('.', '/') + ".java";
    }

    /**
     * 返回该路径下的所有脚本文件
     * 
     * @param javaFilePath
     * @return
     */
    private ArrayList<String> _getAllJavaFiles(String javaFilePath) {
        File file = new File(javaFilePath);
        if (!file.exists() || !file.isDirectory()) {
            LOGGER.warn("指定的脚本路径不存在：" + javaFilePath);
            return null;
        }

        ArrayList<String> list = new ArrayList<>();

        // 递归所有的文件，解析出类全名
        _processFile(file, list);

        if (list.isEmpty())
            return null;
        return list;
    }

    /**
     * 递归遍历所有文件，同时解析类全名
     * 
     * @param file
     * @param list
     */
    private void _processFile(File file, ArrayList<String> list) {
        String pkg = "";
        File[] files = file.listFiles();
        if (files == null || files.length <= 0) {
            LOGGER.error("file.listFiles 找不到脚本:" + file.getAbsolutePath());
            return;
        }

        for (int a = 0; a < files.length; ++a) {
            String filename = pkg = files[a].getName();
            if (files[a].isFile() && filename.endsWith(".java")) {
                String className = filename.substring(0, filename.indexOf("."));
                list.add(className);
            } else {
                if (files[a].isDirectory()) {
                    _processFile(files[a], pkg, list);
                }
            }
        }
    }

    /**
     * 递归遍历所有文件，同时解析类全名
     * 
     * @param file 文件名
     * @param pkg 包名
     * @param list 结果集
     */
    private void _processFile(File file, String pkg, ArrayList<String> list) {
        File[] files = file.listFiles();
        if (files == null || files.length <= 0) {
            LOGGER.warn("加载脚本子目录文件,该目录为空.path:" + file.getAbsolutePath());
            return;
        }

        for (int a = 0; a < files.length; ++a) {
            String filename = files[a].getName();
            if (files[a].isFile() && filename.endsWith(".java")) {
                String className = filename.substring(0, filename.indexOf("."));
                String classFullName = pkg + "." + className;
                list.add(classFullName);
            } else {
                String pk = pkg + "." + files[a].getName();
                _processFile(files[a], pk, list);
            }
        }
    }


    /** 是否已经初始化了 **/
    private boolean _isInitialize = false;
    /** Java脚本源文件路径 */
    private String _javaFilePath;
    private String _javaClassPath;
    /** Java脚本编译器 */
    private JavaScriptCompiler _compiler;

    // 双重引用 有点多余 代码复杂化了 也容易出错 仅通过名字管理会简单很多 暂时没有好的解决方案
    // 仅管理实现了IScripts接口的脚本
    /**
     * 已经加载进来的脚本缓存 ID -> ScriptBean
     */
    private volatile ConcurrentHashMap<Integer, ScriptBean> _scriptIdBeans =
            new ConcurrentHashMap<>();
    /**
     * 已经加载进来的脚本缓存 ClassName -> ScriptBean
     */
    private volatile ConcurrentHashMap<String, ScriptBean> _scriptNameBeans =
            new ConcurrentHashMap<String, ScriptBean>();

    private volatile ConcurrentHashMap<String, ScriptDynamic> _scriptNameDys =
            new ConcurrentHashMap<String, ScriptDynamic>();

    /** 加载锁 **/
    private volatile Object _loadLock = new Object();

    private enum Singleton {
        INSTANCE;

        ScriptManager manager;

        Singleton() {
            this.manager = new ScriptManager();
        }

        ScriptManager getManager() {
            return manager;
        }
    }

}
