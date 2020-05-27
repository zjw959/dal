/**
 * Auto generated, do not edit it
 *
 * FavorStep
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.FavorStepCfgBean;

public class FavorStepCfgContainer  extends BaseCfgContainer<FavorStepCfgBean>
{
    private final Logger log = Logger.getLogger(FavorStepCfgContainer.class);
    private List<FavorStepCfgBean> list = new ArrayList<>();
    private final Map<Integer, FavorStepCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "FavorStep";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"FavorStep.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"FavorStep.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<FavorStepCfgBean> list) throws IllegalAccessException {
		Iterator<FavorStepCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            FavorStepCfgBean bean = (FavorStepCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in FavorStep.csv");
	        }
	}

    public List<FavorStepCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, FavorStepCfgBean> getMap()
    {
        return map;
    }
    
    public FavorStepCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public FavorStepCfgBean createBean() {
		return new FavorStepCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}