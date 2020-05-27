/**
 * Auto generated, do not edit it
 *
 * SevenSign
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.SevenSignCfgBean;

public class SevenSignCfgContainer  extends BaseCfgContainer<SevenSignCfgBean>
{
    private final Logger log = Logger.getLogger(SevenSignCfgContainer.class);
    private List<SevenSignCfgBean> list = new ArrayList<>();
    private final Map<Integer, SevenSignCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "SevenSign";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"SevenSign.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"SevenSign.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<SevenSignCfgBean> list) throws IllegalAccessException {
		Iterator<SevenSignCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            SevenSignCfgBean bean = (SevenSignCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in SevenSign.csv");
	        }
	}

    public List<SevenSignCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, SevenSignCfgBean> getMap()
    {
        return map;
    }
    
    public SevenSignCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public SevenSignCfgBean createBean() {
		return new SevenSignCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}