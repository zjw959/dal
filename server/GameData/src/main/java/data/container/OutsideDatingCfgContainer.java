/**
 * Auto generated, do not edit it
 *
 * OutsideDating
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.OutsideDatingCfgBean;

public class OutsideDatingCfgContainer  extends BaseCfgContainer<OutsideDatingCfgBean>
{
    private final Logger log = Logger.getLogger(OutsideDatingCfgContainer.class);
    private List<OutsideDatingCfgBean> list = new ArrayList<>();
    private final Map<Integer, OutsideDatingCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "OutsideDating";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"OutsideDating.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"OutsideDating.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<OutsideDatingCfgBean> list) throws IllegalAccessException {
		Iterator<OutsideDatingCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            OutsideDatingCfgBean bean = (OutsideDatingCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in OutsideDating.csv");
	        }
	}

    public List<OutsideDatingCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, OutsideDatingCfgBean> getMap()
    {
        return map;
    }
    
    public OutsideDatingCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public OutsideDatingCfgBean createBean() {
		return new OutsideDatingCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}