/**
 * Auto generated, do not edit it
 *
 * Medal
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.MedalCfgBean;

public class MedalCfgContainer  extends BaseCfgContainer<MedalCfgBean>
{
    private final Logger log = Logger.getLogger(MedalCfgContainer.class);
    private List<MedalCfgBean> list = new ArrayList<>();
    private final Map<Integer, MedalCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Medal";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Medal.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Medal.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<MedalCfgBean> list) throws IllegalAccessException {
		Iterator<MedalCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            MedalCfgBean bean = (MedalCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Medal.csv");
	        }
	}

    public List<MedalCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, MedalCfgBean> getMap()
    {
        return map;
    }
    
    public MedalCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public MedalCfgBean createBean() {
		return new MedalCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}