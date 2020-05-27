/**
 * Auto generated, do not edit it
 *
 * Dress
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.DressCfgBean;

public class DressCfgContainer  extends BaseCfgContainer<DressCfgBean>
{
    private final Logger log = Logger.getLogger(DressCfgContainer.class);
    private List<DressCfgBean> list = new ArrayList<>();
    private final Map<Integer, DressCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Dress";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Dress.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Dress.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<DressCfgBean> list) throws IllegalAccessException {
		Iterator<DressCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            DressCfgBean bean = (DressCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Dress.csv");
	        }
	}

    public List<DressCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, DressCfgBean> getMap()
    {
        return map;
    }
    
    public DressCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public DressCfgBean createBean() {
		return new DressCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}