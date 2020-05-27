/**
 * Auto generated, do not edit it
 *
 * EnergySupplement
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.EnergySupplementCfgBean;

public class EnergySupplementCfgContainer  extends BaseCfgContainer<EnergySupplementCfgBean>
{
    private final Logger log = Logger.getLogger(EnergySupplementCfgContainer.class);
    private List<EnergySupplementCfgBean> list = new ArrayList<>();
    private final Map<Integer, EnergySupplementCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "EnergySupplement";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"EnergySupplement.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"EnergySupplement.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<EnergySupplementCfgBean> list) throws IllegalAccessException {
		Iterator<EnergySupplementCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            EnergySupplementCfgBean bean = (EnergySupplementCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in EnergySupplement.csv");
	        }
	}

    public List<EnergySupplementCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, EnergySupplementCfgBean> getMap()
    {
        return map;
    }
    
    public EnergySupplementCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public EnergySupplementCfgBean createBean() {
		return new EnergySupplementCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}