package utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @UserName : 
 * @DataTime : 2013年11月26日 下午10:28:35
 * @Description ：Please describe this document
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ToolMap {
    public static boolean getBoolean(Object key, Map map) {
        Object obj = map.get(key);
        if (obj == null)
            return false;
        if (obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue();
        } else if (obj instanceof Integer) {
            return ((Integer) obj) <= 0 ? false : true;
        } else if (obj instanceof Long) {
            return ((Long) obj) <= 0 ? false : true;
        } else if (obj instanceof String) {
            return Boolean.valueOf((String) obj);
        }
        return false;
    }

    public static byte getByte(Object key, Map map) {
        Object obj = map.get(key);
        if (obj == null)
            return 0;
        if (obj instanceof Byte) {
            return ((Byte) obj).byteValue();
        } else if (obj instanceof String) {
            return Byte.valueOf((String) obj);
        }
        return 0;
    }

    public static short getShort(Object key, Map map) {
        Object obj = map.get(key);
        if (obj == null)
            return 0;
        if (obj instanceof Byte) {
            return ((Byte) obj).shortValue();
        } else if (obj instanceof Short) {
            return ((Short) obj).shortValue();
        } else if (obj instanceof Integer) {
            return ((Integer) obj).shortValue();
        } else if (obj instanceof Long) {
            return ((Long) obj).shortValue();
        } else if (obj instanceof Float) {
            return ((Float) obj).shortValue();
        } else if (obj instanceof Double) {
            return ((Double) obj).shortValue();
        } else if (obj instanceof String) {
            return Short.valueOf((String) obj);
        } else if (obj instanceof BigInteger) {
            return ((BigInteger) obj).shortValue();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).shortValue();
        }
        return 0;
    }

    public static int getInt(Object key, Map map) {
        Object obj = map.get(key);
        if (obj == null)
            return 0;
        if (obj instanceof Byte) {
            return ((Byte) obj).intValue();
        } else if (obj instanceof Short) {
            return ((Short) obj).intValue();
        } else if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else if (obj instanceof Float) {
            return ((Float) obj).intValue();
        } else if (obj instanceof Double) {
            return ((Double) obj).intValue();
        } else if (obj instanceof String) {
            return Integer.valueOf((String) obj);
        } else if (obj instanceof BigInteger) {
            return ((BigInteger) obj).intValue();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).intValue();
        }
        return 0;
    }

    public static long getLong(Object key, Map map) {
        Object obj = map.get(key);
        if (obj == null)
            return 0;
        if (obj instanceof Byte) {
            return ((Byte) obj).longValue();
        } else if (obj instanceof Short) {
            return ((Short) obj).longValue();
        } else if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        } else if (obj instanceof Long) {
            return ((Long) obj).longValue();
        } else if (obj instanceof Float) {
            return ((Float) obj).longValue();
        } else if (obj instanceof Double) {
            return ((Double) obj).longValue();
        } else if (obj instanceof String) {
            return Long.valueOf((String) obj);
        } else if (obj instanceof BigInteger) {
            return ((BigInteger) obj).longValue();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).longValue();
        }
        return 0;
    }

    public static float getFloat(Object key, Map map) {
        Object obj = map.get(key);
        if (obj == null)
            return (float) 0.0;
        if (obj instanceof Byte) {
            return ((Byte) obj).floatValue();
        } else if (obj instanceof Short) {
            return ((Short) obj).floatValue();
        } else if (obj instanceof Integer) {
            return ((Integer) obj).floatValue();
        } else if (obj instanceof Long) {
            return ((Long) obj).floatValue();
        } else if (obj instanceof Float) {
            return ((Float) obj).floatValue();
        } else if (obj instanceof Double) {
            return ((Double) obj).floatValue();
        } else if (obj instanceof String) {
            return Float.valueOf((String) obj);
        } else if (obj instanceof BigInteger) {
            return ((BigInteger) obj).floatValue();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).floatValue();
        }
        return 0;
    }

    public static double getDouble(Object key, Map map) {
        Object obj = map.get(key);
        if (obj == null)
            return 0.0;
        if (obj instanceof Byte) {
            return ((Byte) obj).doubleValue();
        } else if (obj instanceof Short) {
            return ((Short) obj).floatValue();
        } else if (obj instanceof Integer) {
            return ((Integer) obj).doubleValue();
        } else if (obj instanceof Long) {
            return ((Long) obj).doubleValue();
        } else if (obj instanceof Float) {
            return ((Float) obj).doubleValue();
        } else if (obj instanceof Double) {
            return ((Double) obj).doubleValue();
        } else if (obj instanceof String) {
            return Double.valueOf((String) obj);
        } else if (obj instanceof BigInteger) {
            return ((BigInteger) obj).doubleValue();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).doubleValue();
        }
        return 0;
    }

    public static String getString(Object key, Map map) {
        Object obj = map.get(key);
        if (obj == null)
            return "";
        else if (obj instanceof String)
            return (String) obj;

        return String.valueOf(obj);
    }

    public static Date getDate(Object key, Map map) {
        Object obj = map.get(key);
        if (obj == null)
            return null;
        if (obj instanceof Date)
            return (Date) obj;
        else if (obj instanceof java.sql.Date)
            return new Date(((java.sql.Date) obj).getTime());
        else if (obj instanceof java.sql.Timestamp)
            return new Date(((java.sql.Timestamp) obj).getTime());
        else if (obj instanceof Long)
            return new Date((Long) obj);

        return null;
    }

    public static <K, V> Map<K, V> getMap(Object key, Map map) {
        Object obj = map.get(key);
        if (obj == null)
            return new HashMap<K, V>();

        if (obj instanceof Map)
            return (Map<K, V>) obj;

        return new HashMap<K, V>();
    }

    public static <T> List<T> getList(Object key, Map map) {
        Object obj = map.get(key);
        if (obj == null)
            return new ArrayList();

        if (obj instanceof List)
            return (List) obj;

        return new ArrayList();
    }



    public static boolean getBoolean(Object key, Map map, boolean defaultValues) {
        Object obj = map.get(key);
        if (obj == null) {
            return defaultValues;
        } else {
            return getBoolean(key, map);
        }
    }

    public static byte getByte(Object key, Map map, byte defaultValues) {
        Object obj = map.get(key);
        if (obj == null) {
            return defaultValues;
        } else {
            return getByte(key, map);
        }
    }

    public static short getShort(Object key, Map map, short defaultValues) {
        Object obj = map.get(key);
        if (obj == null) {
            return defaultValues;
        } else {
            return getShort(key, map);
        }
    }

    public static int getInt(Object key, Map map, int defaultValues) {
        Object obj = map.get(key);
        if (obj == null) {
            return defaultValues;
        } else {
            return getInt(key, map);
        }
    }

    public static long getLong(Object key, Map map, long defaultValues) {
        Object obj = map.get(key);
        if (obj == null) {
            return defaultValues;
        } else {
            return getLong(key, map);
        }
    }

    public static float getFloat(Object key, Map map, float defaultValues) {
        Object obj = map.get(key);
        if (obj == null) {
            return defaultValues;
        } else {
            return getFloat(key, map);
        }
    }

    public static double getDouble(Object key, Map map, double defaultValues) {
        Object obj = map.get(key);
        if (obj == null) {
            return defaultValues;
        } else {
            return getDouble(key, map);
        }
    }

    public static String getString(Object key, Map map, String defaultValues) {
        Object obj = map.get(key);
        if (obj == null) {
            return defaultValues;
        } else {
            return getString(key, map);
        }
    }

    public static Date getDate(Object key, Map map, Date defaultValues) {
        Object obj = map.get(key);
        if (obj == null) {
            return defaultValues;
        } else {
            return getDate(key, map);
        }
    }

    public static <K, V> Map<K, V> getMap(Object key, Map map, Map defaultValues) {
        Object obj = map.get(key);
        if (obj == null) {
            return defaultValues;
        } else {
            return getMap(key, map);
        }
    }


    public static <T> List<T> getList(Object key, Map map, List defaultValues) {
        Object obj = map.get(key);
        if (obj == null) {
            return defaultValues;
        } else {
            return getList(key, map);
        }
    }

    public static <T> T getObject(Object key, Map map, Class<T> clazz) {
        return (T) map.get(key);
    }
}
