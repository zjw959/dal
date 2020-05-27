/**
 * Auto generated, do not edit it
 *
 * KabalaWorld
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.KabalaWorldCfgBean;

public class KabalaWorldCfgContainer  extends BaseCfgContainer<KabalaWorldCfgBean>
{
    private final Logger log = Logger.getLogger(KabalaWorldCfgContainer.class);
    private List<KabalaWorldCfgBean> list = new ArrayList<>();
    private final Map<Integer, KabalaWorldCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "KabalaWorld";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"KabalaWorld.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"KabalaWorld.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<KabalaWorldCfgBean> list) throws IllegalAccessException {
		Iterator<KabalaWorldCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            KabalaWorldCfgBean bean = (KabalaWorldCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in KabalaWorld.csv");
	        }
	}

    public List<KabalaWorldCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, KabalaWorldCfgBean> getMap()
    {
        return map;
    }
    
    public KabalaWorldCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public KabalaWorldCfgBean createBean() {
		return new KabalaWorldCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}