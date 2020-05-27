package com.service.impl;

import java.util.ArrayList;
import java.util.Collection;
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

import com.enity.PackageInfo;
import com.enity.PackageInfoRepository;
import com.enity.Specification.SimpleSpecification;
import com.enity.Specification.SpecificationOperator;
import com.service.IPackageInfoService;

@Service
public class PackageInfoService implements CommandLineRunner, IPackageInfoService {
    @Autowired
    private PackageInfoRepository packRepository;

    private Map<Integer, PackageInfo> dataMap = new HashMap<Integer, PackageInfo>();

    public Collection<PackageInfo> findAll() {
        Collection<PackageInfo> packList = dataMap.values();
        if (packList.isEmpty()) {
            packList = (Collection<PackageInfo>) packRepository.findAll();
            for (PackageInfo info : packList) {
                dataMap.put(info.getId(), info);
            }
        }
        return packList;
    }

    @Override
    public void run(String... arg0) throws Exception {
        List<PackageInfo> packList = (List<PackageInfo>) packRepository.findAll();
        for (PackageInfo info : packList) {
            dataMap.put(info.getId(), info);
        }
    }


    @Override
    public PackageInfo findByKey(Integer key) {
        PackageInfo info = dataMap.get(key);
        if (info == null) {
            info = packRepository.findPackageInfoById(key);
            if (info != null) {
                dataMap.put(key, info);
            }
        }
        return info;
    }


    @Override
    public PackageInfo remove(Integer key) {
        if (!dataMap.containsKey(key)) {
            return null;
        }
        PackageInfo info = dataMap.remove(key);
        packRepository.deleteById(key);
        return info;
    }


    @Override
    public boolean isExist(Integer key) {
        return dataMap.containsKey(key);
    }


    @Override
    public PackageInfo save(PackageInfo value) {
        dataMap.put(value.getId(), value);
        packRepository.save(value);
        return value;
    }

    public Collection<PackageInfo> getList() {
        return dataMap.values();
    }

    public Page<PackageInfo> queryPackage(PackageInfo template, Integer page, Integer size) {
        List<SpecificationOperator> opers = new ArrayList<>();
        addOpers(opers, template);
        SimpleSpecification<PackageInfo> specification =
                new SimpleSpecification<PackageInfo>(opers);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of((page - 1), size, sort);
        Page<PackageInfo> qyPage = packRepository.findAll(specification, pageable);

        return qyPage;
    }

    public List<PackageInfo> queryPackage(PackageInfo template) {
        List<SpecificationOperator> opers = new ArrayList<>();
        addOpers(opers, template);
        SimpleSpecification<PackageInfo> specification =
                new SimpleSpecification<PackageInfo>(opers);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List<PackageInfo> qyPage = packRepository.findAll(specification, sort);

        return qyPage;
    }

    public void addOpers(List<SpecificationOperator> opers, PackageInfo template) {
        SpecificationOperator o = null;
        if (!StringUtils.isEmpty(template.getId())) {
            o = new SpecificationOperator();
            o.setKey("id");
            o.setJoin("and");
            o.setValue(template.getId());
            o.setOper("=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getName())) {
            o = new SpecificationOperator();
            o.setKey("name");
            o.setJoin("and");
            o.setValue(template.getName());
            o.setOper("=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getChannel())) {
            o = new SpecificationOperator();
            o.setKey("channel");
            o.setJoin("and");
            o.setValue(template.getChannel());
            o.setOper("=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getServerId())) {
            o = new SpecificationOperator();
            o.setKey("serverId");
            o.setJoin("and");
            o.setValue(template.getServerId());
            o.setOper("=");
            opers.add(o);
        }
        if (!StringUtils.isEmpty(template.getBeginTime())) {
            o = new SpecificationOperator();
            o.setKey("beginTime");
            o.setJoin("and");
            o.setValue(template.getBeginTime());
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
