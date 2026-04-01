package com.esmt.researchcenter.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long project_id;

    private String titre_projet;
    @ManyToOne
    @JoinColumn(name = "research_domain_id")
    private ResearchDomain researchDomain;

    private String description;
    
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private LocalDate date_debut;
    private LocalDate date_fin;

    private Float budget_estime;
    private String institution;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private Participant responsable;

    @ManyToMany
    @JoinTable(
        name = "project_participants",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private List<Participant> participants;

    private Integer niveau_avancement; // in percentage

    public Project() {
    }

    public Project(Long project_id, String titre_projet, ResearchDomain researchDomain, String description, ProjectStatus status, LocalDate date_debut, LocalDate date_fin, Float budget_estime, String institution, Participant responsable, List<Participant> participants, Integer niveau_avancement) {
        this.project_id = project_id;
        this.titre_projet = titre_projet;
        this.researchDomain = researchDomain;
        this.description = description;
        this.status = status;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.budget_estime = budget_estime;
        this.institution = institution;
        this.responsable = responsable;
        this.participants = participants;
        this.niveau_avancement = niveau_avancement;
    }

    public Long getProject_id() {
        return project_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public String getTitre_projet() {
        return titre_projet;
    }

    public void setTitre_projet(String titre_projet) {
        this.titre_projet = titre_projet;
    }

    public ResearchDomain getResearchDomain() {
        return researchDomain;
    }

    public void setResearchDomain(ResearchDomain researchDomain) {
        this.researchDomain = researchDomain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public LocalDate getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(LocalDate date_debut) {
        this.date_debut = date_debut;
        calculateNiveauAvancement();
    }

    public LocalDate getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(LocalDate date_fin) {
        this.date_fin = date_fin;
        calculateNiveauAvancement();
    }

    public Float getBudget_estime() {
        return budget_estime;
    }

    public void setBudget_estime(Float budget_estime) {
        this.budget_estime = budget_estime;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public Participant getResponsable() {
        return responsable;
    }

    public void setResponsable(Participant responsable) {
        this.responsable = responsable;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public Integer getNiveau_avancement() {
        // Return stored value or calculate if null
        if (this.niveau_avancement == null) {
            calculateNiveauAvancement();
        }
        return this.niveau_avancement;
    }

    public void setNiveau_avancement(Integer niveau_avancement) {
        this.niveau_avancement = niveau_avancement;
    }

    private void calculateNiveauAvancement() {
        if (date_debut == null || date_fin == null) {
            this.niveau_avancement = 0;
            return;
        }
        LocalDate now = LocalDate.now();
        if (now.isBefore(date_debut)) {
            this.niveau_avancement = 0;
            return;
        }
        if (now.isAfter(date_fin)) {
            this.niveau_avancement = 100;
            return;
        }
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(date_debut, date_fin);
        if (totalDays == 0) {
            this.niveau_avancement = 100; // Start = End
            return;
        }
        
        long daysPassed = java.time.temporal.ChronoUnit.DAYS.between(date_debut, now);
        this.niveau_avancement = (int) ((daysPassed * 100) / totalDays);
    }
}
