/**
 * Auto generated, do not edit it
 *
 * 约会容器类
 */
package data.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import data.bean.BaseDating;

public class DatingBeanCfgContainer
{
	private final Logger log = Logger.getLogger(DatingBeanCfgContainer.class);
    private final Map<String, Map<Integer, BaseDating>> map = new HashMap<String, Map<Integer, BaseDating>>();

	public void addCfgBean(String tableName,BaseDating bean) {
		if(tableName==null || tableName.length()<=0) return ;
		 Map<Integer, BaseDating> beans=map.get(tableName);
		 if(beans==null){
		 	beans=new HashMap<Integer, BaseDating>(); 
		 	map.put(tableName,beans);
		 } 
		 beans.put(bean.getId(),bean);
	}

    public Map<String, Map<Integer, BaseDating>> getMap(){
        return map;
    }
    
    
    public BaseDating getBaseDatingByScriptId(String tableName,int ScriptId){
    	if(tableName==null || tableName.length()<=0) return null;
    	 Map<Integer, BaseDating> beans=map.get(tableName);
    	 if(beans==null) return null;
    	 return beans.get(ScriptId);
    }
    
     public List<BaseDating> getBaseDatingByRuleId(String tableName,int datingRuleId){
        if(tableName==null || tableName.length()<=0) return null;
        Map<Integer, BaseDating> beans=map.get(tableName);
        if(beans==null) return null;
        List<BaseDating> allCfgList = beans.values()
                .stream().filter(cfg -> cfg.getScriptId() == datingRuleId)
                .collect(Collectors.toList());
        return allCfgList;
    }
    
}