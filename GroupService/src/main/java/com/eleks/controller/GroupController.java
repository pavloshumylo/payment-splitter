package com.eleks.controller;

import com.eleks.dto.GroupRequestDto;
import com.eleks.dto.GroupResponseDto;
import com.eleks.dto.UserStatusResponse;
import com.eleks.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/groups")
public class GroupController {

    private GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    ResponseEntity<Void> createGroup(@RequestBody @Valid GroupRequestDto groupRequestDto) {
        groupService.createGroup(groupRequestDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "{id}")
    GroupResponseDto retrieveGroup(@PathVariable Long id) {
        return groupService.retrieveGroup(id);
    }

    @DeleteMapping(value = "{id}")
    ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "{id}")
    ResponseEntity<Void> updateGroup(@PathVariable Long id, @RequestBody @Valid GroupRequestDto userRequestDto) {
        groupService.updateGroup(id, userRequestDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "{groupId}/users/{userId}/status")
    List<UserStatusResponse> retrieveGroupMemberOwings(@PathVariable Long groupId, @PathVariable Long userId) {
        return groupService.retrieveGroupMemberOwings(groupId, userId);
    }
}
