/**
 * Auto generated, do not edit it
 *
 * MainLine
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.MainLineCfgBean;

public class MainLineCfgContainer  extends BaseCfgContainer<MainLineCfgBean>
{
    private final Logger log = Logger.getLogger(MainLineCfgContainer.class);
    private List<MainLineCfgBean> list = new ArrayList<>();
    private final Map<Integer, MainLineCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "MainLine";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"MainLine.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"MainLine.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<MainLineCfgBean> list) throws IllegalAccessException {
		Iterator<MainLineCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            MainLineCfgBean bean = (MainLineCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in MainLine.csv");
	        }
	}

    public List<MainLineCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, MainLineCfgBean> getMap()
    {
        return map;
    }
    
    public MainLineCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public MainLineCfgBean createBean() {
		return new MainLineCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}