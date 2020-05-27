/**
 * Auto generated, do not edit it
 *
 * Dating105
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.Dating105CfgBean;

public class Dating105CfgContainer  extends BaseCfgContainer<Dating105CfgBean>
{
    private final Logger log = Logger.getLogger(Dating105CfgContainer.class);
    private List<Dating105CfgBean> list = new ArrayList<>();
    private final Map<Integer, Dating105CfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Dating105";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Dating105.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Dating105.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<Dating105CfgBean> list) throws IllegalAccessException {
		Iterator<Dating105CfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            Dating105CfgBean bean = (Dating105CfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Dating105.csv");
	        }
	}

    public List<Dating105CfgBean> getList()
    {
        return list;
    }

    public Map<Integer, Dating105CfgBean> getMap()
    {
        return map;
    }
    
    public Dating105CfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public Dating105CfgBean createBean() {
		return new Dating105CfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}