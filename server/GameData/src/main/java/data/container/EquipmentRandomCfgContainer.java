/**
 * Auto generated, do not edit it
 *
 * EquipmentRandom
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.EquipmentRandomCfgBean;

public class EquipmentRandomCfgContainer  extends BaseCfgContainer<EquipmentRandomCfgBean>
{
    private final Logger log = Logger.getLogger(EquipmentRandomCfgContainer.class);
    private List<EquipmentRandomCfgBean> list = new ArrayList<>();
    private final Map<Integer, EquipmentRandomCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "EquipmentRandom";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"EquipmentRandom.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"EquipmentRandom.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<EquipmentRandomCfgBean> list) throws IllegalAccessException {
		Iterator<EquipmentRandomCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            EquipmentRandomCfgBean bean = (EquipmentRandomCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in EquipmentRandom.csv");
	        }
	}

    public List<EquipmentRandomCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, EquipmentRandomCfgBean> getMap()
    {
        return map;
    }
    
    public EquipmentRandomCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public EquipmentRandomCfgBean createBean() {
		return new EquipmentRandomCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}