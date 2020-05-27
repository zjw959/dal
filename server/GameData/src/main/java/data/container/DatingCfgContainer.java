/**
 * Auto generated, do not edit it
 *
 * Dating
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.DatingCfgBean;

public class DatingCfgContainer  extends BaseCfgContainer<DatingCfgBean>
{
    private final Logger log = Logger.getLogger(DatingCfgContainer.class);
    private List<DatingCfgBean> list = new ArrayList<>();
    private final Map<Integer, DatingCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Dating";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Dating.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Dating.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<DatingCfgBean> list) throws IllegalAccessException {
		Iterator<DatingCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            DatingCfgBean bean = (DatingCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Dating.csv");
	        }
	}

    public List<DatingCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, DatingCfgBean> getMap()
    {
        return map;
    }
    
    public DatingCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public DatingCfgBean createBean() {
		return new DatingCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}