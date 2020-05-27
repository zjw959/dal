/**
 * Auto generated, do not edit it
 *
 * 基础容器类
 */
package data.container;

import java.util.Map; import java.util.List; import org.apache.log4j.Logger; 
import java.util.ArrayList; import java.lang.reflect.Method; import 
java.text.DateFormat; import java.util.Date; import java.text.SimpleDateFormat; 
import java.text.ParseException; import com.alibaba.fastjson.JSON; import 
org.apache.commons.lang.BooleanUtils; import 
org.apache.commons.lang.math.NumberUtils;


public abstract class BaseCfgContainer<T>
{
	public abstract T getBean(int id);
	protected abstract T createBean();
	public abstract Logger getLogger();
	public List<T> loadCsv(String path) throws Exception {
		// 需要判定当前配置文件是否存在,若为必须加载则提示错误信息,终止服务器继续启动
		java.io.File csvFile = new java.io.File(path);
		if(!csvFile.exists())
			throw new Exception("Csv file not exists : " + path);
		List<T> list = new ArrayList<T>();
		com.csvreader.CsvReader csvReader = new com.csvreader.CsvReader(path,'	', java.nio.charset.Charset.forName("UTF-8"));
		// 跳过表头
		csvReader.readHeaders();
		String[] columnnames = csvReader.readRecord() ? csvReader.getValues() : null;
		csvReader.readRecord();
//		String[] luaTypes = csvReader.readRecord() ? csvReader.getValues() : null;
		String[] basicTypes = csvReader.readRecord() ? csvReader.getValues() : null;
		int line = 4;
		while (csvReader.readRecord()){
			line++;
            // 读一整行
//			String[] contents = csvReader.getValues();
			String[] contents = csvReader.getRawRecord().split("	");
			StringBuffer sb = new StringBuffer();
			T cfgBean = createBean();
			Method[] methods = cfgBean.getClass().getMethods();
			boolean emptyBean = true;
			for (int i = 0; i < basicTypes.length; i++) {
				try {
					sb.setLength(0);
					// 服务器不需要的字段
					if (isBlank(basicTypes[i])) {
						continue;
					}
					String columnname = columnnames[i].trim();
					sb.append("文件 = " + subSuf(path, path.lastIndexOf("/")));
					sb.append(" 行数 = " + line);
					sb.append(" 字段名 = " + columnname);
					String values = null;
					if (i<contents.length) {
						values = contents[i].trim();	
					}
					sb.append(" 值 = " + values);
					if(isEmpty(columnname))
						continue;
					if(isEmpty(values))
						continue;
					// 组装方法名
					String methodName = "set" + getCamelCaseFieldName(false, columnname);
					boolean methodExists = false;
					// 对字段存在的检测,新增类型不影响当前加载
					for (Method method : methods) {
					if (!method.getName().equals(methodName))
						continue;
						methodExists = true;
						break;
					}
					if (!methodExists)
						continue;
					Object val = null;
					Class<?> filedType = null;
					String basicType = basicTypes[i].toLowerCase();
					if ("short".equals(basicType)) {
						val = NumberUtils.toShort(values, (short)0);
						filedType = short.class;
					}
					else if ("int".equals(basicType)) {
						val = NumberUtils.toInt(values, 0);
						filedType = int.class;
					}
					else if ("long".equals(basicType)) {
						val = NumberUtils.toLong(values, 0L);
						filedType = long.class;
					}
					else if ("float".equals(basicType)) {
						val = NumberUtils.toFloat(values, 0F);
						filedType = float.class;
					}
					else if ("double".equals(basicType)) {
						val = NumberUtils.toDouble(values, 0d);
						filedType = double.class;
					}
					else if ("boolean".equals(basicType)) {
//						val = Boolean.parseBoolean(values);
						if (NumberUtils.isNumber(values)) {
							val = BooleanUtils.toBoolean(Integer.valueOf(values));
						}else {
							val = BooleanUtils.toBoolean(values);						
						}
						filedType = boolean.class;
					}
					else if ("date".equals(basicType))
						val = formatDate("yyyy-MM-dd HH:mm:ss", values);
					else if ("list".equals(basicType)){
						values = "".equals(values) ? "[]" : values;
						val = JSON.parseObject(values, List.class);
						filedType = List.class;
					}else if ("map".equals(basicType)){
						values = "".equals(values) ? "{}" : values;
						val = JSON.parseObject(values, Map.class);
						filedType = Map.class;
					}else if (basicType.endsWith("int[]")){
						val = StringToIntArray(values, "-");
//						values = "".equals(values) ? "[]" : "["+values+"]";
//						val = JSON.parseObject(values, int[].class);
					}else if (basicType.endsWith("string[]")){
						val = StringToStrArray(values, "-");
//						values = "".equals(values) ? "[]" : "["+values+"]";
//						val = JSON.parseObject(values, String[].class);
					}else if (basicType.endsWith("float[]")){
						val = StringToFloatArray(values, "-");
//						values = "".equals(values) ? "[]" : "["+values+"]";
//						val = JSON.parseObject(values, float[].class);
					}else if (basicType.endsWith("double[]")){
						val = StringToDoubleArray(values, "-");
//						values = "".equals(values) ? "[]" : "["+values+"]";
//						val = JSON.parseObject(values, double[].class);
					}else{
						val = values;
					}
					Method method = null;
					method = cfgBean.getClass().getMethod(methodName, filedType == null ? val.getClass() : filedType);
					method.invoke(cfgBean, val);
					emptyBean = false;
				} catch (Exception e) {
					Logger log = getLogger();
					if(log !=null)
						log.error(sb.toString(), e);
					throw new Exception(sb.toString(), e);
				}
			}
			if(!emptyBean)
				list.add(cfgBean);
        }
        return list;
	}

