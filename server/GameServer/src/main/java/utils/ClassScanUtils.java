package utils;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;

/**
 * 注解处理器
 */
public class ClassScanUtils {
    /**
     * 扫描路径配置示例"logic|server."
     * 
     * @param pkgPath
     * @return
     */
    public static Collection<Class<?>> scanPackages(String pkgPath) {
        Collection<Class<?>> classes = new HashSet<>();
        String[] pathArray = StringUtils.split(pkgPath, "|");
        for (String path : pathArray) {
            classes.addAll(PackageScanner.getClasses(path));
        }
        return classes;
    }
}
