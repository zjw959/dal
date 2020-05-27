/**
 * Auto generated, do not edit it
 *
 * RechargeGiftBag
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.RechargeGiftBagCfgBean;

public class RechargeGiftBagCfgContainer  extends BaseCfgContainer<RechargeGiftBagCfgBean>
{
    private final Logger log = Logger.getLogger(RechargeGiftBagCfgContainer.class);
    private List<RechargeGiftBagCfgBean> list = new ArrayList<>();
    private final Map<Integer, RechargeGiftBagCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "RechargeGiftBag";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"RechargeGiftBag.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"RechargeGiftBag.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<RechargeGiftBagCfgBean> list) throws IllegalAccessException {
		Iterator<RechargeGiftBagCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            RechargeGiftBagCfgBean bean = (RechargeGiftBagCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in RechargeGiftBag.csv");
	        }
	}

    public List<RechargeGiftBagCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, RechargeGiftBagCfgBean> getMap()
    {
        return map;
    }
    
    public RechargeGiftBagCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public RechargeGiftBagCfgBean createBean() {
		return new RechargeGiftBagCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}