package com.enity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//该接口会自动被实现，springdata已经帮我们实现了基本的增删改查
@Repository
public interface AccountInfoRepository extends CrudRepository<AccountInfo, Integer> {
	AccountInfo findByAccountIdAndChannelId(String accountId,String channelId);
	
	AccountInfo findByPlayerId(Integer playerId);

	List<AccountInfo> findByMobile(String mobile);
}
