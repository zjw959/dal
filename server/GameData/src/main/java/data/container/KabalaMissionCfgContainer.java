/**
 * Auto generated, do not edit it
 *
 * KabalaMission
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.KabalaMissionCfgBean;

public class KabalaMissionCfgContainer  extends BaseCfgContainer<KabalaMissionCfgBean>
{
    private final Logger log = Logger.getLogger(KabalaMissionCfgContainer.class);
    private List<KabalaMissionCfgBean> list = new ArrayList<>();
    private final Map<Integer, KabalaMissionCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "KabalaMission";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"KabalaMission.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"KabalaMission.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<KabalaMissionCfgBean> list) throws IllegalAccessException {
		Iterator<KabalaMissionCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            KabalaMissionCfgBean bean = (KabalaMissionCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in KabalaMission.csv");
	        }
	}

    public List<KabalaMissionCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, KabalaMissionCfgBean> getMap()
    {
        return map;
    }
    
    public KabalaMissionCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public KabalaMissionCfgBean createBean() {
		return new KabalaMissionCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}