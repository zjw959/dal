/**
 * Auto generated, do not edit it
 *
 * Role
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.RoleCfgBean;

public class RoleCfgContainer  extends BaseCfgContainer<RoleCfgBean>
{
    private final Logger log = Logger.getLogger(RoleCfgContainer.class);
    private List<RoleCfgBean> list = new ArrayList<>();
    private final Map<Integer, RoleCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Role";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Role.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Role.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<RoleCfgBean> list) throws IllegalAccessException {
		Iterator<RoleCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            RoleCfgBean bean = (RoleCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Role.csv");
	        }
	}

    public List<RoleCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, RoleCfgBean> getMap()
    {
        return map;
    }
    
    public RoleCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public RoleCfgBean createBean() {
		return new RoleCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}