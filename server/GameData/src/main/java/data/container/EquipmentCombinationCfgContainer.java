/**
 * Auto generated, do not edit it
 *
 * EquipmentCombination
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.EquipmentCombinationCfgBean;

public class EquipmentCombinationCfgContainer  extends BaseCfgContainer<EquipmentCombinationCfgBean>
{
    private final Logger log = Logger.getLogger(EquipmentCombinationCfgContainer.class);
    private List<EquipmentCombinationCfgBean> list = new ArrayList<>();
    private final Map<Integer, EquipmentCombinationCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "EquipmentCombination";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"EquipmentCombination.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"EquipmentCombination.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<EquipmentCombinationCfgBean> list) throws IllegalAccessException {
		Iterator<EquipmentCombinationCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            EquipmentCombinationCfgBean bean = (EquipmentCombinationCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in EquipmentCombination.csv");
	        }
	}

    public List<EquipmentCombinationCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, EquipmentCombinationCfgBean> getMap()
    {
        return map;
    }
    
    public EquipmentCombinationCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public EquipmentCombinationCfgBean createBean() {
		return new EquipmentCombinationCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}