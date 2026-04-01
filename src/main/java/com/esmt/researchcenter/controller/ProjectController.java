package com.esmt.researchcenter.controller;

import com.esmt.researchcenter.model.Participant;
import com.esmt.researchcenter.model.Project;
import com.esmt.researchcenter.model.ProjectStatus;
import com.esmt.researchcenter.model.User;
import com.esmt.researchcenter.service.ProjectImportService;
import com.esmt.researchcenter.service.ProjectService;
import com.esmt.researchcenter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Project management APIs")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectImportService projectImportService;

    @Operation(summary = "Get all projects", description = "Retrieve a list of all projects")
    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @Operation(summary = "Get project by ID", description = "Retrieve a project by its unique ID")
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return project != null ? ResponseEntity.ok(project) : ResponseEntity.notFound().build();
    }

    @Autowired
    private UserService userService;

    @Operation(summary = "Create project", description = "Create a new project. Updates status to EN_COURS if null. Sets responsable if user is a participant.")
    @PostMapping
    public Project createProject(@RequestBody Project project, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElse(null);
            if (user instanceof Participant) {
                Participant p = (Participant) user;
                project.setResponsable(p);
                if (project.getParticipants() == null) {
                    project.setParticipants(new ArrayList<>());
                }
                project.getParticipants().add(p);
            }
        }
        if (project.getStatus() == null) {
            project.setStatus(ProjectStatus.EN_COURS);
        }
        return projectService.saveProject(project);
    }

    @Operation(summary = "Update project", description = "Update an existing project. Recalculates progress if dates change.")
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        Project project = projectService.getProjectById(id);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        if(projectDetails.getTitre_projet() != null) project.setTitre_projet(projectDetails.getTitre_projet());
        if(projectDetails.getDescription() != null) project.setDescription(projectDetails.getDescription());
        if(projectDetails.getStatus() != null) project.setStatus(projectDetails.getStatus());
        if(projectDetails.getBudget_estime() != null) project.setBudget_estime(projectDetails.getBudget_estime());
        if(projectDetails.getResearchDomain() != null) project.setResearchDomain(projectDetails.getResearchDomain());
        
        if(projectDetails.getDate_debut() != null) project.setDate_debut(projectDetails.getDate_debut());
        if(projectDetails.getDate_fin() != null) project.setDate_fin(projectDetails.getDate_fin());

        if(projectDetails.getInstitution() != null) project.setInstitution(projectDetails.getInstitution());

        return ResponseEntity.ok(projectService.saveProject(project));
    }

    @Operation(summary = "Delete project", description = "Delete a project by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Assign participant", description = "Assign a participant to a project")
    @PostMapping("/{projectId}/participants/{participantId}")
    public ResponseEntity<?> assignParticipant(@PathVariable Long projectId, @PathVariable Long participantId) {
        try {
            projectService.assignParticipant(projectId, participantId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Import projects", description = "Import projects from a CSV file (titre, description, budget, institution, date_debut, date_fin). Restricted to ADMINISTRATEUR and GESTIONNAIRE.")
    @PostMapping("/import")
    public ResponseEntity<?> importProjects(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = projectImportService.importProjects(file);
        return ResponseEntity.ok(result);
    }
}
