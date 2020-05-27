/**
 * Auto generated, do not edit it
 *
 * DungeonLevel
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.DungeonLevelCfgBean;

public class DungeonLevelCfgContainer  extends BaseCfgContainer<DungeonLevelCfgBean>
{
    private final Logger log = Logger.getLogger(DungeonLevelCfgContainer.class);
    private List<DungeonLevelCfgBean> list = new ArrayList<>();
    private final Map<Integer, DungeonLevelCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "DungeonLevel";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"DungeonLevel.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"DungeonLevel.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<DungeonLevelCfgBean> list) throws IllegalAccessException {
		Iterator<DungeonLevelCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            DungeonLevelCfgBean bean = (DungeonLevelCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in DungeonLevel.csv");
	        }
	}

    public List<DungeonLevelCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, DungeonLevelCfgBean> getMap()
    {
        return map;
    }
    
    public DungeonLevelCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public DungeonLevelCfgBean createBean() {
		return new DungeonLevelCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}