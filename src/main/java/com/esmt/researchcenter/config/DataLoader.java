package com.esmt.researchcenter.config;


import com.esmt.researchcenter.model.*;
import com.esmt.researchcenter.repository.ResearchDomainRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(ResearchDomainRepository repository, com.esmt.researchcenter.service.UserService userService) {
        return args -> {
            // Default Research Domains
            if (repository.count() == 0) {
                Arrays.asList(
                    new ResearchDomain("Santé", "Recherche médicale et bien-être"),
                    new ResearchDomain("IT", "Technologies de l'information et IA"),
                    new ResearchDomain("Finance", "Marchés financiers et économie"),
                    new ResearchDomain("Sports", "Performance sportive et analyse"),
                    new ResearchDomain("Environnement", "Développement durable et écologie")
                ).forEach(repository::save);
                System.out.println("Initialized Default Research Domains");
            }

            if (userService.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setFirstName("Admin");
                admin.setLastName("System");
                admin.setRole(TypeUser.ADMINISTRATEUR);
                admin.setProvider("local");
                userService.createUser(admin, "admin123");
                System.out.println("Initialized Default Admin (admin/admin123)");
            } else {
                User admin = userService.findByUsername("admin").get();
                userService.updateUserPassword(admin, "admin123");
                admin.setRole(TypeUser.ADMINISTRATEUR);
                userService.saveUser(admin);
                System.out.println("Updated existing Admin password and role");
            }

            // Default Participant: Moussa Ndiaye (Santé)
            if (userService.findByUsername("can1").isEmpty()) {
                Participant p = new Participant();
                p.setUsername("can1");
                p.setFirstName("Moussa");
                p.setLastName("Ndiaye");
                p.setRole(TypeUser.PARTICIPANT);
                p.setProvider("local");
                
                repository.findAll().stream()
                    .filter(d -> "Santé".equalsIgnoreCase(d.getName()))
                    .findFirst()
                    .ifPresent(d -> p.getResearchDomains().add(d));

                userService.registerParticipant(p, "passer");
                System.out.println("Initialized Default Participant (can1/passer)");
            }

            if (userService.findByUsername("can2").isEmpty()) {
                Participant p = new Participant();
                p.setUsername("can2");
                p.setFirstName("Amath");
                p.setLastName("Ndiaye");
                p.setRole(TypeUser.PARTICIPANT);
                p.setProvider("local");
                
                repository.findAll().stream()
                    .filter(d -> "IT".equalsIgnoreCase(d.getName()))
                    .findFirst()
                    .ifPresent(d -> p.getResearchDomains().add(d));

                userService.registerParticipant(p, "passer");
                System.out.println("Initialized Default Participant (can2/passer)");
            }

            if (userService.findByUsername("can3").isEmpty()) {
                Participant p = new Participant();
                p.setUsername("can3");
                p.setFirstName("Fatou");
                p.setLastName("Diop");
                p.setRole(TypeUser.PARTICIPANT);
                p.setProvider("local");
                
                repository.findAll().stream()
                    .filter(d -> "Environnement".equalsIgnoreCase(d.getName()))
                    .findFirst()
                    .ifPresent(d -> p.getResearchDomains().add(d));

                userService.registerParticipant(p, "passer");
                System.out.println("Initialized Default Participant (can3/passer)");
            }

            if (userService.findByUsername("can4").isEmpty()) {
                Participant p = new Participant();
                p.setUsername("can4");
                p.setFirstName("Mawa");
                p.setLastName("Kassé");
                p.setRole(TypeUser.PARTICIPANT);
                p.setProvider("local");
                
                repository.findAll().stream()
                    .filter(d -> "Santé".equalsIgnoreCase(d.getName()))
                    .findFirst()
                    .ifPresent(d -> p.getResearchDomains().add(d));

                userService.registerParticipant(p, "passer");
                System.out.println("Initialized Default Participant (can4/passer)");
            }

            if (userService.findByUsername("can5").isEmpty()) {
                Participant p = new Participant();
                p.setUsername("can5");
                p.setFirstName("Sadio");
                p.setLastName("Mané");
                p.setRole(TypeUser.PARTICIPANT);
                p.setProvider("local");
                
                repository.findAll().stream()
                    .filter(d -> "Sports".equalsIgnoreCase(d.getName()))
                    .findFirst()
                    .ifPresent(d -> p.getResearchDomains().add(d));

                userService.registerParticipant(p, "passer");
                System.out.println("Initialized Default Participant (can5/passer)");
            }

            // Default Manager: Ibrahima Samb
            if (userService.findByUsername("man1").isEmpty()) {
                Gestionnaire g = new Gestionnaire();
                g.setUsername("man1");
                g.setFirstName("Ibrahima");
                g.setLastName("Samb");
                g.setRole(TypeUser.GESTIONNAIRE);
                g.setProvider("local");
                userService.createUser(g, "passer");
                System.out.println("Initialized Default Manager (man1/passer)");
            }
        };
    }
}
