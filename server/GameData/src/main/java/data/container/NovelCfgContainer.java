/**
 * Auto generated, do not edit it
 *
 * Novel
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.NovelCfgBean;

public class NovelCfgContainer  extends BaseCfgContainer<NovelCfgBean>
{
    private final Logger log = Logger.getLogger(NovelCfgContainer.class);
    private List<NovelCfgBean> list = new ArrayList<>();
    private final Map<Integer, NovelCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Novel";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Novel.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Novel.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<NovelCfgBean> list) throws IllegalAccessException {
		Iterator<NovelCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            NovelCfgBean bean = (NovelCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Novel.csv");
	        }
	}

    public List<NovelCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, NovelCfgBean> getMap()
    {
        return map;
    }
    
    public NovelCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public NovelCfgBean createBean() {
		return new NovelCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}