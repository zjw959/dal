/**
 * Auto generated, do not edit it
 *
 * GrowUp
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.GrowUpCfgBean;

public class GrowUpCfgContainer  extends BaseCfgContainer<GrowUpCfgBean>
{
    private final Logger log = Logger.getLogger(GrowUpCfgContainer.class);
    private List<GrowUpCfgBean> list = new ArrayList<>();
    private final Map<Integer, GrowUpCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "GrowUp";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"GrowUp.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"GrowUp.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<GrowUpCfgBean> list) throws IllegalAccessException {
		Iterator<GrowUpCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            GrowUpCfgBean bean = (GrowUpCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in GrowUp.csv");
	        }
	}

    public List<GrowUpCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, GrowUpCfgBean> getMap()
    {
        return map;
    }
    
    public GrowUpCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public GrowUpCfgBean createBean() {
		return new GrowUpCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}