/**
 * Auto generated, do not edit it
 *
 * KabalaShop
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.KabalaShopCfgBean;

public class KabalaShopCfgContainer  extends BaseCfgContainer<KabalaShopCfgBean>
{
    private final Logger log = Logger.getLogger(KabalaShopCfgContainer.class);
    private List<KabalaShopCfgBean> list = new ArrayList<>();
    private final Map<Integer, KabalaShopCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "KabalaShop";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"KabalaShop.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"KabalaShop.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<KabalaShopCfgBean> list) throws IllegalAccessException {
		Iterator<KabalaShopCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            KabalaShopCfgBean bean = (KabalaShopCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in KabalaShop.csv");
	        }
	}

    public List<KabalaShopCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, KabalaShopCfgBean> getMap()
    {
        return map;
    }
    
    public KabalaShopCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public KabalaShopCfgBean createBean() {
		return new KabalaShopCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}