/**
 * Auto generated, do not edit it
 *
 * HeroLimitforDungeon
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.HeroLimitforDungeonCfgBean;

public class HeroLimitforDungeonCfgContainer  extends BaseCfgContainer<HeroLimitforDungeonCfgBean>
{
    private final Logger log = Logger.getLogger(HeroLimitforDungeonCfgContainer.class);
    private List<HeroLimitforDungeonCfgBean> list = new ArrayList<>();
    private final Map<Integer, HeroLimitforDungeonCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "HeroLimitforDungeon";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"HeroLimitforDungeon.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"HeroLimitforDungeon.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<HeroLimitforDungeonCfgBean> list) throws IllegalAccessException {
		Iterator<HeroLimitforDungeonCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            HeroLimitforDungeonCfgBean bean = (HeroLimitforDungeonCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in HeroLimitforDungeon.csv");
	        }
	}

    public List<HeroLimitforDungeonCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, HeroLimitforDungeonCfgBean> getMap()
    {
        return map;
    }
    
    public HeroLimitforDungeonCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public HeroLimitforDungeonCfgBean createBean() {
		return new HeroLimitforDungeonCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}