/**
 * Auto generated, do not edit it
 *
 * FavorDating104
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.FavorDating104CfgBean;

public class FavorDating104CfgContainer  extends BaseCfgContainer<FavorDating104CfgBean>
{
    private final Logger log = Logger.getLogger(FavorDating104CfgContainer.class);
    private List<FavorDating104CfgBean> list = new ArrayList<>();
    private final Map<Integer, FavorDating104CfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "FavorDating104";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"FavorDating104.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"FavorDating104.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<FavorDating104CfgBean> list) throws IllegalAccessException {
		Iterator<FavorDating104CfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            FavorDating104CfgBean bean = (FavorDating104CfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in FavorDating104.csv");
	        }
	}

    public List<FavorDating104CfgBean> getList()
    {
        return list;
    }

    public Map<Integer, FavorDating104CfgBean> getMap()
    {
        return map;
    }
    
    public FavorDating104CfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public FavorDating104CfgBean createBean() {
		return new FavorDating104CfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}