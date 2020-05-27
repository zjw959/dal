/**
 * Auto generated, do not edit it
 *
 * Foodbase
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.FoodbaseCfgBean;

public class FoodbaseCfgContainer  extends BaseCfgContainer<FoodbaseCfgBean>
{
    private final Logger log = Logger.getLogger(FoodbaseCfgContainer.class);
    private List<FoodbaseCfgBean> list = new ArrayList<>();
    private final Map<Integer, FoodbaseCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Foodbase";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Foodbase.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Foodbase.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<FoodbaseCfgBean> list) throws IllegalAccessException {
		Iterator<FoodbaseCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            FoodbaseCfgBean bean = (FoodbaseCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Foodbase.csv");
	        }
	}

    public List<FoodbaseCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, FoodbaseCfgBean> getMap()
    {
        return map;
    }
    
    public FoodbaseCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public FoodbaseCfgBean createBean() {
		return new FoodbaseCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}