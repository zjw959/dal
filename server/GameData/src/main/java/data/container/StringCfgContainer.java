/**
 * Auto generated, do not edit it
 *
 * String
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.StringCfgBean;

public class StringCfgContainer  extends BaseCfgContainer<StringCfgBean>
{
    private final Logger log = Logger.getLogger(StringCfgContainer.class);
    private List<StringCfgBean> list = new ArrayList<>();
    private final Map<Integer, StringCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "String";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"String.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"String.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<StringCfgBean> list) throws IllegalAccessException {
		Iterator<StringCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            StringCfgBean bean = (StringCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in String.csv");
	        }
	}

    public List<StringCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, StringCfgBean> getMap()
    {
        return map;
    }
    
    public StringCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public StringCfgBean createBean() {
		return new StringCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}