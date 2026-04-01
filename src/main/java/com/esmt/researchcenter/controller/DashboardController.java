package com.esmt.researchcenter.controller;

import com.esmt.researchcenter.model.Participant;
import com.esmt.researchcenter.model.Project;
import com.esmt.researchcenter.model.User;
import com.esmt.researchcenter.service.ProjectService;
import com.esmt.researchcenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @GetMapping("/participant")
    public ResponseEntity<?> getParticipantDashboard(Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!(user instanceof Participant)) {
            return ResponseEntity.status(403).body("User is not a participant");
        }

        Participant participant = (Participant) user;
        
        List<Project> assignedProjects = projectService.getProjectsContributedBy(participant).stream()
            .filter(p -> !p.getResponsable().getId().equals(participant.getId()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
            "myProjects", projectService.getProjectsManagedBy(participant),
            "assignedProjects", assignedProjects,
            "profile", participant
        ));
    }

    @GetMapping("/manager")
    public ResponseEntity<?> getManagerDashboard(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        
        return ResponseEntity.ok(Map.of(
            "projects", projectService.getAllProjects(),
            "statistics", projectService.getStatistics(),
            "participants", userService.getAllParticipants(),
            "profile", user
        ));
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAdminDashboard(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        Map<String, Object> projectStats = projectService.getStatistics();
        Map<String, Object> userStats = userService.getUserStatistics();
        
        Map<String, Object> combinedStats = new java.util.HashMap<>();
        combinedStats.putAll(projectStats);
        combinedStats.putAll(userStats);
        
        return ResponseEntity.ok(Map.of(
            "projects", projectService.getAllProjects(),
            "statistics", combinedStats,
            "users", userService.getAllUsers(),
            "profile", user
        ));
    }
}
