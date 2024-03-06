package com.sample.service;

import com.sample.dto.request.UserCreationRequest;
import com.sample.dto.request.UserUpdateRequest;
import com.sample.dto.response.UserDetailResponse;
import com.sample.dto.response.UserListResponse;
import com.sample.exception.ResourceNotFoundException;
import com.sample.model.AddressEntity;
import com.sample.model.UserEntity;
import com.sample.repository.SearchRepository;
import com.sample.repository.UserRepository;
import com.sample.repository.criteria.SearchCriteria;
import com.sample.repository.specification.UserSpecificationsBuilder;
import com.sample.util.UserStatus;
import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sample.util.Constant.Regex.SEARCH_OPERATOR;
import static com.sample.util.Constant.Regex.SORT_OPERATOR;
import static com.sample.util.SearchOperation.SIMPLE_OPERATION_SET;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SearchRepository searchRepository;

    /**
     * Create new user
     *
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public long addUser(UserCreationRequest req) {
        log.info("Saving user ...");

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(req.getFirstName());
        userEntity.setLastName(req.getLastName());
        userEntity.setDateOfBirth(req.getDateOfBirth());
        userEntity.setGender(req.getGender());
        userEntity.setPhone(req.getPhone());
        userEntity.setEmail(req.getEmail());
        userEntity.setUsername(req.getUsername());
        userEntity.setPassword(req.getPassword());
        userEntity.setType(req.getType());
        userEntity.setStatus(UserStatus.none);
        req.getAddresses().forEach(a ->
                userEntity.saveAddress(AddressEntity.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())

        );

        UserEntity result = userRepository.save(userEntity);

        log.info("User added successfully");

        return result.getId();
    }

    /**
     * Update a userEntity
     *
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateRequest req) {
        log.info("Processing update user ...");

    }

    /**
     * Change user active or inactive
     *
     * @param id
     * @param status
     */
    public void changeStatus(long id, String status) {
        log.info("Changing status user, status={}", status);

    }

    /**
     * Delete user permanently
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(long id) {
        log.info("Processing delete user ...");
        userRepository.deleteById(id);
    }

    public UserDetailResponse getUser(int userId) {
        log.info("Processing get user ...");

        UserEntity user = get(userId);

        return UserDetailResponse.builder()
                .id(userId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    /**
     * Get user list has been paged
     *
     * @param pageable include pageNo, pageSize, sort
     * @return list of users
     */
    public List<UserDetailResponse> getUsers(Pageable pageable) {
        log.info("Processing get user list with pageable");
        Page<UserEntity> users = userRepository.findAll(pageable);

        return users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()
        ).toList();
    }

    /**
     * Get user list has been sorted and paged
     *
     * @param pageNo   page number
     * @param pageSize size of page
     * @param sort     sort by fields
     * @return list of users
     */
    public UserListResponse getUsers(int pageNo, int pageSize, String... sort) {
        log.info("Getting user list with pageable and sorting");

        int currentPage = pageNo;
        if (pageNo > 0) currentPage = pageNo - 1;

        List<Sort.Order> sorts = new ArrayList<>();
        if (sort.length > 0) {
            Pattern pattern = Pattern.compile(SORT_OPERATOR);
            for (String s : sort) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc"))
                        sorts.add(new Sort.Order(ASC, matcher.group(1)));
                    else
                        sorts.add(new Sort.Order(DESC, matcher.group(1)));
                }
            }
        }

        Page<UserEntity> users = userRepository.findAll(PageRequest.of(currentPage, pageSize, Sort.by(sorts)));

        return toUserList(users);
    }

    /**
     * Get user list has been page, sorted and filtered.
     *
     * @param pageable includes page, size and sort
     * @param search   array of filters
     * @return list of users
     */
    public UserListResponse getUsersByCriteria(Pageable pageable, String... search) {
        List<SearchCriteria> params = new ArrayList<>();

        if (search.length > 0) {
            Pattern pattern = Pattern.compile(SEARCH_OPERATOR);
            for (String s : search) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    params.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        Page<UserEntity> users = searchRepository.findAllUsersByCriteria(pageable, params);

        return toUserList(users);
    }

    /**
     * Get user list by specifications
     *
     * @param pageable includes page, size and sort
     * @param search   array of filters
     * @return list of users
     */
    public UserListResponse getUsersBySpecifications(Pageable pageable, String... search) {
        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        String operations = Joiner.on("|").join(SIMPLE_OPERATION_SET);

        if (search.length > 0) {
            Pattern pattern = Pattern.compile("(\\w+?)(" + operations + ")(\\p{Punct}?)(.*)(\\p{Punct}?)");
            for (String s : search) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
                }
            }
        }

        Page<UserEntity> users = userRepository.findAll(Objects.requireNonNull(builder.build()), pageable);

        return toUserList(users);
    }

    /**
     * Get list of users by
     * @param firstName
     * @param lastName
     * @param gender
     * @param pageNo
     * @param pageSize
     * @return
     */
    public UserListResponse getUsersByCustomizeQuery(String firstName, String lastName, Integer gender, int pageNo, int pageSize) {
        Page<UserDetailResponse> users = searchRepository.findAllUsersByCustomizeQuery(firstName, lastName, gender, pageNo, pageSize);
        return UserListResponse.builder()
                .users(users.stream().toList())
                .pageNo(users.getNumber())
                .pageSize(users.getSize())
                .totalPage(users.getTotalPages())
                .build();
    }

    /**
     * Get user by ID
     *
     * @param id
     * @return
     */
    private UserEntity get(long id) {
        log.info("Retrieving user from database");
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found user"));
    }

    /**
     * Covert to UserListResponse
     *
     * @param users
     * @return
     */
    private UserListResponse toUserList(Page<UserEntity> users) {
        List<UserDetailResponse> list = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()
        ).toList();

        return UserListResponse.builder()
                .users(list)
                .pageNo(users.getNumber())
                .pageSize(users.getSize())
                .totalPage(users.getTotalPages())
                .build();
    }
}