	/**
	 * 驼峰命名法
	 * @param littleCamelCase 是否小驼峰命名(首字小写),否则首字大写
	 * @param sourceName 源名称
	 * @return 驼峰命名
	 */
	public static String getCamelCaseFieldName(boolean littleCamelCase, String sourceName) {
		String[] splits = sourceName.split("_");
		if (splits == null || splits.length < 1)
			return sourceName;
		String result;
		// 处理首字
		if(littleCamelCase)
			result = firstToLower(splits[0]);
		else
			result = firstToUpper(splits[0]);
		// 后续处理
		for (int i = 1; i < splits.length; i++)
			result += firstToUpper(splits[i]);
		return result;
	}

	public static boolean isBlank(CharSequence str) {
		return str == null || str.length() == 0;
	}
	
	/**
	 * 切割后部分
	 * 
	 * @param string 字符串
	 * @param fromIndex 切割开始的位置（包括）
	 * @return 切割后的字符串
	 */
	public static String subSuf(CharSequence string, int fromIndex) {
		if (isEmpty(string)) {
			return null;
		}
		return sub(string, fromIndex, string.length());
	}
	/**
	 * 字符串是否为空，空的定义如下 1、为null <br>
	 * 2、为""<br>
	 * 
	 * @param str 被检测的字符串
	 * @return 是否为空
	 */
	public static boolean isEmpty(CharSequence str) {
		return str == null || str.length() == 0;
	}
	/**
	 * 改进JDK subString<br>
	 * index从0开始计算，最后一个字符为-1<br>
	 * 如果from和to位置一样，返回 "" <br>
	 * 如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length<br>
	 * 如果经过修正的index中from大于to，则互换from和to example: <br>
	 * abcdefgh 2 3 =》 c <br>
	 * abcdefgh 2 -3 =》 cde <br>
	 * 
	 * @param string String
	 * @param fromIndex 开始的index（包括）
	 * @param toIndex 结束的index（不包括）
	 * @return 字串
	 */
	public static String sub(CharSequence string, int fromIndex, int toIndex) {
		int len = string.length();

		if (fromIndex < 0) {
			fromIndex = len + fromIndex;
			if (fromIndex < 0) {
				fromIndex = 0;
			}
		} else if (fromIndex > len) {
			fromIndex = len;
		}

		if (toIndex < 0) {
			toIndex = len + toIndex;
			if (toIndex < 0) {
				toIndex = len;
			}
		} else if (toIndex > len) {
			toIndex = len;
		}

		if (toIndex < fromIndex) {
			int tmp = fromIndex;
			fromIndex = toIndex;
			toIndex = tmp;
		}

		if (fromIndex == toIndex) {
			return "";
		}

		return string.toString().substring(fromIndex, toIndex);
	}
	
	/**
	 * 字符串转时间
	 * 
	 * @param dateStr
	 * @param formatStr
	 * @return
	 */
	public static Date formatDate(String dateStr, String formatStr) throws ParseException {
		DateFormat dd = new SimpleDateFormat(formatStr);
		Date date = null;
		date = dd.parse(dateStr);
		return date;
	}
	
	/**
	 * 字符串转Int数组
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	public static int[] StringToIntArray(String str, String regex) {
		String[] array = StringToStrArray(str, regex);
		if(array == null) return null;
		int[] v = new int[array.length];
		for (int j = 0; j < array.length; j++) {
			v[j] = Integer.valueOf(array[j]);
		}
		return v;
	}
	public static String[] StringToStrArray(String str, String regex) {
		if(str == null) return null;
		return str.split(regex);
	}
	public static float[] StringToFloatArray(String str, String regex) {
		String[] array = StringToStrArray(str, regex);
		if(array == null) return null;
		float[] v = new float[array.length];
		for (int j = 0; j < array.length; j++) {
			v[j] = Float.valueOf(array[j]);
		}
		return v;
	}
	public static double[] StringToDoubleArray(String str, String regex) {
		String[] array = StringToStrArray(str, regex);
		if(array == null) return null;
		double[] v = new double[array.length];
		for (int j = 0; j < array.length; j++) {
			v[j] = Double.valueOf(array[j]);
		}
		return v;
	}

	public static String firstToLower(String str) {
		char[] nameChars = str.toCharArray();
		return new String(nameChars, 0, 1).toLowerCase() + new String(nameChars, 1, nameChars.length - 1);
	}
	
	public static String firstToUpper(String str) {
		char[] nameChars = str.toCharArray();
		return new String(nameChars, 0, 1).toUpperCase() + new String(nameChars, 1, nameChars.length - 1);
	}
}