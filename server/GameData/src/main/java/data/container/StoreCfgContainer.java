/**
 * Auto generated, do not edit it
 *
 * Store
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.StoreCfgBean;

public class StoreCfgContainer  extends BaseCfgContainer<StoreCfgBean>
{
    private final Logger log = Logger.getLogger(StoreCfgContainer.class);
    private List<StoreCfgBean> list = new ArrayList<>();
    private final Map<Integer, StoreCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Store";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Store.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Store.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<StoreCfgBean> list) throws IllegalAccessException {
		Iterator<StoreCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            StoreCfgBean bean = (StoreCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Store.csv");
	        }
	}

    public List<StoreCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, StoreCfgBean> getMap()
    {
        return map;
    }
    
    public StoreCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public StoreCfgBean createBean() {
		return new StoreCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}