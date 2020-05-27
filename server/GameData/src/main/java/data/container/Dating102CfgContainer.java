/**
 * Auto generated, do not edit it
 *
 * Dating102
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.Dating102CfgBean;

public class Dating102CfgContainer  extends BaseCfgContainer<Dating102CfgBean>
{
    private final Logger log = Logger.getLogger(Dating102CfgContainer.class);
    private List<Dating102CfgBean> list = new ArrayList<>();
    private final Map<Integer, Dating102CfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Dating102";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Dating102.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Dating102.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<Dating102CfgBean> list) throws IllegalAccessException {
		Iterator<Dating102CfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            Dating102CfgBean bean = (Dating102CfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Dating102.csv");
	        }
	}

    public List<Dating102CfgBean> getList()
    {
        return list;
    }

    public Map<Integer, Dating102CfgBean> getMap()
    {
        return map;
    }
    
    public Dating102CfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public Dating102CfgBean createBean() {
		return new Dating102CfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}