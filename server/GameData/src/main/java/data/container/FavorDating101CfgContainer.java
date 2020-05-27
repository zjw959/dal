/**
 * Auto generated, do not edit it
 *
 * FavorDating101
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.FavorDating101CfgBean;

public class FavorDating101CfgContainer  extends BaseCfgContainer<FavorDating101CfgBean>
{
    private final Logger log = Logger.getLogger(FavorDating101CfgContainer.class);
    private List<FavorDating101CfgBean> list = new ArrayList<>();
    private final Map<Integer, FavorDating101CfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "FavorDating101";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"FavorDating101.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"FavorDating101.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<FavorDating101CfgBean> list) throws IllegalAccessException {
		Iterator<FavorDating101CfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            FavorDating101CfgBean bean = (FavorDating101CfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in FavorDating101.csv");
	        }
	}

    public List<FavorDating101CfgBean> getList()
    {
        return list;
    }

    public Map<Integer, FavorDating101CfgBean> getMap()
    {
        return map;
    }
    
    public FavorDating101CfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public FavorDating101CfgBean createBean() {
		return new FavorDating101CfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}