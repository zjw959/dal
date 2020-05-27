package com.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.controller.helper.QueryBatch;
import com.enity.InvitationCodeRepository;
import com.enity.Specification.SimpleSpecification;
import com.enity.Specification.SpecificationOperator;
import com.enity.batch.BatchInfo;
import com.enity.batch.BatchInfoRepository;
import com.service.IBatchInfoService;

import util.TimeUtil;

@Service
public class BatchInfoService implements CommandLineRunner, IBatchInfoService {
    @Autowired
    private BatchInfoRepository batchInfoRepository;
    @Autowired
    private InvitationCodeRepository invitationCodeRepository;

    private Map<String, BatchInfo> dataMap = new HashMap<>();

    @Override
    public BatchInfo findByKey(String key) {
        BatchInfo info = dataMap.get(key);
        return info;
    }

    @Override
    public BatchInfo remove(String key) {
        return null;
    }

    @Override
    public boolean isExist(String key) {
        return dataMap.containsKey(key);
    }

    @Override
    public BatchInfo save(BatchInfo value) {
        dataMap.put(value.getBatchId(), value);
        batchInfoRepository.save(value);
        return value;
    }

    @Override
    public void run(String... args) throws Exception {
        List<BatchInfo> batchInfoList = (List<BatchInfo>) batchInfoRepository.findAll();
        for (BatchInfo info : batchInfoList) {
            dataMap.put(info.getBatchId(), info);
        }
    }

    public Page<BatchInfo> query(QueryBatch template, Integer page, Integer size) {
        List<SpecificationOperator> opers = new ArrayList<>();
        addOpers(opers, template);
        SimpleSpecification<BatchInfo> specification = new SimpleSpecification<BatchInfo>(opers);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of((page - 1), size, sort);
        Page<BatchInfo> qyPage = batchInfoRepository.findAll(specification, pageable);

        return qyPage;
    }

    public void addOpers(List<SpecificationOperator> opers, QueryBatch template) {
        SpecificationOperator o = null;
        if (!StringUtils.isEmpty(template.getBatchId())) {
            o = new SpecificationOperator();
            o.setKey("batchId");
            o.setJoin("and");
            o.setValue(template.getBatchId());
            o.setOper("=");
            opers.add(o);
        }
        if (template.getId() != 0) {
            o = new SpecificationOperator();
            o.setKey("id");
            o.setJoin("and");
            o.setValue(template.getId());
            o.setOper("=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getStartTime())) {
            o = new SpecificationOperator();
            o.setKey("createTime");
            o.setJoin("and");
            o.setValue(TimeUtil.parse(template.getStartTime()));
            o.setOper(">=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getEndTime())) {
            o = new SpecificationOperator();
            o.setKey("createTime");
            o.setJoin("and");
            o.setValue(TimeUtil.parse(template.getEndTime()));
            o.setOper("<=");
            opers.add(o);
        }
    }

}
