package com.eleks.repository;

import com.eleks.entity.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UserEntity firstUserEntityExpected, secondUserEntityExpected;

    @Before
    public void init() {
        firstUserEntityExpected = UserEntity.builder()
                .userName("testUserName")
                .firstName("testFirstName")
                .lastName("testLastName")
                .dateOfBirth(LocalDate.of(2010, 1, 1))
                .email("testEmail")
                .password("testPassword")
                .receiveNotifications(true)
                .build();

        secondUserEntityExpected = UserEntity.builder()
                .userName("testUserNameExt")
                .firstName("testFirstNameExt")
                .lastName("testLastNameExt")
                .dateOfBirth(LocalDate.of(2012, 1, 1))
                .email("testEmailExt")
                .password("testPasswordExt")
                .receiveNotifications(false)
                .build();
    }

    @Test
    public void findById_userExist_shouldReturnProperUserById() {
        userRepository.save(Arrays.asList(firstUserEntityExpected, secondUserEntityExpected));

        Optional<UserEntity> userEntityActual = userRepository.findById(secondUserEntityExpected.getId());

        assertTrue(userEntityActual.isPresent());
        assertEquals(secondUserEntityExpected, userEntityActual.get());
    }

    @Test
    public void findById_userDoesNotExist_shouldReturnEmptyOptional() {
        assertFalse(userRepository.findById(1L).isPresent());
    }

    @Test
    public void findByUserName_userExist_shouldReturnProperUserByName() {
        userRepository.save(Arrays.asList(firstUserEntityExpected, secondUserEntityExpected));

        Optional<UserEntity> userEntityActual = userRepository.findByUserName(secondUserEntityExpected.getUserName());

        assertTrue(userEntityActual.isPresent());
        assertEquals(secondUserEntityExpected, userEntityActual.get());
    }

    @Test
    public void findByUserName_userDoesNotExist_shouldReturnEmptyOptional() {
        assertFalse(userRepository.findByUserName("testNameNotFound").isPresent());
    }

    @Test
    public void findByEmail_userExists_shouldReturnProperUserByEmail() {
        userRepository.save(Arrays.asList(firstUserEntityExpected, secondUserEntityExpected));

        Optional<UserEntity> userEntityActual = userRepository.findByEmail(firstUserEntityExpected.getEmail());

        assertTrue(userEntityActual.isPresent());
        assertEquals(firstUserEntityExpected, userEntityActual.get());
    }

    @Test
    public void findByEmail_userDoesNotExists_shouldRetturnEmptyOptional() {
        assertFalse(userRepository.findByEmail("testEmailNotFound").isPresent());
    }

    @Test
    public void deleteById_userExistsBeforeDeletion_shouldDeleteProperUser() {
        userRepository.save(Arrays.asList(firstUserEntityExpected, secondUserEntityExpected));

        userRepository.deleteById(firstUserEntityExpected.getId());
        userRepository.deleteById(secondUserEntityExpected.getId());

        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    public void getOne_userExists_shouldReturnProperUser() {
        userRepository.save(Arrays.asList(firstUserEntityExpected, secondUserEntityExpected));

        assertEquals(firstUserEntityExpected, userRepository.getOne(firstUserEntityExpected.getId()));
    }

    @Test
    public void save_shouldSaveAndReturnExpectedUser() {
        UserEntity firstUserEntityActual = userRepository.save(firstUserEntityExpected);
        UserEntity secondUserEntityActual = userRepository.save(secondUserEntityExpected);

        assertEquals(firstUserEntityExpected, firstUserEntityActual);
        assertEquals(secondUserEntityExpected, secondUserEntityActual);
        assertEquals(2,  userRepository.count());
    }
}
