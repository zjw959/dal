/**
 * Auto generated, do not edit it
 *
 * DailyChapterMultiple
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.DailyChapterMultipleCfgBean;

public class DailyChapterMultipleCfgContainer  extends BaseCfgContainer<DailyChapterMultipleCfgBean>
{
    private final Logger log = Logger.getLogger(DailyChapterMultipleCfgContainer.class);
    private List<DailyChapterMultipleCfgBean> list = new ArrayList<>();
    private final Map<Integer, DailyChapterMultipleCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "DailyChapterMultiple";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"DailyChapterMultiple.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"DailyChapterMultiple.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<DailyChapterMultipleCfgBean> list) throws IllegalAccessException {
		Iterator<DailyChapterMultipleCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            DailyChapterMultipleCfgBean bean = (DailyChapterMultipleCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in DailyChapterMultiple.csv");
	        }
	}

    public List<DailyChapterMultipleCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, DailyChapterMultipleCfgBean> getMap()
    {
        return map;
    }
    
    public DailyChapterMultipleCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public DailyChapterMultipleCfgBean createBean() {
		return new DailyChapterMultipleCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}