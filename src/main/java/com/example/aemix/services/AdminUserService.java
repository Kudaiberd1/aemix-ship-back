package com.example.aemix.services;

import com.example.aemix.dto.requests.UserUpdateRequest;
import com.example.aemix.dto.responses.UserResponse;
import com.example.aemix.entities.User;
import com.example.aemix.entities.enums.Role;
import com.example.aemix.exceptions.ResourceNotFoundException;
import com.example.aemix.mappers.UserMapper;
import com.example.aemix.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Page<UserResponse> getUsers(
            String text,
            Role role,
            Boolean isVerified,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findUsers(text, role, isVerified, pageable)
                .map(userMapper::toDto);
    }

    public UserResponse updateUser(String emailOrTelegramId, UserUpdateRequest request) {
        User user = userRepository.findByIdentifier(emailOrTelegramId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(request.getRole());

        return userMapper.toDto(userRepository.save(user));
    }

    public void deleteUser(String emailOrTelegramId) {
        User user = userRepository.findByIdentifier(emailOrTelegramId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }
}
