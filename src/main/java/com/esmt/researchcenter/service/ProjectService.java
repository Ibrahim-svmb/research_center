package com.esmt.researchcenter.service;

import com.esmt.researchcenter.model.Participant;
import com.esmt.researchcenter.model.Project;
import com.esmt.researchcenter.repository.ParticipantRepository;
import com.esmt.researchcenter.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esmt.researchcenter.model.ProjectStatus;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Sort;

@Service
@EnableScheduling
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAllSorted();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public Project saveProject(Project project) {
        if (project.getDate_debut() != null && project.getDate_fin() != null) {
            if (project.getDate_fin().isBefore(project.getDate_debut())) {
                throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début.");
            }
        }
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public List<Project> getProjectsForParticipant(Participant participant) {
        return projectRepository.findByParticipant(participant);
    }

    public List<Project> getProjectsManagedBy(Participant participant) {
        return projectRepository.findByResponsable(participant);
    }

    public List<Project> getProjectsContributedBy(Participant participant) {
        return projectRepository.findByParticipantsContains(participant);
    }

    public void addParticipantToProject(Long projectId, Long participantId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        Participant participant = new Participant();
    }

    public void assignParticipant(Long projectId, Long participantId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        Participant participant = participantRepository.findById(participantId).orElseThrow(() -> new RuntimeException("Participant not found"));

        if (!project.getParticipants().contains(participant)) {
            project.getParticipants().add(participant);
            projectRepository.save(project);
        }
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalProjects", projectRepository.count());
        stats.put("projectsByDomain", projectRepository.countProjectsByDomain());
        stats.put("projectsByStatus", projectRepository.countProjectsByStatus());
        stats.put("budgetByDomain", projectRepository.sumBudgetByDomain());

        Double avgProgress = projectRepository.getAverageProgress();
        stats.put("averageProgress", avgProgress != null ? avgProgress : 0.0);

        stats.put("projectsEvolution", projectRepository.countProjectsByYear());
        stats.put("participantLoad", projectRepository.countProjectsPerParticipant());

        return stats;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateProjectStatuses() {
        LocalDate now = LocalDate.now();
        List<Project> projects = projectRepository.findProjectsToClose(now);
        for (Project p : projects) {
            p.setStatus(ProjectStatus.TERMINE);
            projectRepository.save(p);
        }
    }
}
