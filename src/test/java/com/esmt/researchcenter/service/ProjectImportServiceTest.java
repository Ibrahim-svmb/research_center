package com.esmt.researchcenter.service;

import com.esmt.researchcenter.model.Participant;
import com.esmt.researchcenter.model.Project;
import com.esmt.researchcenter.repository.ParticipantRepository;
import com.esmt.researchcenter.repository.ProjectRepository;
import com.esmt.researchcenter.repository.ResearchDomainRepository;
import com.esmt.researchcenter.model.ResearchDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectImportServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ResearchDomainRepository researchDomainRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ProjectImportService projectImportService;

    @Test
    void importProjects_ShouldImportValidCsv() {
        String csvContent = "budget_estime,date_debut,date_fin,niveau_avancement,project_id,research_domain_id,responsable_id,decription,institution,status,titre_projet\n" +
                "10000.0,2023-01-01,2023-12-31,50,,1,1,Description Alpha,ESMT,EN_COURS,Project Alpha\n" +
                "20000.0,2024-01-01,2024-12-31,20,,1,1,Description Beta,ESMT,EN_COURS,Project Beta";

        MockMultipartFile file = new MockMultipartFile("file", "projects.csv", "text/csv", csvContent.getBytes());

        ResearchDomain domain = new ResearchDomain();
        domain.setId(1L);
        when(researchDomainRepository.findById(1L)).thenReturn(Optional.of(domain));

        Participant participant = new Participant();
        participant.setId(1L);
        when(participantRepository.findById(1L)).thenReturn(Optional.of(participant));

        Map<String, Object> result = projectImportService.importProjects(file);

        assertEquals(2, result.get("successCount"));
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) result.get("errors");
        assertEquals(0, errors.size());
        verify(projectRepository, times(1)).saveAll(any());
    }

    @Test
    void importProjects_ShouldHandleEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.csv", "text/csv", new byte[0]);

        Map<String, Object> result = projectImportService.importProjects(file);

        assertEquals(0, result.get("successCount"));
    }

    @Test
    void importProjects_ShouldSkipInvalidDates() {
        String csvContent = "budget_estime,date_debut,date_fin,niveau_avancement,project_id,research_domain_id,responsable_id,decription,institution,status,titre_projet\n" +
                "10000.0,invalid-date,2023-12-31,50,,1,1,Description,ESMT,EN_COURS,Project Invalid";

        MockMultipartFile file = new MockMultipartFile("file", "projects.csv", "text/csv", csvContent.getBytes());

        Map<String, Object> result = projectImportService.importProjects(file);

        assertEquals(0, result.get("successCount"));
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) result.get("errors");
        assertEquals(1, errors.size());
        verify(projectRepository, never()).saveAll(any());
    }
}
