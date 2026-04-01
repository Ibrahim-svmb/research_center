package com.esmt.researchcenter.service;

import com.esmt.researchcenter.model.Participant;
import com.esmt.researchcenter.model.Project;
import com.esmt.researchcenter.model.ProjectStatus;
import com.esmt.researchcenter.model.ResearchDomain;
import com.esmt.researchcenter.repository.ParticipantRepository;
import com.esmt.researchcenter.repository.ProjectRepository;
import com.esmt.researchcenter.repository.ResearchDomainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProjectImportService {

    private final ProjectRepository projectRepository;
    private final ResearchDomainRepository researchDomainRepository;
    private final ParticipantRepository participantRepository;

    public ProjectImportService(ProjectRepository projectRepository,
                                ResearchDomainRepository researchDomainRepository,
                                ParticipantRepository participantRepository) {
        this.projectRepository = projectRepository;
        this.researchDomainRepository = researchDomainRepository;
        this.participantRepository = participantRepository;
    }

    @Transactional
    public Map<String, Object> importProjects(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<Project> projectsToSave = new ArrayList<>();
        int successCount = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            int lineNumber = 0;
            
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                if (line.trim().isEmpty()) continue;
                
                // CSV Format: budget_estime,date_debut,date_fin,niveau_avancement,project_id,
                // research_domain_id,responsable_id,decription,institution,status,titre_projet
                
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); 
                
                if (values.length < 11) {
                    errors.add("Line " + lineNumber + ": Invalid number of columns (found " + values.length + ", expected 11)");
                    continue; 
                }

                try {
                    Project project = new Project();
                    
                    // 0: budget_estime
                    project.setBudget_estime(parseFloat(values[0]));
                    
                    // 1: date_debut
                    project.setDate_debut(LocalDate.parse(clean(values[1])));
                    
                    // 2: date_fin
                    project.setDate_fin(LocalDate.parse(clean(values[2])));
                    
                    // 3: niveau_avancement
                    project.setNiveau_avancement(parseInt(values[3]));
                    
                    // 4: project_id (Ignored)
                    
                    // 5: research_domain_id
                    Long domainId = parseLong(values[5]);
                    Optional<ResearchDomain> domain = researchDomainRepository.findById(domainId);
                    if (domain.isPresent()) {
                        project.setResearchDomain(domain.get());
                    } else {
                        // Warn but proceed? Or fail? Let's warn.
                        errors.add("Line " + lineNumber + ": Domain ID " + domainId + " not found.");
                        continue;
                    }
                    
                    // 6: responsable_id
                    Long responsableId = parseLong(values[6]);
                    Optional<Participant> responsable = participantRepository.findById(responsableId);
                    if (responsable.isPresent()) {
                        Participant p = responsable.get();
                        project.setResponsable(p);
                        // Add responsable to participants list to ensure count is at least 1
                        if (project.getParticipants() == null) {
                            project.setParticipants(new ArrayList<>());
                        }
                        project.getParticipants().add(p);
                    } else {
                         errors.add("Line " + lineNumber + ": Responsable ID " + responsableId + " not found.");
                         continue;
                    }
                    
                    // 7: description
                    project.setDescription(clean(values[7]));
                    
                    // 8: institution
                    project.setInstitution(clean(values[8]));
                    
                    // 9: status
                    try {
                        project.setStatus(ProjectStatus.valueOf(clean(values[9])));
                    } catch (IllegalArgumentException e) {
                        project.setStatus(ProjectStatus.EN_COURS); // Default or error?
                        errors.add("Line " + lineNumber + ": Invalid Status '" + values[9] + "', defaulted to EN_COURS.");
                    }
                    
                    // 10: titre_projet
                    project.setTitre_projet(clean(values[10]));

                    projectsToSave.add(project);
                    successCount++;
                    
                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": Parsing error - " + e.getMessage());
                }
            }
            
            if (!projectsToSave.isEmpty()) {
                projectRepository.saveAll(projectsToSave);
            }

        } catch (IOException e) {
            errors.add("File reading error: " + e.getMessage());
        }

        result.put("successCount", successCount);
        result.put("errors", errors);
        return result;
    }
    
    private String clean(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    private Float parseFloat(String s) {
        try {
            return Float.parseFloat(clean(s));
        } catch (NumberFormatException e) {
            return 0f;
        }
    }
    
    private Integer parseInt(String s) {
        try {
            return Integer.parseInt(clean(s));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private Long parseLong(String s) {
        try {
            return Long.parseLong(clean(s));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Long value: " + s);
        }
    }
}
