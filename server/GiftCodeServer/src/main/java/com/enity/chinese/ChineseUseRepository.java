package com.enity.chinese;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChineseUseRepository
        extends CrudRepository<ChineseUse, String>, JpaSpecificationExecutor<ChineseUse> {
    ChineseUse findByRoleIdAndId(int roleId, String id);

    List<ChineseUse> findByIdAndPackageId(String id, int packageId);

    List<ChineseUse> findByPackageId(int packageId);
}
