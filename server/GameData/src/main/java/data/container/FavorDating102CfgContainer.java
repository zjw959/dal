/**
 * Auto generated, do not edit it
 *
 * FavorDating102
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.FavorDating102CfgBean;

public class FavorDating102CfgContainer  extends BaseCfgContainer<FavorDating102CfgBean>
{
    private final Logger log = Logger.getLogger(FavorDating102CfgContainer.class);
    private List<FavorDating102CfgBean> list = new ArrayList<>();
    private final Map<Integer, FavorDating102CfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "FavorDating102";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"FavorDating102.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"FavorDating102.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<FavorDating102CfgBean> list) throws IllegalAccessException {
		Iterator<FavorDating102CfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            FavorDating102CfgBean bean = (FavorDating102CfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in FavorDating102.csv");
	        }
	}

    public List<FavorDating102CfgBean> getList()
    {
        return list;
    }

    public Map<Integer, FavorDating102CfgBean> getMap()
    {
        return map;
    }
    
    public FavorDating102CfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public FavorDating102CfgBean createBean() {
		return new FavorDating102CfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}