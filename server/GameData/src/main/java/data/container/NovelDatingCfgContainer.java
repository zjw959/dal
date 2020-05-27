/**
 * Auto generated, do not edit it
 *
 * NovelDating
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.NovelDatingCfgBean;

public class NovelDatingCfgContainer  extends BaseCfgContainer<NovelDatingCfgBean>
{
    private final Logger log = Logger.getLogger(NovelDatingCfgContainer.class);
    private List<NovelDatingCfgBean> list = new ArrayList<>();
    private final Map<Integer, NovelDatingCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "NovelDating";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"NovelDating.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"NovelDating.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<NovelDatingCfgBean> list) throws IllegalAccessException {
		Iterator<NovelDatingCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            NovelDatingCfgBean bean = (NovelDatingCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in NovelDating.csv");
	        }
	}

    public List<NovelDatingCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, NovelDatingCfgBean> getMap()
    {
        return map;
    }
    
    public NovelDatingCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public NovelDatingCfgBean createBean() {
		return new NovelDatingCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}