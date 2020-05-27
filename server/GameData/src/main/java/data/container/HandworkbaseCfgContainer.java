/**
 * Auto generated, do not edit it
 *
 * Handworkbase
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.HandworkbaseCfgBean;

public class HandworkbaseCfgContainer  extends BaseCfgContainer<HandworkbaseCfgBean>
{
    private final Logger log = Logger.getLogger(HandworkbaseCfgContainer.class);
    private List<HandworkbaseCfgBean> list = new ArrayList<>();
    private final Map<Integer, HandworkbaseCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Handworkbase";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Handworkbase.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Handworkbase.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<HandworkbaseCfgBean> list) throws IllegalAccessException {
		Iterator<HandworkbaseCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            HandworkbaseCfgBean bean = (HandworkbaseCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Handworkbase.csv");
	        }
	}

    public List<HandworkbaseCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, HandworkbaseCfgBean> getMap()
    {
        return map;
    }
    
    public HandworkbaseCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public HandworkbaseCfgBean createBean() {
		return new HandworkbaseCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}