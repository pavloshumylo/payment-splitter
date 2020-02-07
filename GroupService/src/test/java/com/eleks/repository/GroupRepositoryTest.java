package com.eleks.repository;

import com.eleks.entity.GroupEntity;
import com.eleks.entity.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private TestEntityManager entityManager;

    private GroupEntity firstGroupEntityExpected, secondGroupEntityExpected;

    private UserEntity firstUserEntityExpected, secondUserEntityExpected;

    @Before
    public void init() {
        firstUserEntityExpected = UserEntity.builder()
                .userId(10L)
                .build();
        secondUserEntityExpected = UserEntity.builder()
                .userId(11L)
                .build();

        firstGroupEntityExpected = GroupEntity.builder()
                .groupName("testGroupNameFirst")
                .currency("testCurrency")
                .members(Collections.singletonList(firstUserEntityExpected))
                .build();

        secondGroupEntityExpected = GroupEntity.builder()
                .groupName("testGroupNameSecond")
                .currency("testCurrency")
                .members(Collections.singletonList(secondUserEntityExpected))
                .build();
    }

    @Test
    public void findById_groupExist_shouldReturnProperGroupById() {
        entityManager.persist(firstGroupEntityExpected);
        entityManager.persist(secondGroupEntityExpected);

        Optional<GroupEntity> groupEntityActual = groupRepository.findById(secondGroupEntityExpected.getId());

        assertTrue(groupEntityActual.isPresent());
        assertEquals(secondGroupEntityExpected, groupEntityActual.get());
    }

    @Test
    public void findById_groupDoesNotExist_shouldReturnEmptyOptional() {
        assertFalse(groupRepository.findById(1L).isPresent());
    }

    @Test
    public void findByGroupName_groupExist_shouldReturnProperGroupByName() {
        entityManager.persist(firstGroupEntityExpected);
        entityManager.persist(secondGroupEntityExpected);

        Optional<GroupEntity> groupEntityActual = groupRepository.findByGroupName(firstGroupEntityExpected.getGroupName());

        assertTrue(groupEntityActual.isPresent());
        assertEquals(firstGroupEntityExpected, groupEntityActual.get());
    }

    @Test
    public void findByGroupName_groupDoesNotExist_shouldReturnEmptyOptional() {
        assertFalse(groupRepository.findByGroupName("testNameNotFound").isPresent());
    }

    @Test
    public void delete_groupExistsBeforeDeletion_shouldDeleteProperGroup() {
        entityManager.persist(firstGroupEntityExpected);
        entityManager.persist(secondGroupEntityExpected);

        groupRepository.delete(firstGroupEntityExpected.getId());
        groupRepository.delete(secondGroupEntityExpected.getId());

        assertTrue(groupRepository.findAll().isEmpty());
    }

    @Test
    public void getOne_groupExists_shouldReturnProperGroup() {
        entityManager.persist(firstGroupEntityExpected);
        entityManager.persist(secondGroupEntityExpected);

        assertEquals(firstGroupEntityExpected, groupRepository.getOne(firstGroupEntityExpected.getId()));
    }

    @Test
    public void save_shouldSaveAndReturnExpectedGroup() {
        GroupEntity firstGroupEntityActual = groupRepository.save(firstGroupEntityExpected);
        GroupEntity secondGroupEntityActual = groupRepository.save(secondGroupEntityExpected);

        assertEquals(firstGroupEntityExpected, firstGroupEntityActual);
        assertEquals(secondGroupEntityExpected, secondGroupEntityActual);
        assertEquals(2,  groupRepository.count());
    }
}
