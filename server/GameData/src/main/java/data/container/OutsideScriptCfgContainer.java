/**
 * Auto generated, do not edit it
 *
 * OutsideScript
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.OutsideScriptCfgBean;

public class OutsideScriptCfgContainer  extends BaseCfgContainer<OutsideScriptCfgBean>
{
    private final Logger log = Logger.getLogger(OutsideScriptCfgContainer.class);
    private List<OutsideScriptCfgBean> list = new ArrayList<>();
    private final Map<Integer, OutsideScriptCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "OutsideScript";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"OutsideScript.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"OutsideScript.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<OutsideScriptCfgBean> list) throws IllegalAccessException {
		Iterator<OutsideScriptCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            OutsideScriptCfgBean bean = (OutsideScriptCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in OutsideScript.csv");
	        }
	}

    public List<OutsideScriptCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, OutsideScriptCfgBean> getMap()
    {
        return map;
    }
    
    public OutsideScriptCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public OutsideScriptCfgBean createBean() {
		return new OutsideScriptCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}