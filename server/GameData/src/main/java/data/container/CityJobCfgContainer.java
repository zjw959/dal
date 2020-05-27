/**
 * Auto generated, do not edit it
 *
 * CityJob
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.CityJobCfgBean;

public class CityJobCfgContainer  extends BaseCfgContainer<CityJobCfgBean>
{
    private final Logger log = Logger.getLogger(CityJobCfgContainer.class);
    private List<CityJobCfgBean> list = new ArrayList<>();
    private final Map<Integer, CityJobCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "CityJob";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"CityJob.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"CityJob.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<CityJobCfgBean> list) throws IllegalAccessException {
		Iterator<CityJobCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            CityJobCfgBean bean = (CityJobCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in CityJob.csv");
	        }
	}

    public List<CityJobCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, CityJobCfgBean> getMap()
    {
        return map;
    }
    
    public CityJobCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public CityJobCfgBean createBean() {
		return new CityJobCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}