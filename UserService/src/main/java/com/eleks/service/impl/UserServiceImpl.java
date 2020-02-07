package com.eleks.service.impl;

import com.eleks.dto.UserRequestDto;
import com.eleks.dto.UserResponseDto;
import com.eleks.dto.UsersBulkRequestDto;
import com.eleks.entity.UserEntity;
import com.eleks.exception.InvalidRequestException;
import com.eleks.exception.ResourceNotFoundException;
import com.eleks.mapper.UserMapper;
import com.eleks.repository.UserRepository;
import com.eleks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDto retrieveUser(Long userId) {
        return userMapper.convertToDto(Collections.singletonList(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User entity with id " + userId + " doesn't exist")))).get(0);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new ResourceNotFoundException("User entity with id " + userId + " doesn't exist");
        }
    }

    @Transactional
    public void createUser(UserRequestDto userRequestDto) {
        if (userRepository.findByUserName(userRequestDto.getUserName()).isPresent()) {
            throw new InvalidRequestException("User name should be unique");
        }

        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new InvalidRequestException("User email should be unique");
        }

        userRepository.save(userMapper.convertToEntity(Collections.singletonList(userRequestDto)));
    }

    @Transactional
    public void updateUser(Long userId, UserRequestDto userRequestDto) {
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User entity with id " + userId + " doesn't exist"));

        userRepository.save(userMapper.convertToEntity(userId, Collections.singletonList(userRequestDto)));
    }

    @Override
    public List<UserResponseDto> bulkSearch(UsersBulkRequestDto bulkRequestDto) {

        List<UserEntity> userEntities = bulkRequestDto.getUserIds().stream()
                .map(userId -> userRepository.findById(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return userMapper.convertToDto(userEntities);
    }
}
