package com.esmt.researchcenter.controller;

import com.esmt.researchcenter.model.Project;
import com.esmt.researchcenter.model.ProjectStatus;
import com.esmt.researchcenter.service.ProjectImportService;
import com.esmt.researchcenter.service.ProjectService;
import com.esmt.researchcenter.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private UserService userService;
    
    @MockitoBean
    private ProjectImportService projectImportService;

    private ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setProject_id(1L);
        project.setTitre_projet("Test Project");
        project.setStatus(ProjectStatus.EN_COURS);
    }

    @Test
    @WithMockUser
    void getAllProjects_ShouldReturnListOfProjects() throws Exception {
        when(projectService.getAllProjects()).thenReturn(Arrays.asList(project));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titre_projet").value("Test Project"));
    }

    @Test
    @WithMockUser
    void getProjectById_ShouldReturnProject() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(project);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre_projet").value("Test Project"));
    }

    @Test
    @WithMockUser(username = "user")
    void createProject_ShouldReturnCreatedProject() throws Exception {
        when(projectService.saveProject(any(Project.class))).thenReturn(project);

        mockMvc.perform(post("/api/projects")
                        .with(csrf()) // CSRF token is required for POST
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre_projet").value("Test Project"));
    }

    @Test
    @WithMockUser
    void deleteProject_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/projects/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
