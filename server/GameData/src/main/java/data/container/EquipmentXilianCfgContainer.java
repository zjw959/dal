/**
 * Auto generated, do not edit it
 *
 * EquipmentXilian
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.EquipmentXilianCfgBean;

public class EquipmentXilianCfgContainer  extends BaseCfgContainer<EquipmentXilianCfgBean>
{
    private final Logger log = Logger.getLogger(EquipmentXilianCfgContainer.class);
    private List<EquipmentXilianCfgBean> list = new ArrayList<>();
    private final Map<Integer, EquipmentXilianCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "EquipmentXilian";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"EquipmentXilian.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"EquipmentXilian.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<EquipmentXilianCfgBean> list) throws IllegalAccessException {
		Iterator<EquipmentXilianCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            EquipmentXilianCfgBean bean = (EquipmentXilianCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in EquipmentXilian.csv");
	        }
	}

    public List<EquipmentXilianCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, EquipmentXilianCfgBean> getMap()
    {
        return map;
    }
    
    public EquipmentXilianCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public EquipmentXilianCfgBean createBean() {
		return new EquipmentXilianCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}