/**
 * Auto generated, do not edit it
 *
 * Sign
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.SignCfgBean;

public class SignCfgContainer  extends BaseCfgContainer<SignCfgBean>
{
    private final Logger log = Logger.getLogger(SignCfgContainer.class);
    private List<SignCfgBean> list = new ArrayList<>();
    private final Map<Integer, SignCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Sign";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Sign.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Sign.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<SignCfgBean> list) throws IllegalAccessException {
		Iterator<SignCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            SignCfgBean bean = (SignCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Sign.csv");
	        }
	}

    public List<SignCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, SignCfgBean> getMap()
    {
        return map;
    }
    
    public SignCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public SignCfgBean createBean() {
		return new SignCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}