package com.enity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//该接口会自动被实现，springdata已经帮我们实现了基本的增删改查
@Repository
public interface ServerGroupRepository extends CrudRepository<ServerGroup, Integer> {
	
	ServerGroup findByName(String name);
}
