/**
 * Auto generated, do not edit it
 *
 * DatingVariable
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.DatingVariableCfgBean;

public class DatingVariableCfgContainer  extends BaseCfgContainer<DatingVariableCfgBean>
{
    private final Logger log = Logger.getLogger(DatingVariableCfgContainer.class);
    private List<DatingVariableCfgBean> list = new ArrayList<>();
    private final Map<Integer, DatingVariableCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "DatingVariable";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"DatingVariable.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"DatingVariable.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<DatingVariableCfgBean> list) throws IllegalAccessException {
		Iterator<DatingVariableCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            DatingVariableCfgBean bean = (DatingVariableCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in DatingVariable.csv");
	        }
	}

    public List<DatingVariableCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, DatingVariableCfgBean> getMap()
    {
        return map;
    }
    
    public DatingVariableCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public DatingVariableCfgBean createBean() {
		return new DatingVariableCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}