/**
 * Auto generated, do not edit it
 *
 * CityRoleModel
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.CityRoleModelCfgBean;

public class CityRoleModelCfgContainer  extends BaseCfgContainer<CityRoleModelCfgBean>
{
    private final Logger log = Logger.getLogger(CityRoleModelCfgContainer.class);
    private List<CityRoleModelCfgBean> list = new ArrayList<>();
    private final Map<Integer, CityRoleModelCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "CityRoleModel";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"CityRoleModel.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"CityRoleModel.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<CityRoleModelCfgBean> list) throws IllegalAccessException {
		Iterator<CityRoleModelCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            CityRoleModelCfgBean bean = (CityRoleModelCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in CityRoleModel.csv");
	        }
	}

    public List<CityRoleModelCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, CityRoleModelCfgBean> getMap()
    {
        return map;
    }
    
    public CityRoleModelCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public CityRoleModelCfgBean createBean() {
		return new CityRoleModelCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}