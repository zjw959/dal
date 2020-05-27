/**
 * Auto generated, do not edit it
 *
 * CityEffect
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.CityEffectCfgBean;

public class CityEffectCfgContainer  extends BaseCfgContainer<CityEffectCfgBean>
{
    private final Logger log = Logger.getLogger(CityEffectCfgContainer.class);
    private List<CityEffectCfgBean> list = new ArrayList<>();
    private final Map<Integer, CityEffectCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "CityEffect";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"CityEffect.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"CityEffect.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<CityEffectCfgBean> list) throws IllegalAccessException {
		Iterator<CityEffectCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            CityEffectCfgBean bean = (CityEffectCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in CityEffect.csv");
	        }
	}

    public List<CityEffectCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, CityEffectCfgBean> getMap()
    {
        return map;
    }
    
    public CityEffectCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public CityEffectCfgBean createBean() {
		return new CityEffectCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}