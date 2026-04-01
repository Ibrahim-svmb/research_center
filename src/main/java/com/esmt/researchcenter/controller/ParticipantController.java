package com.esmt.researchcenter.controller;

import com.esmt.researchcenter.model.Participant;
import com.esmt.researchcenter.model.User;
import com.esmt.researchcenter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participants")
@Tag(name = "Participants", description = "Participant management APIs")
public class ParticipantController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get my profile", description = "Retrieve the profile of the currently authenticated participant")
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user instanceof Participant) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(403).body("User is not a participant");
        }
    }

    @Operation(summary = "Update my profile", description = "Update the profile of the currently authenticated participant")
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(Authentication authentication, @RequestBody Participant participantDetails) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!(user instanceof Participant)) {
             return ResponseEntity.status(403).body("User is not a participant");
        }

        Participant participant = (Participant) user;
        
        participant.setFirstName(participantDetails.getFirstName());
        participant.setLastName(participantDetails.getLastName());

        // Update Research Domains
        if (participantDetails.getResearchDomains() != null) {
            participant.setResearchDomains(participantDetails.getResearchDomains());
        }

        return ResponseEntity.ok(userService.saveParticipant(participant));
    }
}
