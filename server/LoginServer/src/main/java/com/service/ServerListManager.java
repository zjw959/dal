package com.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.hutool.core.util.RandomUtil;

import com.constant.ServerType;
import com.enity.ServerGroup;
import com.enity.ServerGroupRepository;
import com.enity.ServerInfo;
import com.enity.ServerInfoRepository;

@Service
public class ServerListManager implements CommandLineRunner{
	@Autowired
	private ServerInfoRepository serverInfoRepository;
	@Autowired
	private ServerGroupRepository serverGroupRepository;
	@Autowired
	private RedisTemplate<String, Object> redistemplate;
    private static final Logger log = Logger.getLogger(ServerListManager.class);
	private static String ALL_SERVER_INFO = "allServerInfo" ;
	private static String ALL_SERVER_GROUP = "allServerGroup" ;
	
	public static int open_status = 0;//开启状态
	public static int weihu_status = 1;//维护状态
	
    public static int distribute_degree = 500;// 服务器在线人数分发档位差（每档之间的差值）

	/**
	 * 根据名字获取服务器信息
	 * @param serverName   服务器名称（唯一）
	 * @return	ServerInfo
	 */
	public ServerInfo getServerByName(String serverName) {
		List<ServerInfo> all_server = getAllServerInfo();
		ServerInfo serverInfo = null;
		for(ServerInfo info : all_server){
			if(info.getName().equals(serverName)){
				serverInfo = info;
			}
		}
		if(serverInfo==null){
			serverInfo = serverInfoRepository.findByName(serverName);
			if(serverInfo!=null){
				putServerInfo(serverInfo);
			}
		}
		return serverInfo;
	}

	/**
	 * 根据组名查询分组
	 * @param groupName
	 * @return
	 */
	public ServerGroup getServerGroupByName(String groupName) {
		List<ServerGroup> all_group = getAllServerGroup();
		ServerGroup group = null;
		for(ServerGroup info : all_group){
			if(info.getName().equals(groupName)){
				group = info;
                break;
			}
		}
		if(group==null){
			group = serverGroupRepository.findByName(groupName);
			if(group!=null){
				putServerGroup(group);
			}
		}
		return group;
	}
	
	/**
	 * 根据分组ID查询分组
	 * @param groupName
	 * @return
	 */
	public ServerGroup getServerGroupById(Integer groupId) {
		ServerGroup serverGroup = (ServerGroup)redistemplate.opsForHash().get(ALL_SERVER_GROUP, String.valueOf(groupId));
		if(serverGroup==null){
			serverGroup = serverGroupRepository.findById(groupId).orElse(null);
			if(serverGroup!=null){
				putServerGroup(serverGroup);
			}
		}
		return serverGroup;
	}
	
	/**
	 * 获取所有服务器信息
	 * @return
	 */
	public List<ServerInfo> getAllServerInfo() {
		List<ServerInfo> all_server = new ArrayList<ServerInfo>();
		List<Object> list = redistemplate.opsForHash().values(ALL_SERVER_INFO);
		for(Object ob:list){
			ServerInfo info = (ServerInfo) ob;
			all_server.add(info);
		}
		return all_server;
	}
	
	/**
	 * 获取所有可用服务器
	 * @return
	 */
    public List<ServerInfo> getUsingServerInfo(boolean isWhiteUser) {
        long now = System.currentTimeMillis();
		List<Object> list = redistemplate.opsForHash().values(ALL_SERVER_INFO);
		List<ServerInfo> using_server = new ArrayList<ServerInfo>();
		for(Object ob:list){
			ServerInfo info = (ServerInfo)ob;
            if (info.getType() != ServerType.GAME_SERVER) {// 跳过非游戏服
                continue;
            }
            if (info.getMark() == weihu_status && !isWhiteUser) {// 服务器维护状态
				continue;
			}
			ServerGroup group = getServerGroupById(info.getServerGroup());
            if (group != null && group.getMark() == weihu_status && !isWhiteUser) {// 所在分组处于维护状态
			    continue;
			}
            // if(info.getOpenTime()!=null&&info.getOpenTime().getTime()>now){
            // continue;
            // }
			//新增逻辑   如果长时间没有收到游戏服务器通知当前人数  就从可用服务器中移除
            if (info.getOnlineNumTime() > 0 && now - info.getOnlineNumTime() > 5 * 60 * 1000) {
				continue;
			}
            // log.info("---可用服务器 server:" + info.getId() + " group:" + info.getServerGroup());
			using_server.add(info);
		}
		return using_server;
	}
	
	/**
	 * 获取所有分组信息
	 * @return
	 */
	public List<ServerGroup> getAllServerGroup() {
		List<ServerGroup> all_group = new ArrayList<ServerGroup>();
		List<Object> list = redistemplate.opsForHash().values(ALL_SERVER_GROUP);
		for(Object ob:list){
			ServerGroup info = (ServerGroup) ob;
			all_group.add(info);
		}
		return all_group;
	}

