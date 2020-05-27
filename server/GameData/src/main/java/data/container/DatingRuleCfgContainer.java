/**
 * Auto generated, do not edit it
 *
 * DatingRule
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.DatingRuleCfgBean;

public class DatingRuleCfgContainer  extends BaseCfgContainer<DatingRuleCfgBean>
{
    private final Logger log = Logger.getLogger(DatingRuleCfgContainer.class);
    private List<DatingRuleCfgBean> list = new ArrayList<>();
    private final Map<Integer, DatingRuleCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "DatingRule";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"DatingRule.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"DatingRule.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<DatingRuleCfgBean> list) throws IllegalAccessException {
		Iterator<DatingRuleCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            DatingRuleCfgBean bean = (DatingRuleCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in DatingRule.csv");
	        }
	}

    public List<DatingRuleCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, DatingRuleCfgBean> getMap()
    {
        return map;
    }
    
    public DatingRuleCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public DatingRuleCfgBean createBean() {
		return new DatingRuleCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}