package com.service.impl;

import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.enity.forever.ForeverUse;
import com.enity.forever.ForeverUseRepository;
import com.service.IForeverUseService;

@Service
public class ForeverUseService implements IForeverUseService {
    private static final String CacheName = "ForeverCodeCache";
    @Autowired
    ForeverUseRepository foreverUseRepository;

    @Override
    @Cacheable(value = CacheName, key = "#key")
    public ForeverUse findByKey(String key) {
        StringTokenizer tokenizer = new StringTokenizer(key, ":");
        String id = tokenizer.nextToken();
        int roleId = Integer.parseInt(tokenizer.nextToken());
        ForeverUse foreverUse = foreverUseRepository.findByRoleIdAndId(roleId, id);
        return foreverUse;
    }

    @Override
    public ForeverUse remove(String key) {
        return null;
    }

    @Override
    public boolean isExist(String key) {
        return false;
    }

    @Override
    @CachePut(value = CacheName, key = "#value.getIdAndRoleIdStr()")
    public ForeverUse save(ForeverUse value) {
        foreverUseRepository.save(value);
        return value;
    }


}
