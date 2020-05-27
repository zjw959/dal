/**
 * Auto generated, do not edit it
 *
 * MonthCard
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.MonthCardCfgBean;

public class MonthCardCfgContainer  extends BaseCfgContainer<MonthCardCfgBean>
{
    private final Logger log = Logger.getLogger(MonthCardCfgContainer.class);
    private List<MonthCardCfgBean> list = new ArrayList<>();
    private final Map<Integer, MonthCardCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "MonthCard";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"MonthCard.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"MonthCard.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<MonthCardCfgBean> list) throws IllegalAccessException {
		Iterator<MonthCardCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            MonthCardCfgBean bean = (MonthCardCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in MonthCard.csv");
	        }
	}

    public List<MonthCardCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, MonthCardCfgBean> getMap()
    {
        return map;
    }
    
    public MonthCardCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public MonthCardCfgBean createBean() {
		return new MonthCardCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}