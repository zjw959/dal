/**
 * Auto generated, do not edit it
 *
 * Dating103
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.Dating103CfgBean;

public class Dating103CfgContainer  extends BaseCfgContainer<Dating103CfgBean>
{
    private final Logger log = Logger.getLogger(Dating103CfgContainer.class);
    private List<Dating103CfgBean> list = new ArrayList<>();
    private final Map<Integer, Dating103CfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Dating103";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Dating103.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Dating103.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<Dating103CfgBean> list) throws IllegalAccessException {
		Iterator<Dating103CfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            Dating103CfgBean bean = (Dating103CfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Dating103.csv");
	        }
	}

    public List<Dating103CfgBean> getList()
    {
        return list;
    }

    public Map<Integer, Dating103CfgBean> getMap()
    {
        return map;
    }
    
    public Dating103CfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public Dating103CfgBean createBean() {
		return new Dating103CfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}