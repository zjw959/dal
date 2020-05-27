package com.enity;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

//该接口会自动被实现，springdata已经帮我们实现了基本的增删改查、JpaSpecificationExecutor实现多条件查询
@Repository
public interface AccountPermissionInfoRepository extends CrudRepository<AccountPermissionInfo, Integer>,JpaSpecificationExecutor<AccountPermissionInfo> {
	AccountPermissionInfo findByPlayerIdAndType(Integer playerId,Integer type);

	@Transactional
	void deleteByPlayerIdAndType(Integer playerId, Integer type);

}
