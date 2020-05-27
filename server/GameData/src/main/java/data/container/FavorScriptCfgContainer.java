/**
 * Auto generated, do not edit it
 *
 * FavorScript
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.FavorScriptCfgBean;

public class FavorScriptCfgContainer  extends BaseCfgContainer<FavorScriptCfgBean>
{
    private final Logger log = Logger.getLogger(FavorScriptCfgContainer.class);
    private List<FavorScriptCfgBean> list = new ArrayList<>();
    private final Map<Integer, FavorScriptCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "FavorScript";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"FavorScript.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"FavorScript.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<FavorScriptCfgBean> list) throws IllegalAccessException {
		Iterator<FavorScriptCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            FavorScriptCfgBean bean = (FavorScriptCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in FavorScript.csv");
	        }
	}

    public List<FavorScriptCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, FavorScriptCfgBean> getMap()
    {
        return map;
    }
    
    public FavorScriptCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public FavorScriptCfgBean createBean() {
		return new FavorScriptCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}