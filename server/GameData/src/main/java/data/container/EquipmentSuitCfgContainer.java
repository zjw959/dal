/**
 * Auto generated, do not edit it
 *
 * EquipmentSuit
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.EquipmentSuitCfgBean;

public class EquipmentSuitCfgContainer  extends BaseCfgContainer<EquipmentSuitCfgBean>
{
    private final Logger log = Logger.getLogger(EquipmentSuitCfgContainer.class);
    private List<EquipmentSuitCfgBean> list = new ArrayList<>();
    private final Map<Integer, EquipmentSuitCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "EquipmentSuit";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"EquipmentSuit.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"EquipmentSuit.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<EquipmentSuitCfgBean> list) throws IllegalAccessException {
		Iterator<EquipmentSuitCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            EquipmentSuitCfgBean bean = (EquipmentSuitCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in EquipmentSuit.csv");
	        }
	}

    public List<EquipmentSuitCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, EquipmentSuitCfgBean> getMap()
    {
        return map;
    }
    
    public EquipmentSuitCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public EquipmentSuitCfgBean createBean() {
		return new EquipmentSuitCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}