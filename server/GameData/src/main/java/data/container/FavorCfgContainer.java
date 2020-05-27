/**
 * Auto generated, do not edit it
 *
 * Favor
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.FavorCfgBean;

public class FavorCfgContainer  extends BaseCfgContainer<FavorCfgBean>
{
    private final Logger log = Logger.getLogger(FavorCfgContainer.class);
    private List<FavorCfgBean> list = new ArrayList<>();
    private final Map<Integer, FavorCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Favor";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Favor.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Favor.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<FavorCfgBean> list) throws IllegalAccessException {
		Iterator<FavorCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            FavorCfgBean bean = (FavorCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Favor.csv");
	        }
	}

    public List<FavorCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, FavorCfgBean> getMap()
    {
        return map;
    }
    
    public FavorCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public FavorCfgBean createBean() {
		return new FavorCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}