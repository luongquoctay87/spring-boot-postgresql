package com.sample.controller;


import com.sample.dto.request.UserCreationRequest;
import com.sample.dto.request.UserUpdateRequest;
import com.sample.dto.response.UserDetailResponse;
import com.sample.dto.response.UserListResponse;
import com.sample.exception.InvalidDataException;
import com.sample.service.UserService;
import com.sample.util.UserStatus;
import com.sample.validator.ValueOfEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "User Controller")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Add new user", description = "Return user ID")
    @PostMapping(path = "/add", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public long createUser(@Valid @RequestBody UserCreationRequest request) {
        return userService.addUser(request);
    }

    @Operation(summary = "Update user", description = "Return message")
    @PutMapping(path = "/upd", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void updateUser(@Valid @RequestBody UserUpdateRequest request) {
        try {
            userService.updateUser(request);
        } catch (Exception e) {
            throw new InvalidDataException("Update user unsuccessful, Please try again");
        }
    }

    @Operation(summary = "Change user status", description = "Return message")
    @PatchMapping(path = "/user/{id}/change-status", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void changeStatus(@PathVariable long id,
                             @RequestParam @ValueOfEnum(message = "status must be any of enum (ACTIVE,INACTIVE,NONE)", enumClass = UserStatus.class) String status) {
        try {
            userService.changeStatus(id, status);
        } catch (Exception e) {
            throw new InvalidDataException("Change status unsuccessful, Please try again");
        }
    }

    @Operation(summary = "Delete user", description = "Return no content")
    @DeleteMapping(path = "/del/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@PathVariable("id") @Min(1) long id) {
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            throw new InvalidDataException("Delete user unsuccessful, Please try again");
        }
    }

    @Operation(summary = "Get user detail", description = "Return user detail")
    @GetMapping(path = "/user/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserDetailResponse getUser(@PathVariable("id") @Min(1) int id) {
        return userService.getUser(id);
    }

    @Operation(summary = "Get user list has been paged", description = "Return list of users")
    @GetMapping(path = "/list", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public List<UserDetailResponse> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }

    @Operation(summary = "Get user list has been sorted and paged", description = "Return list of users")
    @GetMapping(path = "/list-sorted-paged", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserListResponse getUsers(@RequestParam(defaultValue = "0") int pageNo,
                                     @RequestParam(defaultValue = "20") int pageSize,
                                     @RequestParam(required = false) String... sort) {
        return userService.getUsers(pageNo, pageSize, sort);
    }

    @Operation(summary = "Search user with criteria", description = "Return list of users")
    @GetMapping(path = "/search-with-criteria", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserListResponse searchWithCriteria(Pageable pageable, @RequestParam String... search) {
        return userService.getUsersByCriteria(pageable, search);
    }

    @Operation(summary = "Search user with specifications", description = "Return list of users")
    @GetMapping(path = "/search-with-specifications", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserListResponse searchWithSpecifications(Pageable pageable, @RequestParam String... search) {
        return userService.getUsersBySpecifications(pageable, search);
    }

    @Operation(summary = "Get user list has been sorted and paged by customize query", description = "Return list of users")
    @GetMapping(path = "/list-sorted-paged-by-customize-query", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserListResponse getUsersByCustomizeQuery(@RequestParam(required = false) String firstName,
                                                  @RequestParam(required = false) String lastName,
                                                  @RequestParam(required = false) Integer gender,
                                                  @RequestParam(defaultValue = "0") int pageNo,
                                                  @RequestParam(defaultValue = "20") int pageSize                                                  ) {
        return userService.getUsersByCustomizeQuery(firstName, lastName, gender, pageNo, pageSize);
    }
}
