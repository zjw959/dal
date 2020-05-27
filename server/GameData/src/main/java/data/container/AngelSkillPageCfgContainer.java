/**
 * Auto generated, do not edit it
 *
 * AngelSkillPage
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.AngelSkillPageCfgBean;

public class AngelSkillPageCfgContainer  extends BaseCfgContainer<AngelSkillPageCfgBean>
{
    private final Logger log = Logger.getLogger(AngelSkillPageCfgContainer.class);
    private List<AngelSkillPageCfgBean> list = new ArrayList<>();
    private final Map<Integer, AngelSkillPageCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "AngelSkillPage";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"AngelSkillPage.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"AngelSkillPage.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<AngelSkillPageCfgBean> list) throws IllegalAccessException {
		Iterator<AngelSkillPageCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            AngelSkillPageCfgBean bean = (AngelSkillPageCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in AngelSkillPage.csv");
	        }
	}

    public List<AngelSkillPageCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, AngelSkillPageCfgBean> getMap()
    {
        return map;
    }
    
    public AngelSkillPageCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public AngelSkillPageCfgBean createBean() {
		return new AngelSkillPageCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}