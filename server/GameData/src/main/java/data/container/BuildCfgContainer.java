/**
 * Auto generated, do not edit it
 *
 * Build
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.BuildCfgBean;

public class BuildCfgContainer  extends BaseCfgContainer<BuildCfgBean>
{
    private final Logger log = Logger.getLogger(BuildCfgContainer.class);
    private List<BuildCfgBean> list = new ArrayList<>();
    private final Map<Integer, BuildCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Build";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Build.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Build.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<BuildCfgBean> list) throws IllegalAccessException {
		Iterator<BuildCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            BuildCfgBean bean = (BuildCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Build.csv");
	        }
	}

    public List<BuildCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, BuildCfgBean> getMap()
    {
        return map;
    }
    
    public BuildCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public BuildCfgBean createBean() {
		return new BuildCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}