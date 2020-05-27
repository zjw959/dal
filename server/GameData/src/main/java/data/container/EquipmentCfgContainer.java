/**
 * Auto generated, do not edit it
 *
 * Equipment
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.EquipmentCfgBean;

public class EquipmentCfgContainer  extends BaseCfgContainer<EquipmentCfgBean>
{
    private final Logger log = Logger.getLogger(EquipmentCfgContainer.class);
    private List<EquipmentCfgBean> list = new ArrayList<>();
    private final Map<Integer, EquipmentCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Equipment";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Equipment.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Equipment.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<EquipmentCfgBean> list) throws IllegalAccessException {
		Iterator<EquipmentCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            EquipmentCfgBean bean = (EquipmentCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Equipment.csv");
	        }
	}

    public List<EquipmentCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, EquipmentCfgBean> getMap()
    {
        return map;
    }
    
    public EquipmentCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public EquipmentCfgBean createBean() {
		return new EquipmentCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}