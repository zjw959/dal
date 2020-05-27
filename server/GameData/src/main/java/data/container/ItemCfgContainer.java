/**
 * Auto generated, do not edit it
 *
 * Item
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.ItemCfgBean;

public class ItemCfgContainer  extends BaseCfgContainer<ItemCfgBean>
{
    private final Logger log = Logger.getLogger(ItemCfgContainer.class);
    private List<ItemCfgBean> list = new ArrayList<>();
    private final Map<Integer, ItemCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Item";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Item.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Item.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<ItemCfgBean> list) throws IllegalAccessException {
		Iterator<ItemCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            ItemCfgBean bean = (ItemCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Item.csv");
	        }
	}

    public List<ItemCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, ItemCfgBean> getMap()
    {
        return map;
    }
    
    public ItemCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public ItemCfgBean createBean() {
		return new ItemCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}