/**
 * Auto generated, do not edit it
 *
 * Function
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.FunctionCfgBean;

public class FunctionCfgContainer  extends BaseCfgContainer<FunctionCfgBean>
{
    private final Logger log = Logger.getLogger(FunctionCfgContainer.class);
    private List<FunctionCfgBean> list = new ArrayList<>();
    private final Map<Integer, FunctionCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Function";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Function.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Function.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<FunctionCfgBean> list) throws IllegalAccessException {
		Iterator<FunctionCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            FunctionCfgBean bean = (FunctionCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Function.csv");
	        }
	}

    public List<FunctionCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, FunctionCfgBean> getMap()
    {
        return map;
    }
    
    public FunctionCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public FunctionCfgBean createBean() {
		return new FunctionCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}