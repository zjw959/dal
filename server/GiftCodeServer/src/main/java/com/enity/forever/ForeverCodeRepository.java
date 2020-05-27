package com.enity.forever;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForeverCodeRepository extends CrudRepository<ForeverCode, String> {
    ForeverCode findForeverCodeInfoById(String id);
}
