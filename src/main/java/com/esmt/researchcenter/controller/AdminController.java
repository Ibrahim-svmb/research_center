package com.esmt.researchcenter.controller;

import com.esmt.researchcenter.model.*;
import com.esmt.researchcenter.repository.UserRepository;
import com.esmt.researchcenter.service.ResearchDomainService;
import com.esmt.researchcenter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Administrative APIs")
public class AdminController {

    @Autowired
    private ResearchDomainService researchDomainService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // --- Domain Management ---

    @Operation(summary = "Create domain", description = "Create a new research domain")
    @PostMapping("/domains")
    public ResearchDomain createDomain(@RequestBody ResearchDomain domain) {
        return researchDomainService.saveDomain(domain);
    }

    @Operation(summary = "Delete domain", description = "Delete a research domain by ID")
    @DeleteMapping("/domains/{id}")
    public ResponseEntity<?> deleteDomain(@PathVariable Long id) {
        researchDomainService.deleteDomain(id);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "Get all domains", description = "Retrieve a list of all research domains")
    @GetMapping("/domains")
    public List<ResearchDomain> getAllDomains() {
        return researchDomainService.getAllDomains();
    }

    // --- User/Role Management ---
    
    @Operation(summary = "Create user", description = "Create a new user (Administrator, Gestionnaire). Participants cannot be created here.")
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (user.getPassword() == null) {
             return ResponseEntity.badRequest().body("Password is required for new user");
        }
        
        // Restriction: Admin cannot create Participants
        if (user.getRole() == TypeUser.PARTICIPANT) {
            return ResponseEntity.status(403).body("Administrators cannot create Participants. Participants must register via OAuth2.");
        }

        User newUser;
        if (user.getRole() == TypeUser.PARTICIPANT) {
            newUser = new Participant();
        } else if (user.getRole() == TypeUser.GESTIONNAIRE) {
            newUser = new Gestionnaire();
        } else if (user.getRole() == TypeUser.ADMINISTRATEUR) {
             newUser = new Administrateur();
        } else {
            newUser = new User();
        }
        
        // Copy fields
        newUser.setUsername(user.getUsername());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setRole(user.getRole());
        newUser.setProvider("local");
        
        try {
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body("Username already exists");
            }
            return ResponseEntity.ok(userService.createUser(newUser, user.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error creating user: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Delete user", description = "Delete a user by ID")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update user role", description = "Update the role of an existing user")
    @PutMapping("/users/{username}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable String username, @RequestBody TypeUser newRole) {
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setRole(newRole);
        userService.saveUser(user);
        return ResponseEntity.ok(user);
    }
}
