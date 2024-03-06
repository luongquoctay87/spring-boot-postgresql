package com.sample.dto.request;

import com.sample.util.Gender;
import com.sample.util.UserStatus;
import com.sample.util.UserType;
import com.sample.validator.EnumNamePattern;
import com.sample.validator.UserTypeSubset;
import com.sample.validator.ValueOfEnum;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import static com.sample.util.UserType.*;

@Setter
@Getter
public class UserUpdateRequest implements Serializable {
    @Min(value = 1, message = "Id must be greater than or equal to 1")
    private Integer id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    @EnumNamePattern(name = "status", regexp = "active|inactive|none")
    private UserStatus status;
    @UserTypeSubset(anyOf = {sysadmin, admin, manager, user})
    private UserType userType;
    @ValueOfEnum(name="gender", regexp = "(male|female|other)", enumClass = Gender.class)
    private String gender;
}
