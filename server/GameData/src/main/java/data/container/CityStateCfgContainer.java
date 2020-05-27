/**
 * Auto generated, do not edit it
 *
 * CityState
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.CityStateCfgBean;

public class CityStateCfgContainer  extends BaseCfgContainer<CityStateCfgBean>
{
    private final Logger log = Logger.getLogger(CityStateCfgContainer.class);
    private List<CityStateCfgBean> list = new ArrayList<>();
    private final Map<Integer, CityStateCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "CityState";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"CityState.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"CityState.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<CityStateCfgBean> list) throws IllegalAccessException {
		Iterator<CityStateCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            CityStateCfgBean bean = (CityStateCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in CityState.csv");
	        }
	}

    public List<CityStateCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, CityStateCfgBean> getMap()
    {
        return map;
    }
    
    public CityStateCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public CityStateCfgBean createBean() {
		return new CityStateCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}