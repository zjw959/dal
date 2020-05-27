package com.enity.batch;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;


public interface BatchInfoRepository
        extends CrudRepository<BatchInfo, String>, JpaSpecificationExecutor<BatchInfo> {

    BatchInfo findByBatchId(String batchId);
}
