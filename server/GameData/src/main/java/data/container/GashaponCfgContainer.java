/**
 * Auto generated, do not edit it
 *
 * Gashapon
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.GashaponCfgBean;

public class GashaponCfgContainer  extends BaseCfgContainer<GashaponCfgBean>
{
    private final Logger log = Logger.getLogger(GashaponCfgContainer.class);
    private List<GashaponCfgBean> list = new ArrayList<>();
    private final Map<Integer, GashaponCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Gashapon";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Gashapon.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Gashapon.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<GashaponCfgBean> list) throws IllegalAccessException {
		Iterator<GashaponCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            GashaponCfgBean bean = (GashaponCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Gashapon.csv");
	        }
	}

    public List<GashaponCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, GashaponCfgBean> getMap()
    {
        return map;
    }
    
    public GashaponCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public GashaponCfgBean createBean() {
		return new GashaponCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}