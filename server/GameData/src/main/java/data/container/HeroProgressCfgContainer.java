/**
 * Auto generated, do not edit it
 *
 * HeroProgress
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.HeroProgressCfgBean;

public class HeroProgressCfgContainer  extends BaseCfgContainer<HeroProgressCfgBean>
{
    private final Logger log = Logger.getLogger(HeroProgressCfgContainer.class);
    private List<HeroProgressCfgBean> list = new ArrayList<>();
    private final Map<Integer, HeroProgressCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "HeroProgress";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"HeroProgress.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"HeroProgress.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<HeroProgressCfgBean> list) throws IllegalAccessException {
		Iterator<HeroProgressCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            HeroProgressCfgBean bean = (HeroProgressCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in HeroProgress.csv");
	        }
	}

    public List<HeroProgressCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, HeroProgressCfgBean> getMap()
    {
        return map;
    }
    
    public HeroProgressCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public HeroProgressCfgBean createBean() {
		return new HeroProgressCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}