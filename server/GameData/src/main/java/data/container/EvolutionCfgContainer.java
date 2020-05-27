/**
 * Auto generated, do not edit it
 *
 * Evolution
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.EvolutionCfgBean;

public class EvolutionCfgContainer  extends BaseCfgContainer<EvolutionCfgBean>
{
    private final Logger log = Logger.getLogger(EvolutionCfgContainer.class);
    private List<EvolutionCfgBean> list = new ArrayList<>();
    private final Map<Integer, EvolutionCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Evolution";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Evolution.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Evolution.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<EvolutionCfgBean> list) throws IllegalAccessException {
		Iterator<EvolutionCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            EvolutionCfgBean bean = (EvolutionCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Evolution.csv");
	        }
	}

    public List<EvolutionCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, EvolutionCfgBean> getMap()
    {
        return map;
    }
    
    public EvolutionCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public EvolutionCfgBean createBean() {
		return new EvolutionCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}