package com.enity.forever;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForeverUseRepository extends CrudRepository<ForeverUse, String> {
    ForeverUse findByRoleIdAndId(int roleId, String id);
}
