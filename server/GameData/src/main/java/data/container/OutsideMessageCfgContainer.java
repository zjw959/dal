/**
 * Auto generated, do not edit it
 *
 * OutsideMessage
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.OutsideMessageCfgBean;

public class OutsideMessageCfgContainer  extends BaseCfgContainer<OutsideMessageCfgBean>
{
    private final Logger log = Logger.getLogger(OutsideMessageCfgContainer.class);
    private List<OutsideMessageCfgBean> list = new ArrayList<>();
    private final Map<Integer, OutsideMessageCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "OutsideMessage";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"OutsideMessage.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"OutsideMessage.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<OutsideMessageCfgBean> list) throws IllegalAccessException {
		Iterator<OutsideMessageCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            OutsideMessageCfgBean bean = (OutsideMessageCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in OutsideMessage.csv");
	        }
	}

    public List<OutsideMessageCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, OutsideMessageCfgBean> getMap()
    {
        return map;
    }
    
    public OutsideMessageCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public OutsideMessageCfgBean createBean() {
		return new OutsideMessageCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}