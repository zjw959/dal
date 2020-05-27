/**
 * Auto generated, do not edit it
 *
 * MainDialogue
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.MainDialogueCfgBean;

public class MainDialogueCfgContainer  extends BaseCfgContainer<MainDialogueCfgBean>
{
    private final Logger log = Logger.getLogger(MainDialogueCfgContainer.class);
    private List<MainDialogueCfgBean> list = new ArrayList<>();
    private final Map<Integer, MainDialogueCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "MainDialogue";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"MainDialogue.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"MainDialogue.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<MainDialogueCfgBean> list) throws IllegalAccessException {
		Iterator<MainDialogueCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            MainDialogueCfgBean bean = (MainDialogueCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in MainDialogue.csv");
	        }
	}

    public List<MainDialogueCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, MainDialogueCfgBean> getMap()
    {
        return map;
    }
    
    public MainDialogueCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public MainDialogueCfgBean createBean() {
		return new MainDialogueCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}