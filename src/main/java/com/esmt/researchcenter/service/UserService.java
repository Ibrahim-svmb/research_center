package com.esmt.researchcenter.service;

import com.esmt.researchcenter.model.Participant;
import com.esmt.researchcenter.model.User;
import com.esmt.researchcenter.repository.ParticipantRepository;
import com.esmt.researchcenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    public void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
       userRepository.deleteById(id);
    }
    
    // Create user with explicit role and password
    public User createUser(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        return userRepository.save(user);
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Participant getParticipantById(Long id) {
        return participantRepository.findById(id).orElse(null);
    }
    
    public Participant saveParticipant(Participant participant) {
        return participantRepository.save(participant);
    }

    public Participant registerParticipant(Participant participant, String rawPassword) {
        participant.setPassword(passwordEncoder.encode(rawPassword));
        return participantRepository.save(participant);
    }
    
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);
        return stats;
    }
}
