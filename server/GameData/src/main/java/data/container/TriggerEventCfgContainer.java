/**
 * Auto generated, do not edit it
 *
 * TriggerEvent
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.TriggerEventCfgBean;

public class TriggerEventCfgContainer  extends BaseCfgContainer<TriggerEventCfgBean>
{
    private final Logger log = Logger.getLogger(TriggerEventCfgContainer.class);
    private List<TriggerEventCfgBean> list = new ArrayList<>();
    private final Map<Integer, TriggerEventCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "TriggerEvent";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"TriggerEvent.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"TriggerEvent.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<TriggerEventCfgBean> list) throws IllegalAccessException {
		Iterator<TriggerEventCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            TriggerEventCfgBean bean = (TriggerEventCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in TriggerEvent.csv");
	        }
	}

    public List<TriggerEventCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, TriggerEventCfgBean> getMap()
    {
        return map;
    }
    
    public TriggerEventCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public TriggerEventCfgBean createBean() {
		return new TriggerEventCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}