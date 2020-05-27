package com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.enity.AccountPermissionInfo;
import com.enity.AccountPermissionInfoRepository;
import com.enity.Specification.SimpleSpecificationBuilder;


@Service
public class AccountPermissionService {
	@Autowired
    private AccountPermissionInfoRepository permissionInfoRepository;
	
	public static int ban=1;//封号
	public static int gag=2;//禁言
	public static int white_user=3;//白名单
	
	public AccountPermissionService() {
		super();
	}

	@Cacheable(key = "#playerId+'-'+#type",value = "ban", unless="#result == null")
	public AccountPermissionInfo findByPlayerIdAndType(Integer playerId,Integer type){
		AccountPermissionInfo info = permissionInfoRepository.findByPlayerIdAndType(playerId,type);
		return info;
	}
	
	@CachePut(key = "#permissionInfo.getPlayerId()+'-'+#permissionInfo.getType()" , value = "ban")
	public AccountPermissionInfo putAccountPermission(AccountPermissionInfo permissionInfo){
		permissionInfoRepository.save(permissionInfo);
		return permissionInfo;
	}
	
	@CacheEvict(key = "#playerId+'-'+#type" , value = "ban")
	public void removeAccountPermission(Integer playerId,Integer type){
		permissionInfoRepository.deleteByPlayerIdAndType(playerId,type);
	}

	public List<AccountPermissionInfo> queryList(String size,String page,String playerIdStr, String typeStr, String startTimeStr, String endTimeStr, String reason,
			String operator) {
		SimpleSpecificationBuilder<AccountPermissionInfo> builder = new SimpleSpecificationBuilder<AccountPermissionInfo>();
		if(playerIdStr!=null&&!playerIdStr.isEmpty()){
			builder.add("playerId","=",playerIdStr);
		}
		if(typeStr!=null&&!typeStr.isEmpty()){
			builder.add("type","=",typeStr);
		}
		if(startTimeStr!=null&&!startTimeStr.isEmpty()){
			builder.add("startTime",">",startTimeStr);
		}
		if(endTimeStr!=null&&!endTimeStr.isEmpty()){
			builder.add("endTime","<",endTimeStr);
		}
		if(reason!=null&&!reason.isEmpty()){
			builder.add("reason","=",reason);
		}
		if(operator!=null&&!operator.isEmpty()){
			builder.add("operator","=",operator);
		}
		Pageable pageRequest = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
		Page<AccountPermissionInfo> pageInfo = permissionInfoRepository.findAll(builder.generateSpecification(),pageRequest);
		List<AccountPermissionInfo> list = pageInfo.getContent();
		return list;
	}
	
	
	
}
