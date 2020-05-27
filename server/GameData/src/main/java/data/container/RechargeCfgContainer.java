/**
 * Auto generated, do not edit it
 *
 * Recharge
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.RechargeCfgBean;

public class RechargeCfgContainer  extends BaseCfgContainer<RechargeCfgBean>
{
    private final Logger log = Logger.getLogger(RechargeCfgContainer.class);
    private List<RechargeCfgBean> list = new ArrayList<>();
    private final Map<Integer, RechargeCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Recharge";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Recharge.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Recharge.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<RechargeCfgBean> list) throws IllegalAccessException {
		Iterator<RechargeCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            RechargeCfgBean bean = (RechargeCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Recharge.csv");
	        }
	}

    public List<RechargeCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, RechargeCfgBean> getMap()
    {
        return map;
    }
    
    public RechargeCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public RechargeCfgBean createBean() {
		return new RechargeCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}