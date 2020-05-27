/**
 * Auto generated, do not edit it
 *
 * DiscreteData
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;

public class DiscreteDataCfgContainer  extends BaseCfgContainer<DiscreteDataCfgBean>
{
    private final Logger log = Logger.getLogger(DiscreteDataCfgContainer.class);
    private List<DiscreteDataCfgBean> list = new ArrayList<>();
    private final Map<Integer, DiscreteDataCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "DiscreteData";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"DiscreteData.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"DiscreteData.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<DiscreteDataCfgBean> list) throws IllegalAccessException {
		Iterator<DiscreteDataCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            DiscreteDataCfgBean bean = (DiscreteDataCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in DiscreteData.csv");
	        }
	}

    public List<DiscreteDataCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, DiscreteDataCfgBean> getMap()
    {
        return map;
    }
    
    public DiscreteDataCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public DiscreteDataCfgBean createBean() {
		return new DiscreteDataCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}