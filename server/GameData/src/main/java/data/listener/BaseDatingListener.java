/**
 * Auto generated, do not edit it
 *
 * 缓存监听器
 */
package data.listener;

import data.GameDataManager;
import java.util.Map;
import java.util.HashMap;


public class BaseDatingListener implements GameDataListener
{
    @Override
    public void beforeLoad(GameDataManager gameDataManager) throws Exception {
        
    }

    @Override
    public void postLoad(GameDataManager gameDataManager) throws Exception {
        Map<String, Map<Integer,data.bean.BaseDating>> baseDatingCache = gameDataManager.getInstancebaseDatingCacheKV();
	baseDatingCache.clear();


	data.container.DatingCfgContainer datingCfgContainer = gameDataManager.getInstanceDatingCfgContainer();
	// ֻ��Ϊmap
	Map<Integer,data.bean.BaseDating> datingCfgContainerCopy = new HashMap<>();
	datingCfgContainerCopy.putAll(datingCfgContainer.getMap());
	baseDatingCache.put(datingCfgContainer.TABLE_NAME, datingCfgContainerCopy);

	data.container.Dating101CfgContainer dating101CfgContainer = gameDataManager.getInstanceDating101CfgContainer();
	// ֻ��Ϊmap
	Map<Integer,data.bean.BaseDating> dating101CfgContainerCopy = new HashMap<>();
	dating101CfgContainerCopy.putAll(dating101CfgContainer.getMap());
	baseDatingCache.put(dating101CfgContainer.TABLE_NAME, dating101CfgContainerCopy);

	data.container.Dating102CfgContainer dating102CfgContainer = gameDataManager.getInstanceDating102CfgContainer();
	// ֻ��Ϊmap
	Map<Integer,data.bean.BaseDating> dating102CfgContainerCopy = new HashMap<>();
	dating102CfgContainerCopy.putAll(dating102CfgContainer.getMap());
	baseDatingCache.put(dating102CfgContainer.TABLE_NAME, dating102CfgContainerCopy);

	data.container.Dating103CfgContainer dating103CfgContainer = gameDataManager.getInstanceDating103CfgContainer();
	// ֻ��Ϊmap
	Map<Integer,data.bean.BaseDating> dating103CfgContainerCopy = new HashMap<>();
	dating103CfgContainerCopy.putAll(dating103CfgContainer.getMap());
	baseDatingCache.put(dating103CfgContainer.TABLE_NAME, dating103CfgContainerCopy);

	data.container.Dating104CfgContainer dating104CfgContainer = gameDataManager.getInstanceDating104CfgContainer();
	// ֻ��Ϊmap
	Map<Integer,data.bean.BaseDating> dating104CfgContainerCopy = new HashMap<>();
	dating104CfgContainerCopy.putAll(dating104CfgContainer.getMap());
	baseDatingCache.put(dating104CfgContainer.TABLE_NAME, dating104CfgContainerCopy);

	data.container.Dating105CfgContainer dating105CfgContainer = gameDataManager.getInstanceDating105CfgContainer();
	// ֻ��Ϊmap
	Map<Integer,data.bean.BaseDating> dating105CfgContainerCopy = new HashMap<>();
	dating105CfgContainerCopy.putAll(dating105CfgContainer.getMap());
	baseDatingCache.put(dating105CfgContainer.TABLE_NAME, dating105CfgContainerCopy);

	data.container.MainDialogueCfgContainer mainDialogueCfgContainer = gameDataManager.getInstanceMainDialogueCfgContainer();
	// ֻ��Ϊmap
	Map<Integer,data.bean.BaseDating> mainDialogueCfgContainerCopy = new HashMap<>();
	mainDialogueCfgContainerCopy.putAll(mainDialogueCfgContainer.getMap());
	baseDatingCache.put(mainDialogueCfgContainer.TABLE_NAME, mainDialogueCfgContainerCopy);

    }
}