/**
 * Auto generated, do not edit it
 *
 * NewBuild
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.NewBuildCfgBean;

public class NewBuildCfgContainer  extends BaseCfgContainer<NewBuildCfgBean>
{
    private final Logger log = Logger.getLogger(NewBuildCfgContainer.class);
    private List<NewBuildCfgBean> list = new ArrayList<>();
    private final Map<Integer, NewBuildCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "NewBuild";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"NewBuild.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"NewBuild.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<NewBuildCfgBean> list) throws IllegalAccessException {
		Iterator<NewBuildCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            NewBuildCfgBean bean = (NewBuildCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in NewBuild.csv");
	        }
	}

    public List<NewBuildCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, NewBuildCfgBean> getMap()
    {
        return map;
    }
    
    public NewBuildCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public NewBuildCfgBean createBean() {
		return new NewBuildCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}