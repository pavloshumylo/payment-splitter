package com.eleks.service;

import com.eleks.dto.GroupRequestDto;
import com.eleks.dto.GroupResponseDto;
import com.eleks.dto.UserStatusResponse;

import java.util.List;

public interface GroupService {

    void createGroup(GroupRequestDto groupRequestDto);

    GroupResponseDto retrieveGroup(Long groupId);

    void deleteGroup(Long groupId);

    void updateGroup(Long groupId, GroupRequestDto groupRequestDto);

    List<UserStatusResponse> retrieveGroupMemberOwings(Long groupId, Long userId);
}
