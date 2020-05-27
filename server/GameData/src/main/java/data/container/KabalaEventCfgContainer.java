/**
 * Auto generated, do not edit it
 *
 * KabalaEvent
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.KabalaEventCfgBean;

public class KabalaEventCfgContainer  extends BaseCfgContainer<KabalaEventCfgBean>
{
    private final Logger log = Logger.getLogger(KabalaEventCfgContainer.class);
    private List<KabalaEventCfgBean> list = new ArrayList<>();
    private final Map<Integer, KabalaEventCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "KabalaEvent";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"KabalaEvent.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"KabalaEvent.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<KabalaEventCfgBean> list) throws IllegalAccessException {
		Iterator<KabalaEventCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            KabalaEventCfgBean bean = (KabalaEventCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in KabalaEvent.csv");
	        }
	}

    public List<KabalaEventCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, KabalaEventCfgBean> getMap()
    {
        return map;
    }
    
    public KabalaEventCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public KabalaEventCfgBean createBean() {
		return new KabalaEventCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}