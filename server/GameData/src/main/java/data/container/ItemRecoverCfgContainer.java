/**
 * Auto generated, do not edit it
 *
 * ItemRecover
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.ItemRecoverCfgBean;

public class ItemRecoverCfgContainer  extends BaseCfgContainer<ItemRecoverCfgBean>
{
    private final Logger log = Logger.getLogger(ItemRecoverCfgContainer.class);
    private List<ItemRecoverCfgBean> list = new ArrayList<>();
    private final Map<Integer, ItemRecoverCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "ItemRecover";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"ItemRecover.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"ItemRecover.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<ItemRecoverCfgBean> list) throws IllegalAccessException {
		Iterator<ItemRecoverCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            ItemRecoverCfgBean bean = (ItemRecoverCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in ItemRecover.csv");
	        }
	}

    public List<ItemRecoverCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, ItemRecoverCfgBean> getMap()
    {
        return map;
    }
    
    public ItemRecoverCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public ItemRecoverCfgBean createBean() {
		return new ItemRecoverCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}