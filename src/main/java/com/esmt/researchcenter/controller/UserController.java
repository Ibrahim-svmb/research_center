package com.esmt.researchcenter.controller;

import com.esmt.researchcenter.model.Participant;
import com.esmt.researchcenter.model.TypeUser;
import com.esmt.researchcenter.model.User;
import com.esmt.researchcenter.repository.ResearchDomainRepository;
import com.esmt.researchcenter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ResearchDomainRepository researchDomainRepository;

    @Operation(summary = "Update password", description = "Update the password of the currently authenticated user")
    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody Map<String, String> passwordRequest) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        
        String newPassword = passwordRequest.get("password");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }
        
        userService.updateUserPassword(user, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }


    @Operation(summary = "Register user", description = "Register a new participant")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> payload) {
        String username = ((String) payload.get("username")).trim();
        
        if (userService.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        
        Participant participant = new Participant();
        participant.setUsername(username);
        participant.setFirstName(((String) payload.get("firstName")).trim());
        participant.setLastName(((String) payload.get("lastName")).trim());
        participant.setRole(TypeUser.PARTICIPANT);
        
        if (payload.containsKey("researchDomainIds")) {
            List<Integer> domainIds = (List<Integer>) payload.get("researchDomainIds");
            if (domainIds != null) {
                for (Object idObj : domainIds) {
                    Long id = Long.valueOf(idObj.toString());
                    researchDomainRepository.findById(id).ifPresent(d -> participant.getResearchDomains().add(d));
                }
            }
        }
        
        userService.registerParticipant(participant, ((String) payload.get("password")).trim());
        
        return ResponseEntity.ok("User registered successfully");
    }
}
