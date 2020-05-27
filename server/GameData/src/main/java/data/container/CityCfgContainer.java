/**
 * Auto generated, do not edit it
 *
 * City
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.CityCfgBean;

public class CityCfgContainer  extends BaseCfgContainer<CityCfgBean>
{
    private final Logger log = Logger.getLogger(CityCfgContainer.class);
    private List<CityCfgBean> list = new ArrayList<>();
    private final Map<Integer, CityCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "City";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"City.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"City.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<CityCfgBean> list) throws IllegalAccessException {
		Iterator<CityCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            CityCfgBean bean = (CityCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in City.csv");
	        }
	}

    public List<CityCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, CityCfgBean> getMap()
    {
        return map;
    }
    
    public CityCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public CityCfgBean createBean() {
		return new CityCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}