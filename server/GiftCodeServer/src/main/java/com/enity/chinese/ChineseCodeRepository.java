package com.enity.chinese;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChineseCodeRepository
        extends CrudRepository<ChineseCode, String>, JpaSpecificationExecutor<ChineseCode> {
    ChineseCode findChineseCodeInfoById(String id);
}
