/**
 * Auto generated, do not edit it
 *
 * Egg
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.EggCfgBean;

public class EggCfgContainer  extends BaseCfgContainer<EggCfgBean>
{
    private final Logger log = Logger.getLogger(EggCfgContainer.class);
    private List<EggCfgBean> list = new ArrayList<>();
    private final Map<Integer, EggCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Egg";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Egg.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Egg.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<EggCfgBean> list) throws IllegalAccessException {
		Iterator<EggCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            EggCfgBean bean = (EggCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Egg.csv");
	        }
	}

    public List<EggCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, EggCfgBean> getMap()
    {
        return map;
    }
    
    public EggCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public EggCfgBean createBean() {
		return new EggCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}