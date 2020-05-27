package com.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.enity.forever.ForeverCode;
import com.enity.forever.ForeverCodeRepository;
import com.service.IForeverCodeService;

@Service
public class ForeverCodeService implements IForeverCodeService, CommandLineRunner {
    @Autowired
    ForeverCodeRepository foreverCodeRepository;

    private Map<String, ForeverCode> dataMap = new HashMap<>();

    @Override
    public ForeverCode findByKey(String key) {
        ForeverCode info = dataMap.get(key);
        // if (info == null) {
        // info = foreverCodeRepository.findForeverCodeInfoById(key);
        // if (info != null) {
        // dataMap.put(key, info);
        // }
        // }
        return info;
    }

    @Override
    public ForeverCode remove(String key) {
        if (!dataMap.containsKey(key)) {
            return null;
        }
        ForeverCode info = dataMap.remove(key);
        foreverCodeRepository.deleteById(key);
        return info;
    }

    @Override
    public boolean isExist(String key) {
        return dataMap.containsKey(key);
    }

    @Override
    public ForeverCode save(ForeverCode value) {
        dataMap.put(value.getId(), value);
        foreverCodeRepository.save(value);
        return value;
    }

    @Override
    public void run(String... args) throws Exception {
        List<ForeverCode> chineseCodeList = (List<ForeverCode>) foreverCodeRepository.findAll();
        for (ForeverCode info : chineseCodeList) {
            dataMap.put(info.getId(), info);
        }
    }



}
