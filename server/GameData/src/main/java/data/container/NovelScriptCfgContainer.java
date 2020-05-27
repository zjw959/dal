/**
 * Auto generated, do not edit it
 *
 * NovelScript
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.NovelScriptCfgBean;

public class NovelScriptCfgContainer  extends BaseCfgContainer<NovelScriptCfgBean>
{
    private final Logger log = Logger.getLogger(NovelScriptCfgContainer.class);
    private List<NovelScriptCfgBean> list = new ArrayList<>();
    private final Map<Integer, NovelScriptCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "NovelScript";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"NovelScript.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"NovelScript.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<NovelScriptCfgBean> list) throws IllegalAccessException {
		Iterator<NovelScriptCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            NovelScriptCfgBean bean = (NovelScriptCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in NovelScript.csv");
	        }
	}

    public List<NovelScriptCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, NovelScriptCfgBean> getMap()
    {
        return map;
    }
    
    public NovelScriptCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public NovelScriptCfgBean createBean() {
		return new NovelScriptCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}