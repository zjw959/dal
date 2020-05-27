/**
 * Auto generated, do not edit it
 *
 * AngelPassiveSkillGrooves
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.AngelPassiveSkillGroovesCfgBean;

public class AngelPassiveSkillGroovesCfgContainer  extends BaseCfgContainer<AngelPassiveSkillGroovesCfgBean>
{
    private final Logger log = Logger.getLogger(AngelPassiveSkillGroovesCfgContainer.class);
    private List<AngelPassiveSkillGroovesCfgBean> list = new ArrayList<>();
    private final Map<Integer, AngelPassiveSkillGroovesCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "AngelPassiveSkillGrooves";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"AngelPassiveSkillGrooves.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"AngelPassiveSkillGrooves.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<AngelPassiveSkillGroovesCfgBean> list) throws IllegalAccessException {
		Iterator<AngelPassiveSkillGroovesCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            AngelPassiveSkillGroovesCfgBean bean = (AngelPassiveSkillGroovesCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in AngelPassiveSkillGrooves.csv");
	        }
	}

    public List<AngelPassiveSkillGroovesCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, AngelPassiveSkillGroovesCfgBean> getMap()
    {
        return map;
    }
    
    public AngelPassiveSkillGroovesCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public AngelPassiveSkillGroovesCfgBean createBean() {
		return new AngelPassiveSkillGroovesCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}