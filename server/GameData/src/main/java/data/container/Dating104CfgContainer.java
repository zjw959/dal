/**
 * Auto generated, do not edit it
 *
 * Dating104
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.Dating104CfgBean;

public class Dating104CfgContainer  extends BaseCfgContainer<Dating104CfgBean>
{
    private final Logger log = Logger.getLogger(Dating104CfgContainer.class);
    private List<Dating104CfgBean> list = new ArrayList<>();
    private final Map<Integer, Dating104CfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Dating104";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Dating104.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Dating104.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<Dating104CfgBean> list) throws IllegalAccessException {
		Iterator<Dating104CfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            Dating104CfgBean bean = (Dating104CfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Dating104.csv");
	        }
	}

    public List<Dating104CfgBean> getList()
    {
        return list;
    }

    public Map<Integer, Dating104CfgBean> getMap()
    {
        return map;
    }
    
    public Dating104CfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public Dating104CfgBean createBean() {
		return new Dating104CfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}