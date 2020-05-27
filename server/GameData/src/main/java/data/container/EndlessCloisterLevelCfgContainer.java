/**
 * Auto generated, do not edit it
 *
 * EndlessCloisterLevel
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.EndlessCloisterLevelCfgBean;

public class EndlessCloisterLevelCfgContainer  extends BaseCfgContainer<EndlessCloisterLevelCfgBean>
{
    private final Logger log = Logger.getLogger(EndlessCloisterLevelCfgContainer.class);
    private List<EndlessCloisterLevelCfgBean> list = new ArrayList<>();
    private final Map<Integer, EndlessCloisterLevelCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "EndlessCloisterLevel";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"EndlessCloisterLevel.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"EndlessCloisterLevel.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<EndlessCloisterLevelCfgBean> list) throws IllegalAccessException {
		Iterator<EndlessCloisterLevelCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            EndlessCloisterLevelCfgBean bean = (EndlessCloisterLevelCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in EndlessCloisterLevel.csv");
	        }
	}

    public List<EndlessCloisterLevelCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, EndlessCloisterLevelCfgBean> getMap()
    {
        return map;
    }
    
    public EndlessCloisterLevelCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public EndlessCloisterLevelCfgBean createBean() {
		return new EndlessCloisterLevelCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}