package kafka.team.action;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import kafka.team.param.g2f.TestHandlerParam;
import utils.GsonUtils;
import utils.PackageScanner;

public class TeamActionManager {

    private final static Logger LOGGER = Logger.getLogger(TeamActionManager.class);

    private static class DEFAULT {
        private static final TeamActionManager provider = new TeamActionManager();
    }

    public static TeamActionManager getDefault() {
        return DEFAULT.provider;
    }

    private Map<String, TeamActionHandler<?>> handlerMap = new HashMap<>();

    private Map<String, Type> typeMap = new HashMap<>();

    public void init()
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        LOGGER.info("begin load TeamAction.");
        Collection<Class<?>> classes = scanPackages("kafka.team.action.impl");
        int count = 0;
        for (Class<?> clazz : classes) {
            if (!TeamActionHandler.class.isAssignableFrom(clazz))
                continue;
            TeamActionHandler<?> _handler =
                    (TeamActionHandler<?>) (Class.forName(clazz.getName()).newInstance());

            Type[] types = _handler.getClass().getGenericInterfaces();
            ParameterizedType pType = (ParameterizedType) types[0];
            Type[] params = pType.getActualTypeArguments();

            String paramName = params[0].getTypeName();
            if (handlerMap.get(paramName) != null || typeMap.get(paramName) != null) {
                LOGGER.fatal("TeamActionHandler paramName isExist!" + paramName);
                System.exit(0);
                return;
            }
            
            handlerMap.put(paramName, _handler);
            typeMap.put(paramName, params[0]);

            count++;
        }
        LOGGER.info("end load TeamAction. count:" + count);
    }

    public void process(String json) {
        TeamActionParam _param = GsonUtils.fromJson(json, TeamActionParam.class);
        TeamActionHandler<?> _handler = handlerMap.get(_param.getParamName());
        _handler.process(
                GsonUtils.fromJson(_param.getParam(), typeMap.get(_param.getParamName())));
    }

    public <T> String makeParam(T t) throws InstantiationException, IllegalAccessException {
        TeamActionParam _pa = new TeamActionParam();
        _pa.setParamName(t.getClass().getName());
        _pa.setParam(GsonUtils.toJson(t));
        String json = GsonUtils.toJson(_pa);
        return json;
    }

    private static Collection<Class<?>> scanPackages(String pkgPath) {
        Collection<Class<?>> classes = new HashSet<>();
        String[] pathArray = StringUtils.split(pkgPath, "|");
        for (String path : pathArray) {
            classes.addAll(PackageScanner.getClasses(path));
        }
        return classes;
    }

    public static void main(String[] args)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        TeamActionManager.getDefault().init();
        TestHandlerParam _param = new TestHandlerParam();
        _param.setTest("testaa");
        String json = TeamActionManager.getDefault().makeParam(_param);
        TeamActionManager.getDefault().process(json);
    }
}
