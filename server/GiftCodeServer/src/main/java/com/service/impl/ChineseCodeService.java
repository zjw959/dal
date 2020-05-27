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

import com.controller.helper.QueryChineseCode;
import com.enity.Specification.SimpleSpecification;
import com.enity.Specification.SpecificationOperator;
import com.enity.chinese.ChineseCode;
import com.enity.chinese.ChineseCodeRepository;
import com.service.IChineseCodeService;

@Service
public class ChineseCodeService implements CommandLineRunner, IChineseCodeService {
    @Autowired
    private ChineseCodeRepository chineseCodeRepository;


    private Map<String, ChineseCode> dataMap = new HashMap<>();

    @Override
    public ChineseCode findByKey(String key) {
        ChineseCode info = dataMap.get(key);
        // if (info == null) {
        // info = chineseCodeRepository.findChineseCodeInfoById(key);
        // if (info != null) {
        // dataMap.put(key, info);
        // }
        // }
        return info;
    }

    @Override
    public ChineseCode remove(String key) {
        if (!dataMap.containsKey(key)) {
            return null;
        }
        ChineseCode info = dataMap.remove(key);
        chineseCodeRepository.deleteById(key);
        return info;
    }

    @Override
    public boolean isExist(String key) {
        return dataMap.containsKey(key);
    }

    @Override
    public ChineseCode save(ChineseCode value) {
        dataMap.put(value.getId(), value);
        chineseCodeRepository.save(value);
        return value;
    }

    @Override
    public void run(String... args) throws Exception {
        List<ChineseCode> chineseCodeList = (List<ChineseCode>) chineseCodeRepository.findAll();
        for (ChineseCode info : chineseCodeList) {
            dataMap.put(info.getId(), info);
        }
    }

    public Page<ChineseCode> queryChineseCode(QueryChineseCode template, Integer page,
            Integer size) {
        List<SpecificationOperator> opers = new ArrayList<>();
        addOpers(opers, template);
        SimpleSpecification<ChineseCode> specification =
                new SimpleSpecification<ChineseCode>(opers);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of((page - 1), size, sort);
        Page<ChineseCode> qyPage = chineseCodeRepository.findAll(specification, pageable);

        return qyPage;
    }

    /**
     * 不分页
     */
    public List<ChineseCode> queryChineseCode(QueryChineseCode template) {
        List<SpecificationOperator> opers = new ArrayList<>();
        addOpers(opers, template);
        SimpleSpecification<ChineseCode> specification =
                new SimpleSpecification<ChineseCode>(opers);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List<ChineseCode> qyPage = chineseCodeRepository.findAll(specification, sort);
        return qyPage;
    }

    public void addOpers(List<SpecificationOperator> opers, QueryChineseCode template) {
        SpecificationOperator o = null;
        if (template.getPackId() != 0) {
            o = new SpecificationOperator();
            o.setKey("packageId");
            o.setJoin("and");
            o.setValue(template.getPackId());
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

    }
}
