/**
 * Auto generated, do not edit it
 *
 * Room
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.RoomCfgBean;

public class RoomCfgContainer  extends BaseCfgContainer<RoomCfgBean>
{
    private final Logger log = Logger.getLogger(RoomCfgContainer.class);
    private List<RoomCfgBean> list = new ArrayList<>();
    private final Map<Integer, RoomCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Room";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Room.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Room.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<RoomCfgBean> list) throws IllegalAccessException {
		Iterator<RoomCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            RoomCfgBean bean = (RoomCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Room.csv");
	        }
	}

    public List<RoomCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, RoomCfgBean> getMap()
    {
        return map;
    }
    
    public RoomCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public RoomCfgBean createBean() {
		return new RoomCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}