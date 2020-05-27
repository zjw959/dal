package com.enity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//该接口会自动被实现，springdata已经帮我们实现了基本的增删改查
@Repository
public interface ServerInfoRepository extends CrudRepository<ServerInfo, Integer> {
	ServerInfo findByName(String name);

    ServerInfo findByGameServerInternalIpAndGameServerTcpPort(String game_server_internal_ip,
            int game_server_tcp_port);
}
