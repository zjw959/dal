/**
 * Auto generated, do not edit it
 *
 * EventCondition
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.EventConditionCfgBean;

public class EventConditionCfgContainer  extends BaseCfgContainer<EventConditionCfgBean>
{
    private final Logger log = Logger.getLogger(EventConditionCfgContainer.class);
    private List<EventConditionCfgBean> list = new ArrayList<>();
    private final Map<Integer, EventConditionCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "EventCondition";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"EventCondition.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"EventCondition.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<EventConditionCfgBean> list) throws IllegalAccessException {
		Iterator<EventConditionCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            EventConditionCfgBean bean = (EventConditionCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in EventCondition.csv");
	        }
	}

    public List<EventConditionCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, EventConditionCfgBean> getMap()
    {
        return map;
    }
    
    public EventConditionCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public EventConditionCfgBean createBean() {
		return new EventConditionCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}