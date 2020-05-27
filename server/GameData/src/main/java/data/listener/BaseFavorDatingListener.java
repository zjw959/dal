/**
 * Auto generated, do not edit it
 *
 * 缓存监听器
 */
package data.listener;

import data.GameDataManager;
import java.util.Map;
import java.util.HashMap;


public class BaseFavorDatingListener implements GameDataListener
{
    @Override
    public void beforeLoad(GameDataManager gameDataManager) throws Exception {
        
    }

    @Override
    public void postLoad(GameDataManager gameDataManager) throws Exception {
        Map<String, Map<Integer,data.bean.BaseFavorDating>> baseFavorDatingCache = gameDataManager.getInstancebaseFavorDatingCacheKV();
	baseFavorDatingCache.clear();


	data.container.FavorDating101CfgContainer favorDating101CfgContainer = gameDataManager.getInstanceFavorDating101CfgContainer();
	// ֻ��Ϊmap
	Map<Integer,data.bean.BaseFavorDating> favorDating101CfgContainerCopy = new HashMap<>();
	favorDating101CfgContainerCopy.putAll(favorDating101CfgContainer.getMap());
	baseFavorDatingCache.put(favorDating101CfgContainer.TABLE_NAME, favorDating101CfgContainerCopy);

	data.container.FavorDating102CfgContainer favorDating102CfgContainer = gameDataManager.getInstanceFavorDating102CfgContainer();
	// ֻ��Ϊmap
	Map<Integer,data.bean.BaseFavorDating> favorDating102CfgContainerCopy = new HashMap<>();
	favorDating102CfgContainerCopy.putAll(favorDating102CfgContainer.getMap());
	baseFavorDatingCache.put(favorDating102CfgContainer.TABLE_NAME, favorDating102CfgContainerCopy);

	data.container.FavorDating103CfgContainer favorDating103CfgContainer = gameDataManager.getInstanceFavorDating103CfgContainer();
	// ֻ��Ϊmap
	Map<Integer,data.bean.BaseFavorDating> favorDating103CfgContainerCopy = new HashMap<>();
	favorDating103CfgContainerCopy.putAll(favorDating103CfgContainer.getMap());
	baseFavorDatingCache.put(favorDating103CfgContainer.TABLE_NAME, favorDating103CfgContainerCopy);

	data.container.FavorDating104CfgContainer favorDating104CfgContainer = gameDataManager.getInstanceFavorDating104CfgContainer();
	// ֻ��Ϊmap
	Map<Integer,data.bean.BaseFavorDating> favorDating104CfgContainerCopy = new HashMap<>();
	favorDating104CfgContainerCopy.putAll(favorDating104CfgContainer.getMap());
	baseFavorDatingCache.put(favorDating104CfgContainer.TABLE_NAME, favorDating104CfgContainerCopy);

	data.container.FavorDating105CfgContainer favorDating105CfgContainer = gameDataManager.getInstanceFavorDating105CfgContainer();
	// ֻ��Ϊmap
	Map<Integer,data.bean.BaseFavorDating> favorDating105CfgContainerCopy = new HashMap<>();
	favorDating105CfgContainerCopy.putAll(favorDating105CfgContainer.getMap());
	baseFavorDatingCache.put(favorDating105CfgContainer.TABLE_NAME, favorDating105CfgContainerCopy);

    }
}