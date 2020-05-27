/**
 * Auto generated, do not edit it
 *
 * DungeonChapter
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.DungeonChapterCfgBean;

public class DungeonChapterCfgContainer  extends BaseCfgContainer<DungeonChapterCfgBean>
{
    private final Logger log = Logger.getLogger(DungeonChapterCfgContainer.class);
    private List<DungeonChapterCfgBean> list = new ArrayList<>();
    private final Map<Integer, DungeonChapterCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "DungeonChapter";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"DungeonChapter.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"DungeonChapter.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<DungeonChapterCfgBean> list) throws IllegalAccessException {
		Iterator<DungeonChapterCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            DungeonChapterCfgBean bean = (DungeonChapterCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in DungeonChapter.csv");
	        }
	}

    public List<DungeonChapterCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, DungeonChapterCfgBean> getMap()
    {
        return map;
    }
    
    public DungeonChapterCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public DungeonChapterCfgBean createBean() {
		return new DungeonChapterCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}