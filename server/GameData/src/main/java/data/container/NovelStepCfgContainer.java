/**
 * Auto generated, do not edit it
 *
 * NovelStep
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.NovelStepCfgBean;

public class NovelStepCfgContainer  extends BaseCfgContainer<NovelStepCfgBean>
{
    private final Logger log = Logger.getLogger(NovelStepCfgContainer.class);
    private List<NovelStepCfgBean> list = new ArrayList<>();
    private final Map<Integer, NovelStepCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "NovelStep";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"NovelStep.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"NovelStep.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<NovelStepCfgBean> list) throws IllegalAccessException {
		Iterator<NovelStepCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            NovelStepCfgBean bean = (NovelStepCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in NovelStep.csv");
	        }
	}

    public List<NovelStepCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, NovelStepCfgBean> getMap()
    {
        return map;
    }
    
    public NovelStepCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public NovelStepCfgBean createBean() {
		return new NovelStepCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}