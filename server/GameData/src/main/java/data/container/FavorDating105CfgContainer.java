/**
 * Auto generated, do not edit it
 *
 * FavorDating105
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.FavorDating105CfgBean;

public class FavorDating105CfgContainer  extends BaseCfgContainer<FavorDating105CfgBean>
{
    private final Logger log = Logger.getLogger(FavorDating105CfgContainer.class);
    private List<FavorDating105CfgBean> list = new ArrayList<>();
    private final Map<Integer, FavorDating105CfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "FavorDating105";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"FavorDating105.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"FavorDating105.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<FavorDating105CfgBean> list) throws IllegalAccessException {
		Iterator<FavorDating105CfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            FavorDating105CfgBean bean = (FavorDating105CfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in FavorDating105.csv");
	        }
	}

    public List<FavorDating105CfgBean> getList()
    {
        return list;
    }

    public Map<Integer, FavorDating105CfgBean> getMap()
    {
        return map;
    }
    
    public FavorDating105CfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public FavorDating105CfgBean createBean() {
		return new FavorDating105CfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}