package com.eleks.service.impl;

import com.eleks.dto.UserDetailsCustom;
import com.eleks.entity.UserEntity;
import com.eleks.exception.ResourceNotFoundException;
import com.eleks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetailsCustom loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User entity with name: " + username + " doesn't exist"));

        return new UserDetailsCustom(userEntity.getUserName(), userEntity.getPassword(), userEntity.getId());
    }
}
