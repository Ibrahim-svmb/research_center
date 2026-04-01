package com.esmt.researchcenter.service;

import com.esmt.researchcenter.model.Project;
import com.esmt.researchcenter.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project1;
    private Project project2;

    @BeforeEach
    void setUp() {
        project1 = new Project();
        project1.setProject_id(1L);
        project1.setTitre_projet("Project 1");

        project2 = new Project();
        project2.setProject_id(2L);
        project2.setTitre_projet("Project 2");
    }

    @Test
    void getAllProjects_ShouldReturnListOfProjects() {
        when(projectRepository.findAllSorted()).thenReturn(Arrays.asList(project1, project2));

        List<Project> projects = projectService.getAllProjects();

        assertNotNull(projects);
        assertEquals(2, projects.size());
        verify(projectRepository, times(1)).findAllSorted();
    }

    @Test
    void saveProject_ShouldReturnSavedProject() {
        when(projectRepository.save(any(Project.class))).thenReturn(project1);

        Project savedProject = projectService.saveProject(project1);

        assertNotNull(savedProject);
        assertEquals("Project 1", savedProject.getTitre_projet());
        verify(projectRepository, times(1)).save(project1);
    }

    @Test
    void getProjectById_ShouldReturnProjectValidId() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));

        Project foundProject = projectService.getProjectById(1L);

        assertNotNull(foundProject);
        assertEquals(1L, foundProject.getProject_id());
    }

    @Test
    void getProjectById_ShouldReturnNullInvalidId() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        Project foundProject = projectService.getProjectById(99L);

        assertNull(foundProject);
    }
}
