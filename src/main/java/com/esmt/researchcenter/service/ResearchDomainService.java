package com.esmt.researchcenter.service;

import com.esmt.researchcenter.model.ResearchDomain;
import com.esmt.researchcenter.repository.ResearchDomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResearchDomainService {

    @Autowired
    private ResearchDomainRepository researchDomainRepository;

    public List<ResearchDomain> getAllDomains() {
        return researchDomainRepository.findAll();
    }

    public ResearchDomain getDomainById(Long id) {
        return researchDomainRepository.findById(id).orElse(null);
    }

    public ResearchDomain saveDomain(ResearchDomain domain) {
        return researchDomainRepository.save(domain);
    }

    public void deleteDomain(Long id) {
        researchDomainRepository.deleteById(id);
    }
}
