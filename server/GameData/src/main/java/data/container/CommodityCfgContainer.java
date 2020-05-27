/**
 * Auto generated, do not edit it
 *
 * Commodity
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.CommodityCfgBean;

public class CommodityCfgContainer  extends BaseCfgContainer<CommodityCfgBean>
{
    private final Logger log = Logger.getLogger(CommodityCfgContainer.class);
    private List<CommodityCfgBean> list = new ArrayList<>();
    private final Map<Integer, CommodityCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Commodity";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Commodity.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Commodity.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<CommodityCfgBean> list) throws IllegalAccessException {
		Iterator<CommodityCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            CommodityCfgBean bean = (CommodityCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Commodity.csv");
	        }
	}

    public List<CommodityCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, CommodityCfgBean> getMap()
    {
        return map;
    }
    
    public CommodityCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public CommodityCfgBean createBean() {
		return new CommodityCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}