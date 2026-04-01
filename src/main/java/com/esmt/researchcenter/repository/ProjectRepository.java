package com.esmt.researchcenter.repository;

import com.esmt.researchcenter.model.Participant;
import com.esmt.researchcenter.model.Project;
import com.esmt.researchcenter.model.ProjectStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Explicit sorting via JPQL to avoid PropertyReferenceException with underscore fields
    @Query("SELECT p FROM Project p ORDER BY p.project_id ASC")
    List<Project> findAllSorted();

    @Query("SELECT p FROM Project p WHERE p.responsable = :responsable ORDER BY p.project_id ASC")
    List<Project> findByResponsable(Participant responsable);

    @Query("SELECT p FROM Project p WHERE :participant MEMBER OF p.participants ORDER BY p.project_id ASC")
    List<Project> findByParticipantsContains(Participant participant);

    @Query("SELECT p FROM Project p WHERE p.responsable = :participant OR :participant MEMBER OF p.participants ORDER BY p.project_id ASC")
    List<Project> findByParticipant(Participant participant);

    @Query("SELECT p.researchDomain.name, COUNT(p) FROM Project p GROUP BY p.researchDomain.name")
    List<Object[]> countProjectsByDomain();

    @Query("SELECT p.status, COUNT(p) FROM Project p GROUP BY p.status")
    List<Object[]> countProjectsByStatus();

    @Query("SELECT p.researchDomain.name, SUM(p.budget_estime) FROM Project p GROUP BY p.researchDomain.name")
    List<Object[]> sumBudgetByDomain();

    @Query("SELECT AVG(p.niveau_avancement) FROM Project p")
    Double getAverageProgress();
    
    // Stats: Projects Evolution (count per year)
    @Query("SELECT EXTRACT(YEAR FROM p.date_debut), COUNT(p) FROM Project p GROUP BY EXTRACT(YEAR FROM p.date_debut) ORDER BY EXTRACT(YEAR FROM p.date_debut)")
    List<Object[]> countProjectsByYear();

    // Stats: Projects per participant (Coordinator + Contributor)
    @Query("SELECT u.firstName, u.lastName, COUNT(DISTINCT p) " +
           "FROM Participant u " +
           "LEFT JOIN u.managedProjects mp " +
           "LEFT JOIN u.contributedProjects cp " +
           "JOIN Project p ON p = mp OR p = cp " +
           "GROUP BY u.id, u.firstName, u.lastName")
    List<Object[]> countProjectsPerParticipant();
    @Query("SELECT p FROM Project p WHERE p.date_fin < :now AND p.status NOT IN (com.esmt.researchcenter.model.ProjectStatus.TERMINE, com.esmt.researchcenter.model.ProjectStatus.SUSPENDU)")
    List<Project> findProjectsToClose(LocalDate now);
}
