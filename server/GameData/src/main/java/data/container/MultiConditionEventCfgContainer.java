/**
 * Auto generated, do not edit it
 *
 * MultiConditionEvent
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.MultiConditionEventCfgBean;

public class MultiConditionEventCfgContainer  extends BaseCfgContainer<MultiConditionEventCfgBean>
{
    private final Logger log = Logger.getLogger(MultiConditionEventCfgContainer.class);
    private List<MultiConditionEventCfgBean> list = new ArrayList<>();
    private final Map<Integer, MultiConditionEventCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "MultiConditionEvent";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"MultiConditionEvent.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"MultiConditionEvent.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<MultiConditionEventCfgBean> list) throws IllegalAccessException {
		Iterator<MultiConditionEventCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            MultiConditionEventCfgBean bean = (MultiConditionEventCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in MultiConditionEvent.csv");
	        }
	}

    public List<MultiConditionEventCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, MultiConditionEventCfgBean> getMap()
    {
        return map;
    }
    
    public MultiConditionEventCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public MultiConditionEventCfgBean createBean() {
		return new MultiConditionEventCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}