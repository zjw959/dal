package script;


import org.apache.log4j.Logger;

import utils.ExceptionEx;

import java.io.*;

/**
 * func : java class loader 加载脚本用
 */
public class JavaScriptLoader extends ClassLoader {
    private static final Logger LOGGER = Logger.getLogger(JavaScriptLoader.class);

    public JavaScriptLoader(String outPutPath) {
        this.compileOutputPath = outPutPath;
    }

    /**
     * 加载脚本
     *
     * @param classname
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    public Class<?> loadClass(String classname) throws ClassNotFoundException {
        String classFilePath =
                this.compileOutputPath + "/" + classname.replace('.', '/') + ".class";
        // 查看class目录中是否存在,即使是脚本,是脚本则由自己加载
        // 递归调用支持内部类
        File file = new File(classFilePath);
        if (!file.isFile()) {
            return super.loadClass(classname);
        }

        if (!file.canRead()) {
            LOGGER.error("class文件路径指向不是一个合法的文件或者该文件当前不可读: " + classFilePath);
            return null;
        }

        // 由于脚本相互显示引用,可能该脚本已经被此类加载器加载了.
        Class<?> _class = this.findLoadedClass(classname);
        if (_class != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("_class is in this ScriptClassLoader:" + this.toString() + ",name:"
                        + classname);
            }
            return _class;
        }

        byte[] bytes = null;
        try {
            bytes = loadClassBytes(classname);
        } catch (FileNotFoundException e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return null;
        } catch (IOException e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return null;
        }
        Class<?> clazz = this.defineClass(classname, bytes, 0, bytes.length);
        LOGGER.info("loadClass succes, Obj:" + this.toString() + ", name:" + classname);
        return clazz;
    }

    /**
     * 读取字节码
     *
     * @param name
     * @return
     * @throws IOException
     */
    private byte[] loadClassBytes(String name) throws IOException {
        int readCount = 0;
        String classFileName = this.compileOutputPath + "/" + name.replace('.', '/') + ".class";
        FileInputStream in = null;
        ByteArrayOutputStream buffer = null;
        try {
            in = new FileInputStream(classFileName);
            buffer = new ByteArrayOutputStream();
            while ((readCount = in.read()) != -1) {
                buffer.write(readCount);
            }
            return buffer.toByteArray();
        } finally {
            if (in != null) {
                in.close();
            }
            if (buffer != null) {
                buffer.close();
            }
        }
    }

    /**
     * 编译后输出的路径
     */
    private String compileOutputPath;
}
