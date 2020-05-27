/**
 * Auto generated, do not edit it
 *
 * FavorDating103
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.FavorDating103CfgBean;

public class FavorDating103CfgContainer  extends BaseCfgContainer<FavorDating103CfgBean>
{
    private final Logger log = Logger.getLogger(FavorDating103CfgContainer.class);
    private List<FavorDating103CfgBean> list = new ArrayList<>();
    private final Map<Integer, FavorDating103CfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "FavorDating103";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"FavorDating103.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"FavorDating103.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<FavorDating103CfgBean> list) throws IllegalAccessException {
		Iterator<FavorDating103CfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            FavorDating103CfgBean bean = (FavorDating103CfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in FavorDating103.csv");
	        }
	}

    public List<FavorDating103CfgBean> getList()
    {
        return list;
    }

    public Map<Integer, FavorDating103CfgBean> getMap()
    {
        return map;
    }
    
    public FavorDating103CfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public FavorDating103CfgBean createBean() {
		return new FavorDating103CfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}