/**
 * Auto generated, do not edit it
 *
 * Dating101
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.Dating101CfgBean;

public class Dating101CfgContainer  extends BaseCfgContainer<Dating101CfgBean>
{
    private final Logger log = Logger.getLogger(Dating101CfgContainer.class);
    private List<Dating101CfgBean> list = new ArrayList<>();
    private final Map<Integer, Dating101CfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Dating101";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Dating101.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Dating101.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<Dating101CfgBean> list) throws IllegalAccessException {
		Iterator<Dating101CfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            Dating101CfgBean bean = (Dating101CfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Dating101.csv");
	        }
	}

    public List<Dating101CfgBean> getList()
    {
        return list;
    }

    public Map<Integer, Dating101CfgBean> getMap()
    {
        return map;
    }
    
    public Dating101CfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public Dating101CfgBean createBean() {
		return new Dating101CfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}