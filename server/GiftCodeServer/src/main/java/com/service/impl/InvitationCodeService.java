package com.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.enity.InvitationCode;
import com.enity.InvitationCodeRepository;
import com.enity.UsedList;
import com.enity.Specification.SimpleSpecification;
import com.enity.Specification.SpecificationOperator;
import com.service.IInvitationCodeService;

@Service
public class InvitationCodeService implements IInvitationCodeService {
    private static final String RoleCacheName = "RoleCodeCache";
    private static final String CacheName = "CodeCache";
    @Autowired
    private InvitationCodeRepository codeRepository;

    @Override
    @Cacheable(value = RoleCacheName, key = "#key")
    public UsedList findByKey(Integer key) {
        int roleId = key.intValue();
        List<InvitationCode> codeList = codeRepository.findByRoleId(roleId);
        Map<String, InvitationCode> map = new ConcurrentHashMap<String, InvitationCode>();
        for (InvitationCode code : codeList) {
            map.put(code.getId(), code);
        }
        UsedList usedList = new UsedList(roleId, map);
        return usedList;
    }

    @Override
    @CacheEvict(value = RoleCacheName, key = "#key")
    public UsedList remove(Integer key) {
        return null;
    }

    @Override
    public boolean isExist(Integer key) {
        return false;
    }

    @Override
    @CachePut(value = RoleCacheName, key = "#value.getRoleId()")
    public UsedList save(UsedList value) {
        return value;
    }

    @CachePut(value = CacheName, key = "#code.getId()")
    public InvitationCode save(InvitationCode code) {
        codeRepository.save(code);
        return code;
    }

    @Cacheable(value = CacheName, key = "#key")
    public InvitationCode find(String key) {
        InvitationCode code = codeRepository.findInvitationCodeById(key);
        return code;
    }

    public List<InvitationCode> findBySql(String sql) {
        return codeRepository.findBySql(sql);
    }

    public Page<InvitationCode> getInvitationCodeList(InvitationCode template, Integer page,
            Integer size) {
        List<SpecificationOperator> opers = new ArrayList<>();
        addOpers(opers, template);
        SimpleSpecification<InvitationCode> specification =
                new SimpleSpecification<InvitationCode>(opers);
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of((page - 1), size, sort);
        Page<InvitationCode> qyPage = codeRepository.findAll(specification, pageable);

        return qyPage;
    }

    public List<InvitationCode> getInvitationCodeList(InvitationCode template) {
        List<SpecificationOperator> opers = new ArrayList<>();
        addOpers(opers, template);
        SimpleSpecification<InvitationCode> specification =
                new SimpleSpecification<InvitationCode>(opers);
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        List<InvitationCode> qyPage = codeRepository.findAll(specification, sort);

        return qyPage;
    }

    public void addOpers(List<SpecificationOperator> opers, InvitationCode template) {
        SpecificationOperator o = null;
        if (!StringUtils.isEmpty(template.getId())) {
            o = new SpecificationOperator();
            o.setKey("id");
            o.setJoin("and");
            o.setValue(template.getId());
            o.setOper("=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getPackageId()) && template.getPackageId() != 0) {
            o = new SpecificationOperator();
            o.setKey("packageId");
            o.setJoin("and");
            o.setValue(template.getPackageId());
            o.setOper("=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getRoleId()) && template.getRoleId() != 0) {
            o = new SpecificationOperator();
            o.setKey("roleId");
            o.setJoin("and");
            o.setValue(template.getRoleId());
            o.setOper("=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getCreateTime())) {
            o = new SpecificationOperator();
            o.setKey("createTime");
            o.setJoin("and");
            o.setValue(template.getCreateTime());
            o.setOper(">=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getGotTime())) {
            o = new SpecificationOperator();
            o.setKey("gotTime");
            o.setJoin("and");
            o.setValue(template.getGotTime());
            o.setOper("<=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getGot())) {
            o = new SpecificationOperator();
            o.setKey("got");
            o.setJoin("and");
            o.setValue(template.getGot());
            o.setOper("=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getBatchId())) {
            o = new SpecificationOperator();
            o.setKey("batchId");
            o.setJoin("and");
            o.setValue(template.getBatchId());
            o.setOper("=");
            opers.add(o);
        }
    }

    public void countUseByBatch(String batchId) {
        List<InvitationCode> codeList = codeRepository.findByBatchId(batchId);
        int useTotal = 0;
        for (InvitationCode code : codeList) {
            if (code.getRoleId() != 0) {
                useTotal++;
            }
        }
    }
}
