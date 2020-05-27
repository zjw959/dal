/**
 * Auto generated, do not edit it
 *
 * AngelSkillTree
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.AngelSkillTreeCfgBean;

public class AngelSkillTreeCfgContainer  extends BaseCfgContainer<AngelSkillTreeCfgBean>
{
    private final Logger log = Logger.getLogger(AngelSkillTreeCfgContainer.class);
    private List<AngelSkillTreeCfgBean> list = new ArrayList<>();
    private final Map<Integer, AngelSkillTreeCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "AngelSkillTree";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"AngelSkillTree.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"AngelSkillTree.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<AngelSkillTreeCfgBean> list) throws IllegalAccessException {
		Iterator<AngelSkillTreeCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            AngelSkillTreeCfgBean bean = (AngelSkillTreeCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in AngelSkillTree.csv");
	        }
	}

    public List<AngelSkillTreeCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, AngelSkillTreeCfgBean> getMap()
    {
        return map;
    }
    
    public AngelSkillTreeCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public AngelSkillTreeCfgBean createBean() {
		return new AngelSkillTreeCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}