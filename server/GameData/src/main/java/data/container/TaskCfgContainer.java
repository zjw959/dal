/**
 * Auto generated, do not edit it
 *
 * Task
 */
package data.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.TaskCfgBean;

public class TaskCfgContainer  extends BaseCfgContainer<TaskCfgBean>
{
    private final Logger log = Logger.getLogger(TaskCfgContainer.class);
    private List<TaskCfgBean> list = new ArrayList<>();
    private final Map<Integer, TaskCfgBean> map = new HashMap<>();
	public final String TABLE_NAME = "Task";  

	public void load(String path) throws Exception
    {
        list = loadCsv(path +"Task.csv");
        addCfgBean(list);
    }

    public void load() throws Exception
    {
        list = loadCsv(GameDataManager.getConfigPath()+"Task.csv");
        addCfgBean(list);
    }

	private void addCfgBean(List<TaskCfgBean> list) throws IllegalAccessException {
		Iterator<TaskCfgBean> iter = list.iterator();
	        while (iter.hasNext())
	        {
	            TaskCfgBean bean = (TaskCfgBean) iter.next();
	            if(map.putIfAbsent(bean.getId(), bean) != null)
	                throw new IllegalAccessException("duplicate value ["+bean.getId()+"] of primary key [id] in Task.csv");
	        }
	}

    public List<TaskCfgBean> getList()
    {
        return list;
    }

    public Map<Integer, TaskCfgBean> getMap()
    {
        return map;
    }
    
    public TaskCfgBean getBean(int id) {
    	return map.get(id);
    }
    
	public TaskCfgBean createBean() {
		return new TaskCfgBean();
	}
	public Logger getLogger() {
		return log;
	}
}