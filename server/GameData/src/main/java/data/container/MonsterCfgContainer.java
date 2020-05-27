/**
 * Auto generated, do not edit it
 *
 * Monster
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.MonsterCfgBean;

public class MonsterCfgContainer  extends BaseCfgContainer<MonsterCfgBean>
{
    private final Logger log = Logger.getLogger(MonsterCfgContainer.class);
    private List<MonsterCfgBean> list = new ArrayList<>();
    private final Map<Integer, MonsterCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Monster";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Monster.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Monster.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<MonsterCfgBean> list) throws IllegalAccessException {
		Iterator<MonsterCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            MonsterCfgBean bean = (MonsterCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Monster.csv");
	        }
	}

    public List<MonsterCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, MonsterCfgBean> getMap()
    {
        return map;
    }
    
    public MonsterCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public MonsterCfgBean createBean() {
		return new MonsterCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}