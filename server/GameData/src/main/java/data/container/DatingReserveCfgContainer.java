/**
 * Auto generated, do not edit it
 *
 * DatingReserve
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.DatingReserveCfgBean;

public class DatingReserveCfgContainer  extends BaseCfgContainer<DatingReserveCfgBean>
{
    private final Logger log = Logger.getLogger(DatingReserveCfgContainer.class);
    private List<DatingReserveCfgBean> list = new ArrayList<>();
    private final Map<Integer, DatingReserveCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "DatingReserve";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"DatingReserve.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"DatingReserve.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<DatingReserveCfgBean> list) throws IllegalAccessException {
		Iterator<DatingReserveCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            DatingReserveCfgBean bean = (DatingReserveCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in DatingReserve.csv");
	        }
	}

    public List<DatingReserveCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, DatingReserveCfgBean> getMap()
    {
        return map;
    }
    
    public DatingReserveCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public DatingReserveCfgBean createBean() {
		return new DatingReserveCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}