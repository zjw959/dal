package com.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import utils.ExceptionEx;

import com.enity.AccountInfo;
import com.enity.AccountInfoRepository;


@Service
public class AccountService {
    private static final Logger log = Logger.getLogger(AccountService.class);
	@Autowired//自动从spring容器中加载userRepository
    private AccountInfoRepository accountInfoRepository;
	@Autowired
	private RedisTemplate<String, Object> redistemplate;
	
	public AccountService() {
		super();
	}

	@Cacheable(key = "#accountId+'-'+#channelId",value = "account")
	public AccountInfo findByAccountIdAndChannel(String accountId,String channelId){
        long t = System.currentTimeMillis();
        boolean isNew = false;
		AccountInfo info = accountInfoRepository.findByAccountIdAndChannelId(accountId, channelId);
		if(info==null){
            isNew = true;
			info = new AccountInfo();
			info.setPlayerId(0);
			info.setAccountId(accountId);
			info.setChannelId(channelId);
			info.setCreateTime(new Date());
			info.setServerId(0);
			accountInfoRepository.save(info);
		}
        log.info("-----获取账号信息耗时：" + (System.currentTimeMillis() - t) + "ms isNew:" + isNew
                + "  accountId:" + accountId + " channelId:" + channelId);
		return info;
	}
	
	@Cacheable(key = "#accountId+'-'+#channelId",value = "account", unless="#result == null")
	public AccountInfo findByAccountIdAndChannelNotCreate(String accountId, String channelId) {
		AccountInfo info = accountInfoRepository.findByAccountIdAndChannelId(accountId, channelId);
		return info;
	}
	
	@CachePut(key = "#accountInfo.getAccountId()+'-'+#accountInfo.getChannelId()" , value = "account")
	public AccountInfo putAccountIdAndChannel(AccountInfo accountInfo){
		accountInfoRepository.save(accountInfo);
		return accountInfo;
	}
	
	@Cacheable(key = "#token" , value = "token", unless="#result == null")
	public AccountInfo getAccountByToken(String token) {
		return null;
	}
	
	@CachePut(key = "#accountInfo.getToken()" , value = "token")
	public AccountInfo putAccountByToken(AccountInfo accountInfo) {
		return accountInfo;
	}
	
	//暂时没有找到springcache注解指定hash结构存储的方法
//	@CachePut(key = "'loc-'+#playerId" , value = "loc")
	public Integer putServerIdByPlayerId(int playerId,int serverId) {
		redistemplate.opsForHash().put("loc::"+playerId, "serverId", serverId);
		redistemplate.opsForHash().put("loc::"+playerId, "time", System.currentTimeMillis());
		return serverId;
	}

	public Integer getLocServerId(Integer playerId) {
		Integer sid = -1;
		try {
			sid = (Integer)redistemplate.opsForHash().get
					("loc::"+playerId, "serverId");
		} catch (Exception e) {
            log.error(ExceptionEx.e2s(e));
		}
		if(sid==null){
			sid = -1;
		}
		return sid;
	}
	
	
	@Cacheable(key = "#playerId",value = "playerId", unless="#result == null")
	public AccountInfo findByPlayerId(Integer playerId) {
		AccountInfo info = accountInfoRepository.findByPlayerId(playerId);
		return info;
	}

	public List<AccountInfo> findByMobile(String mobile) {
		List<AccountInfo> list = accountInfoRepository.findByMobile(mobile);
		return list;
	}
}
