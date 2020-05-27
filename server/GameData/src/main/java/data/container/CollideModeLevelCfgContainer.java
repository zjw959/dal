/**
 * Auto generated, do not edit it
 *
 * CollideModeLevel
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.CollideModeLevelCfgBean;

public class CollideModeLevelCfgContainer  extends BaseCfgContainer<CollideModeLevelCfgBean>
{
    private final Logger log = Logger.getLogger(CollideModeLevelCfgContainer.class);
    private List<CollideModeLevelCfgBean> list = new ArrayList<>();
    private final Map<Integer, CollideModeLevelCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "CollideModeLevel";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"CollideModeLevel.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"CollideModeLevel.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<CollideModeLevelCfgBean> list) throws IllegalAccessException {
		Iterator<CollideModeLevelCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            CollideModeLevelCfgBean bean = (CollideModeLevelCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in CollideModeLevel.csv");
	        }
	}

    public List<CollideModeLevelCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, CollideModeLevelCfgBean> getMap()
    {
        return map;
    }
    
    public CollideModeLevelCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public CollideModeLevelCfgBean createBean() {
		return new CollideModeLevelCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}