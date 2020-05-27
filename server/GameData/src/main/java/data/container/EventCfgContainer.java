/**
 * Auto generated, do not edit it
 *
 * Event
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.EventCfgBean;

public class EventCfgContainer  extends BaseCfgContainer<EventCfgBean>
{
    private final Logger log = Logger.getLogger(EventCfgContainer.class);
    private List<EventCfgBean> list = new ArrayList<>();
    private final Map<Integer, EventCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Event";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Event.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Event.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<EventCfgBean> list) throws IllegalAccessException {
		Iterator<EventCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            EventCfgBean bean = (EventCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Event.csv");
	        }
	}

    public List<EventCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, EventCfgBean> getMap()
    {
        return map;
    }
    
    public EventCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public EventCfgBean createBean() {
		return new EventCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}