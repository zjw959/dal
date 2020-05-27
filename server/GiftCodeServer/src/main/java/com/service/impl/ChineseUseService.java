package com.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.controller.helper.QueryChineseUse;
import com.enity.Specification.SimpleSpecification;
import com.enity.Specification.SpecificationOperator;
import com.enity.chinese.ChineseUse;
import com.enity.chinese.ChineseUseRepository;
import com.service.IChineseUseService;

@Service
public class ChineseUseService implements IChineseUseService {
    private static final String CacheName = "ChineseCodeCache";
    @Autowired
    ChineseUseRepository chineseUseRepository;

    @Override
    @Cacheable(value = CacheName, key = "#key")
    public ChineseUse findByKey(String key) {
        StringTokenizer tokenizer = new StringTokenizer(key, ":");
        String id = tokenizer.nextToken();
        int roleId = Integer.parseInt(tokenizer.nextToken());
        ChineseUse chineseUse = chineseUseRepository.findByRoleIdAndId(roleId, id);
        return chineseUse;
    }

    @Cacheable(value = CacheName, key = "#key")
    public List<ChineseUse> findByList(Integer key) {
        // List<ChineseUse> list = chineseUseRepository.findByIdAndPackageId(id, packageId);
        List<ChineseUse> list = chineseUseRepository.findByPackageId(key);
        return list;
    }

    @Override
    public ChineseUse remove(String key) {
        return null;
    }

    @Override
    public boolean isExist(String key) {
        return false;
    }

    @Override
    @CachePut(value = CacheName, key = "#value.getIdAndRoleIdStr()")
    public ChineseUse save(ChineseUse value) {
        chineseUseRepository.save(value);
        return value;
    }

    public Page<ChineseUse> queryChineseCode(QueryChineseUse template, Integer page, Integer size) {
        List<SpecificationOperator> opers = new ArrayList<>();
        addOpers(opers, template);
        SimpleSpecification<ChineseUse> specification = new SimpleSpecification<ChineseUse>(opers);
        Sort sort = new Sort(Sort.Direction.DESC, "roleId");
        Pageable pageable = PageRequest.of((page - 1), size, sort);
        Page<ChineseUse> qyPage = chineseUseRepository.findAll(specification, pageable);

        return qyPage;
    }

    /**
     * 不分页
     */
    public List<ChineseUse> queryChineseCode(QueryChineseUse template) {
        List<SpecificationOperator> opers = new ArrayList<>();
        addOpers(opers, template);
        SimpleSpecification<ChineseUse> specification = new SimpleSpecification<ChineseUse>(opers);
        Sort sort = new Sort(Sort.Direction.DESC, "roleId");
        List<ChineseUse> qyPage = chineseUseRepository.findAll(specification, sort);

        return qyPage;
    }

    public void addOpers(List<SpecificationOperator> opers, QueryChineseUse template) {
        SpecificationOperator o = null;
        if (template.getPackageId() != 0) {
            o = new SpecificationOperator();
            o.setKey("packageId");
            o.setJoin("and");
            o.setValue(template.getPackageId());
            o.setOper("=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getId())) {
            o = new SpecificationOperator();
            o.setKey("id");
            o.setJoin("and");
            o.setValue(template.getId());
            o.setOper("=");
            opers.add(o);
        }
        if (template.getRoleId() != 0) {
            o = new SpecificationOperator();
            o.setKey("roleId");
            o.setJoin("and");
            o.setValue(template.getRoleId());
            o.setOper("=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getStarTime())) {
            o = new SpecificationOperator();
            o.setKey("starTime");
            o.setJoin("and");
            o.setValue(template.getStarTime());
            o.setOper(">=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getEndTime())) {
            o = new SpecificationOperator();
            o.setKey("endTime");
            o.setJoin("and");
            o.setValue(template.getEndTime());
            o.setOper("<=");
            opers.add(o);
        }
    }

}
