/**
 * Auto generated, do not edit it
 *
 * Hero
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.HeroCfgBean;

public class HeroCfgContainer  extends BaseCfgContainer<HeroCfgBean>
{
    private final Logger log = Logger.getLogger(HeroCfgContainer.class);
    private List<HeroCfgBean> list = new ArrayList<>();
    private final Map<Integer, HeroCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Hero";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Hero.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Hero.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<HeroCfgBean> list) throws IllegalAccessException {
		Iterator<HeroCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            HeroCfgBean bean = (HeroCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Hero.csv");
	        }
	}

    public List<HeroCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, HeroCfgBean> getMap()
    {
        return map;
    }
    
    public HeroCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public HeroCfgBean createBean() {
		return new HeroCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}