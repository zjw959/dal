/**
 * Auto generated, do not edit it
 *
 * FavorMessage
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.FavorMessageCfgBean;

public class FavorMessageCfgContainer  extends BaseCfgContainer<FavorMessageCfgBean>
{
    private final Logger log = Logger.getLogger(FavorMessageCfgContainer.class);
    private List<FavorMessageCfgBean> list = new ArrayList<>();
    private final Map<Integer, FavorMessageCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "FavorMessage";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"FavorMessage.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"FavorMessage.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<FavorMessageCfgBean> list) throws IllegalAccessException {
		Iterator<FavorMessageCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            FavorMessageCfgBean bean = (FavorMessageCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in FavorMessage.csv");
	        }
	}

    public List<FavorMessageCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, FavorMessageCfgBean> getMap()
    {
        return map;
    }
    
    public FavorMessageCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public FavorMessageCfgBean createBean() {
		return new FavorMessageCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}