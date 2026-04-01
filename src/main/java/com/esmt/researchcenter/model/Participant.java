package com.esmt.researchcenter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@PrimaryKeyJoinColumn(name = "id_user")
public class Participant extends User implements Serializable {

    @ManyToMany
    @JoinTable(
        name = "participant_domains",
        joinColumns = @JoinColumn(name = "participant_id"),
        inverseJoinColumns = @JoinColumn(name = "research_domain_id")
    )
    private Set<ResearchDomain> researchDomains = new HashSet<>();

    @OneToMany(mappedBy = "responsable")
    @JsonIgnore
    private List<Project> managedProjects;

    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private List<Project> contributedProjects;

    public Participant() {
    }

    public Participant(Long id, String username, String password, String lastName, String firstName, TypeUser role, String provider, String providerId, List<Project> managedProjects, List<Project> contributedProjects, java.util.Set<ResearchDomain> researchDomains) {
        super(id, username, password, lastName, firstName, role, provider, providerId);
        this.managedProjects = managedProjects;
        this.contributedProjects = contributedProjects;
        this.researchDomains = researchDomains;
    }

    public Set<ResearchDomain> getResearchDomains() {
        return researchDomains;
    }

    public void setResearchDomains(Set<ResearchDomain> researchDomains) {
        this.researchDomains = researchDomains;
    }

    public List<Project> getManagedProjects() {
        return managedProjects;
    }

    public void setManagedProjects(List<Project> managedProjects) {
        this.managedProjects = managedProjects;
    }

    public List<Project> getContributedProjects() {
        return contributedProjects;
    }

    public void setContributedProjects(List<Project> contributedProjects) {
        this.contributedProjects = contributedProjects;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", researchDomains='" + researchDomains + '\'' +
                '}';
    }
}
