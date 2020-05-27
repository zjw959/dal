/**
 * Auto generated, do not edit it
 *
 * DungeonLevelGroup
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.DungeonLevelGroupCfgBean;

public class DungeonLevelGroupCfgContainer  extends BaseCfgContainer<DungeonLevelGroupCfgBean>
{
    private final Logger log = Logger.getLogger(DungeonLevelGroupCfgContainer.class);
    private List<DungeonLevelGroupCfgBean> list = new ArrayList<>();
    private final Map<Integer, DungeonLevelGroupCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "DungeonLevelGroup";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"DungeonLevelGroup.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"DungeonLevelGroup.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<DungeonLevelGroupCfgBean> list) throws IllegalAccessException {
		Iterator<DungeonLevelGroupCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            DungeonLevelGroupCfgBean bean = (DungeonLevelGroupCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in DungeonLevelGroup.csv");
	        }
	}

    public List<DungeonLevelGroupCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, DungeonLevelGroupCfgBean> getMap()
    {
        return map;
    }
    
    public DungeonLevelGroupCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public DungeonLevelGroupCfgBean createBean() {
		return new DungeonLevelGroupCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}