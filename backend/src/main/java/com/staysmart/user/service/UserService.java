package com.staysmart.user.service;

import com.staysmart.exception.BadRequestException;
import com.staysmart.exception.ResourceNotFoundException;
import com.staysmart.user.dto.ChangePasswordRequest;
import com.staysmart.user.dto.UpdateProfileRequest;
import com.staysmart.user.dto.UserResponse;
import com.staysmart.user.entity.Role;
import com.staysmart.user.entity.User;
import com.staysmart.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User getEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return UserResponse.from(getEntityById(id));
    }

    @Transactional(readOnly = true)
    public User getEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No user found with email: " + email));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> list(Role role, Pageable pageable) {
        Page<User> page = role != null ? userRepository.findByRole(role, pageable) : userRepository.findAll(pageable);
        return page.map(UserResponse::from);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getEntityById(userId);
        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl());
        }
        if (request.bio() != null) {
            user.setBio(request.bio());
        }
        User saved = userRepository.save(user);
        log.info("Updated profile for user {}", userId);
        return UserResponse.from(saved);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getEntityById(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        log.info("Password changed for user {}", userId);
    }

    /** Admin-only: enable or disable a user account. */
    @Transactional
    public UserResponse setEnabled(Long userId, boolean enabled) {
        User user = getEntityById(userId);
        user.setEnabled(enabled);
        User saved = userRepository.save(user);
        log.info("User {} {}", userId, enabled ? "enabled" : "disabled");
        return UserResponse.from(saved);
    }

    /** Admin-only: promote/demote a user's role. */
    @Transactional
    public UserResponse changeRole(Long userId, Role role) {
        User user = getEntityById(userId);
        user.setRole(role);
        User saved = userRepository.save(user);
        log.info("User {} role changed to {}", userId, role);
        return UserResponse.from(saved);
    }

    @Transactional
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw ResourceNotFoundException.of("User", userId);
        }
        userRepository.deleteById(userId);
        log.info("Deleted user {}", userId);
    }

    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }

    public long countAll() {
        return userRepository.count();
    }
}
