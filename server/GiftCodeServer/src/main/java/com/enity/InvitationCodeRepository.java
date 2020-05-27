package com.enity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationCodeRepository
        extends CrudRepository<InvitationCode, String>, JpaSpecificationExecutor<InvitationCode> {
    List<InvitationCode> findByRoleId(int roleId);

    List<InvitationCode> findByBatchId(String batchId);

    InvitationCode findInvitationCodeById(String id);

    @Query(value = ":name", nativeQuery = true)
    List<InvitationCode> findBySql(@Param("name") String sql);


}
