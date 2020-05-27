/**
 * Auto generated, do not edit it
 *
 * CookQte
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.CookQteCfgBean;

public class CookQteCfgContainer  extends BaseCfgContainer<CookQteCfgBean>
{
    private final Logger log = Logger.getLogger(CookQteCfgContainer.class);
    private List<CookQteCfgBean> list = new ArrayList<>();
    private final Map<Integer, CookQteCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "CookQte";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"CookQte.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"CookQte.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<CookQteCfgBean> list) throws IllegalAccessException {
		Iterator<CookQteCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            CookQteCfgBean bean = (CookQteCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in CookQte.csv");
	        }
	}

    public List<CookQteCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, CookQteCfgBean> getMap()
    {
        return map;
    }
    
    public CookQteCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public CookQteCfgBean createBean() {
		return new CookQteCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}