	public ServerInfo getServerById(Integer serverId) {
		ServerInfo serverInfo = null;
		serverInfo = (ServerInfo) redistemplate.opsForHash().get(ALL_SERVER_INFO, String.valueOf(serverId));
		if(serverInfo==null){
			serverInfo = serverInfoRepository.findById(serverId).orElse(null);
			if(serverInfo!=null){
				putServerInfo(serverInfo);
			}
		}
		return serverInfo;
	}

    public ServerInfo getServerByIpAndPort(String gameServerInternalIp, int gameServerTcpPort) {
        List<ServerInfo> all_server = getAllServerInfo();
        ServerInfo serverInfo = null;
        for (ServerInfo info : all_server) {
            if (info.getGameServerInternalIp().equals(gameServerInternalIp)
                    && gameServerTcpPort == info.getGameServerTcpPort()) {
                serverInfo = info;
                break;
            }
        }
        if (serverInfo == null) {
            serverInfo =
                    serverInfoRepository.findByGameServerInternalIpAndGameServerTcpPort(
                            gameServerInternalIp,
                            gameServerTcpPort);
            if (serverInfo != null) {
                putServerInfo(serverInfo);
            }
        }
        return serverInfo;
    }

    public int getGameServerCount(int groupId) {
        List<ServerInfo> all_server = getAllServerInfo();
        int count = 0;
        for (ServerInfo info : all_server) {
            if (info.getServerGroup() == groupId && info.getType() == ServerType.GAME_SERVER) {
                count++;
            }
        }
        return count;
    }

	public void saveServer(ServerInfo serverInfo) {
		putServerInfo(serverInfo);
		serverInfoRepository.save(serverInfo);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("开始清除redis上旧的服务器数据");
		Set<Object> serverInfoKey = redistemplate.opsForHash().keys(ALL_SERVER_INFO);
		for(Object o :serverInfoKey){
			redistemplate.opsForHash().delete(ALL_SERVER_INFO, (String)o);
		}
		Set<Object> groupKey = redistemplate.opsForHash().keys(ALL_SERVER_GROUP);
		for(Object o :groupKey){
			redistemplate.opsForHash().delete(ALL_SERVER_GROUP, (String)o);
		}
		log.info("清除redis上服务器数据成功,添加最新的服务器数据");
		Iterable<ServerInfo> all_server = serverInfoRepository.findAll();
		for(ServerInfo info : all_server){
			putServerInfo(info);
		}
		Iterable<ServerGroup> all_group = serverGroupRepository.findAll();
		for(ServerGroup info : all_group){
			putServerGroup(info);
		}
		log.info("添加最新的服务器数据成功");
	}
	
	public void putServerInfo(ServerInfo serverInfo){
		redistemplate.opsForHash().put(ALL_SERVER_INFO, String.valueOf(serverInfo.getId()), serverInfo);
	}
	
	public void putServerGroup(ServerGroup serverGroup){
		redistemplate.opsForHash().put(ALL_SERVER_GROUP, String.valueOf(serverGroup.getId()), serverGroup);
	}

	public boolean saveServerGroup(ServerGroup serverGroup) {
		serverGroupRepository.save(serverGroup);
		putServerGroup(serverGroup);
		return true;
	}

	/**
	 * 修改服务器在线人数
	 * @param serverId
	 * @param onlineNum
	 */
	public void updateOnlineNum(Integer serverId, Integer onlineNum) {
		ServerInfo serverInfo = getServerById(serverId);
		serverInfo.setOnlineNum(onlineNum);
		serverInfo.setOnlineNumTime(System.currentTimeMillis());
		putServerInfo(serverInfo);
	}
	
	    /**
     * 获得该分组最少在线人数的服务器
     * 
     * @param serverGroupId
     * @return
     */
    public ServerInfo getMinOnlineNumServer(int serverGroupId, boolean isWhiteUser) {
        List<ServerInfo> list = getServerListByGroupId(serverGroupId, isWhiteUser);
        if (list.isEmpty()) {
            return null;
        }
        int min = Integer.MAX_VALUE;
        List<ServerInfo> servers = new ArrayList<ServerInfo>();
        for (ServerInfo info : list) {
            if (info.getOnlineNumDegree() < min) {
                min = info.getOnlineNumDegree();
                servers.clear();
                servers.add(info);
            } else if (info.getOnlineNumDegree() == min) {
                servers.add(info);
            }
        }
        ServerInfo minServer = null;
        int size = servers.size();
        if (size > 0) {
            minServer = servers.get(RandomUtil.randomInt(0, size));// 从人数最低的档位里随机抽取一个服务器
        }
        return minServer;
	}

    /**
     * 根据服务器名字获取所有该分组下面服务器 如果serverGroupId==0 则获取所有可用服务器
     * 
     * @param serverGroupId
     * @return
     */
    public List<ServerInfo> getServerListByGroupId(int serverGroupId, boolean isWhiteUser) {
        List<ServerInfo> using_list = getUsingServerInfo(isWhiteUser);
		List<ServerInfo> group_serverInfo = new ArrayList<ServerInfo>();
		for(ServerInfo info:using_list){
            if (serverGroupId == 0 || info.getServerGroup() == serverGroupId) {
				group_serverInfo.add(info);
			}
		}
		return group_serverInfo;
	}
	
}
