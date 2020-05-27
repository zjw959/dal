/**
 * Auto generated, do not edit it
 *
 * FavorEnd
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.FavorEndCfgBean;

public class FavorEndCfgContainer  extends BaseCfgContainer<FavorEndCfgBean>
{
    private final Logger log = Logger.getLogger(FavorEndCfgContainer.class);
    private List<FavorEndCfgBean> list = new ArrayList<>();
    private final Map<Integer, FavorEndCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "FavorEnd";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"FavorEnd.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"FavorEnd.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<FavorEndCfgBean> list) throws IllegalAccessException {
		Iterator<FavorEndCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            FavorEndCfgBean bean = (FavorEndCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in FavorEnd.csv");
	        }
	}

    public List<FavorEndCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, FavorEndCfgBean> getMap()
    {
        return map;
    }
    
    public FavorEndCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public FavorEndCfgBean createBean() {
		return new FavorEndCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}