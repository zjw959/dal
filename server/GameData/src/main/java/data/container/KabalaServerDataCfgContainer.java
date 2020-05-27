/**
 * Auto generated, do not edit it
 *
 * KabalaServerData
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.KabalaServerDataCfgBean;

public class KabalaServerDataCfgContainer  extends BaseCfgContainer<KabalaServerDataCfgBean>
{
    private final Logger log = Logger.getLogger(KabalaServerDataCfgContainer.class);
    private List<KabalaServerDataCfgBean> list = new ArrayList<>();
    private final Map<Integer, KabalaServerDataCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "KabalaServerData";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"KabalaServerData.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"KabalaServerData.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<KabalaServerDataCfgBean> list) throws IllegalAccessException {
		Iterator<KabalaServerDataCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            KabalaServerDataCfgBean bean = (KabalaServerDataCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in KabalaServerData.csv");
	        }
	}

    public List<KabalaServerDataCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, KabalaServerDataCfgBean> getMap()
    {
        return map;
    }
    
    public KabalaServerDataCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public KabalaServerDataCfgBean createBean() {
		return new KabalaServerDataCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}