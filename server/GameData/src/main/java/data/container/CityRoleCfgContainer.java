/**
 * Auto generated, do not edit it
 *
 * CityRole
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.CityRoleCfgBean;

public class CityRoleCfgContainer  extends BaseCfgContainer<CityRoleCfgBean>
{
    private final Logger log = Logger.getLogger(CityRoleCfgContainer.class);
    private List<CityRoleCfgBean> list = new ArrayList<>();
    private final Map<Integer, CityRoleCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "CityRole";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"CityRole.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"CityRole.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<CityRoleCfgBean> list) throws IllegalAccessException {
		Iterator<CityRoleCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            CityRoleCfgBean bean = (CityRoleCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in CityRole.csv");
	        }
	}

    public List<CityRoleCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, CityRoleCfgBean> getMap()
    {
        return map;
    }
    
    public CityRoleCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public CityRoleCfgBean createBean() {
		return new CityRoleCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}