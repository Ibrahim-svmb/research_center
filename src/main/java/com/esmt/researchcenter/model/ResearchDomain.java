package com.esmt.researchcenter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "research_domains")
public class ResearchDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // IA, Santé, Energie, Télécom...
    
    private String description;

    @OneToMany(mappedBy = "researchDomain")
    @JsonIgnore
    private List<Project> projects;

    public ResearchDomain() {
    }

    public ResearchDomain(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ResearchDomain(Long id, String name, String description, List<Project> projects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.projects = projects;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
