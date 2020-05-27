/**
 * Auto generated, do not edit it
 *
 * ChasmDungeon
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.ChasmDungeonCfgBean;

public class ChasmDungeonCfgContainer  extends BaseCfgContainer<ChasmDungeonCfgBean>
{
    private final Logger log = Logger.getLogger(ChasmDungeonCfgContainer.class);
    private List<ChasmDungeonCfgBean> list = new ArrayList<>();
    private final Map<Integer, ChasmDungeonCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "ChasmDungeon";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"ChasmDungeon.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"ChasmDungeon.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<ChasmDungeonCfgBean> list) throws IllegalAccessException {
		Iterator<ChasmDungeonCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            ChasmDungeonCfgBean bean = (ChasmDungeonCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in ChasmDungeon.csv");
	        }
	}

    public List<ChasmDungeonCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, ChasmDungeonCfgBean> getMap()
    {
        return map;
    }
    
    public ChasmDungeonCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public ChasmDungeonCfgBean createBean() {
		return new ChasmDungeonCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}