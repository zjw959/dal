package com.enity;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;


public interface PackageInfoRepository
        extends CrudRepository<PackageInfo, Integer>, JpaSpecificationExecutor<PackageInfo> {
    PackageInfo findPackageInfoById(Integer id);

}
