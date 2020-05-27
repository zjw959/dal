/**
 * Auto generated, do not edit it
 *
 * EquipmentGrowth
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.EquipmentGrowthCfgBean;

public class EquipmentGrowthCfgContainer  extends BaseCfgContainer<EquipmentGrowthCfgBean>
{
    private final Logger log = Logger.getLogger(EquipmentGrowthCfgContainer.class);
    private List<EquipmentGrowthCfgBean> list = new ArrayList<>();
    private final Map<Integer, EquipmentGrowthCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "EquipmentGrowth";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"EquipmentGrowth.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"EquipmentGrowth.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<EquipmentGrowthCfgBean> list) throws IllegalAccessException {
		Iterator<EquipmentGrowthCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            EquipmentGrowthCfgBean bean = (EquipmentGrowthCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in EquipmentGrowth.csv");
	        }
	}

    public List<EquipmentGrowthCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, EquipmentGrowthCfgBean> getMap()
    {
        return map;
    }
    
    public EquipmentGrowthCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public EquipmentGrowthCfgBean createBean() {
		return new EquipmentGrowthCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}