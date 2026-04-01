package com.esmt.researchcenter.repository;

import com.esmt.researchcenter.model.ResearchDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResearchDomainRepository extends JpaRepository<ResearchDomain, Long> {
    Optional<ResearchDomain> findByName(String name);
}
