/**
 * Auto generated, do not edit it
 *
 * ItemTime
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.ItemTimeCfgBean;

public class ItemTimeCfgContainer  extends BaseCfgContainer<ItemTimeCfgBean>
{
    private final Logger log = Logger.getLogger(ItemTimeCfgContainer.class);
    private List<ItemTimeCfgBean> list = new ArrayList<>();
    private final Map<Integer, ItemTimeCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "ItemTime";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"ItemTime.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"ItemTime.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<ItemTimeCfgBean> list) throws IllegalAccessException {
		Iterator<ItemTimeCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            ItemTimeCfgBean bean = (ItemTimeCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in ItemTime.csv");
	        }
	}

    public List<ItemTimeCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, ItemTimeCfgBean> getMap()
    {
        return map;
    }
    
    public ItemTimeCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public ItemTimeCfgBean createBean() {
		return new ItemTimeCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}