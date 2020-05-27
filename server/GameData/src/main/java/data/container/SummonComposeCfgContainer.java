/**
 * Auto generated, do not edit it
 *
 * SummonCompose
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.SummonComposeCfgBean;

public class SummonComposeCfgContainer  extends BaseCfgContainer<SummonComposeCfgBean>
{
    private final Logger log = Logger.getLogger(SummonComposeCfgContainer.class);
    private List<SummonComposeCfgBean> list = new ArrayList<>();
    private final Map<Integer, SummonComposeCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "SummonCompose";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"SummonCompose.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"SummonCompose.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<SummonComposeCfgBean> list) throws IllegalAccessException {
		Iterator<SummonComposeCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            SummonComposeCfgBean bean = (SummonComposeCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in SummonCompose.csv");
	        }
	}

    public List<SummonComposeCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, SummonComposeCfgBean> getMap()
    {
        return map;
    }
    
    public SummonComposeCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public SummonComposeCfgBean createBean() {
		return new SummonComposeCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}