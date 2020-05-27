/**
 * Auto generated, do not edit it
 *
 * LevelUp
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.LevelUpCfgBean;

public class LevelUpCfgContainer  extends BaseCfgContainer<LevelUpCfgBean>
{
    private final Logger log = Logger.getLogger(LevelUpCfgContainer.class);
    private List<LevelUpCfgBean> list = new ArrayList<>();
    private final Map<Integer, LevelUpCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "LevelUp";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"LevelUp.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"LevelUp.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<LevelUpCfgBean> list) throws IllegalAccessException {
		Iterator<LevelUpCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            LevelUpCfgBean bean = (LevelUpCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in LevelUp.csv");
	        }
	}

    public List<LevelUpCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, LevelUpCfgBean> getMap()
    {
        return map;
    }
    
    public LevelUpCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public LevelUpCfgBean createBean() {
		return new LevelUpCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}