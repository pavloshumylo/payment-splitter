package com.eleks.repository;

import com.eleks.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    Optional<GroupEntity> findById(Long groupId);

    Optional<GroupEntity> findByGroupName(String groupName);
}
