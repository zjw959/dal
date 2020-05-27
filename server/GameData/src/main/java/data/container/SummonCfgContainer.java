/**
 * Auto generated, do not edit it
 *
 * Summon
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.SummonCfgBean;

public class SummonCfgContainer  extends BaseCfgContainer<SummonCfgBean>
{
    private final Logger log = Logger.getLogger(SummonCfgContainer.class);
    private List<SummonCfgBean> list = new ArrayList<>();
    private final Map<Integer, SummonCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Summon";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Summon.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Summon.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<SummonCfgBean> list) throws IllegalAccessException {
		Iterator<SummonCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            SummonCfgBean bean = (SummonCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Summon.csv");
	        }
	}

    public List<SummonCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, SummonCfgBean> getMap()
    {
        return map;
    }
    
    public SummonCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public SummonCfgBean createBean() {
		return new SummonCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}