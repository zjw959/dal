/**
 * Auto generated, do not edit it
 *
 * Outside
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.OutsideCfgBean;

public class OutsideCfgContainer  extends BaseCfgContainer<OutsideCfgBean>
{
    private final Logger log = Logger.getLogger(OutsideCfgContainer.class);
    private List<OutsideCfgBean> list = new ArrayList<>();
    private final Map<Integer, OutsideCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Outside";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Outside.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Outside.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<OutsideCfgBean> list) throws IllegalAccessException {
		Iterator<OutsideCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            OutsideCfgBean bean = (OutsideCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Outside.csv");
	        }
	}

    public List<OutsideCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, OutsideCfgBean> getMap()
    {
        return map;
    }
    
    public OutsideCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public OutsideCfgBean createBean() {
		return new OutsideCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}