/**
 * Auto generated, do not edit it
 *
 * HeroSkin
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.HeroSkinCfgBean;

public class HeroSkinCfgContainer  extends BaseCfgContainer<HeroSkinCfgBean>
{
    private final Logger log = Logger.getLogger(HeroSkinCfgContainer.class);
    private List<HeroSkinCfgBean> list = new ArrayList<>();
    private final Map<Integer, HeroSkinCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "HeroSkin";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"HeroSkin.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"HeroSkin.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<HeroSkinCfgBean> list) throws IllegalAccessException {
		Iterator<HeroSkinCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            HeroSkinCfgBean bean = (HeroSkinCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in HeroSkin.csv");
	        }
	}

    public List<HeroSkinCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, HeroSkinCfgBean> getMap()
    {
        return map;
    }
    
    public HeroSkinCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public HeroSkinCfgBean createBean() {
		return new HeroSkinCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}