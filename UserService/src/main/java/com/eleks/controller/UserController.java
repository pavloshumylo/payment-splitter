package com.eleks.controller;

import com.eleks.dto.UserRequestDto;
import com.eleks.dto.UserResponseDto;
import com.eleks.dto.UsersBulkRequestDto;
import com.eleks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    ResponseEntity<Void> createUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        userService.createUser(userRequestDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "{id}")
    UserResponseDto retrieveUser(@PathVariable Long id) {
        return userService.retrieveUser(id);
    }

    @PutMapping(value = "{userId}")
    ResponseEntity<Void> updateUser(@PathVariable Long userId, @RequestBody @Valid UserRequestDto userRequestDto) {
        userService.updateUser(userId, userRequestDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/search")
    List<UserResponseDto> bulkSearch(@RequestBody @Valid UsersBulkRequestDto usersBulkRequestDto) {
        return userService.bulkSearch(usersBulkRequestDto);
    }
}
