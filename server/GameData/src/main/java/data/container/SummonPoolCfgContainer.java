/**
 * Auto generated, do not edit it
 *
 * SummonPool
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.SummonPoolCfgBean;

public class SummonPoolCfgContainer  extends BaseCfgContainer<SummonPoolCfgBean>
{
    private final Logger log = Logger.getLogger(SummonPoolCfgContainer.class);
    private List<SummonPoolCfgBean> list = new ArrayList<>();
    private final Map<Integer, SummonPoolCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "SummonPool";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"SummonPool.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"SummonPool.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<SummonPoolCfgBean> list) throws IllegalAccessException {
		Iterator<SummonPoolCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            SummonPoolCfgBean bean = (SummonPoolCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in SummonPool.csv");
	        }
	}

    public List<SummonPoolCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, SummonPoolCfgBean> getMap()
    {
        return map;
    }
    
    public SummonPoolCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public SummonPoolCfgBean createBean() {
		return new SummonPoolCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}