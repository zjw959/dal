/**
 * Auto generated, do not edit it
 *
 * TomorrowSign
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.TomorrowSignCfgBean;

public class TomorrowSignCfgContainer  extends BaseCfgContainer<TomorrowSignCfgBean>
{
    private final Logger log = Logger.getLogger(TomorrowSignCfgContainer.class);
    private List<TomorrowSignCfgBean> list = new ArrayList<>();
    private final Map<Integer, TomorrowSignCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "TomorrowSign";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"TomorrowSign.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"TomorrowSign.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<TomorrowSignCfgBean> list) throws IllegalAccessException {
		Iterator<TomorrowSignCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            TomorrowSignCfgBean bean = (TomorrowSignCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in TomorrowSign.csv");
	        }
	}

    public List<TomorrowSignCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, TomorrowSignCfgBean> getMap()
    {
        return map;
    }
    
    public TomorrowSignCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public TomorrowSignCfgBean createBean() {
		return new TomorrowSignCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}