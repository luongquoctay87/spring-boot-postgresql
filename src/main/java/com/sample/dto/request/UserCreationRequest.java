package com.sample.dto.request;


import com.sample.util.Gender;
import com.sample.util.UserType;
import com.sample.validator.EnumNamePattern;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
public class UserCreationRequest implements Serializable {

    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @NotBlank(message = "lastName must be not blank")
    private String lastName;

    @NotBlank(message = "dateOfBirth must be not blank")
    private String dateOfBirth;

    @EnumNamePattern(name="gender", regexp = "male|female|other")
    private Gender gender;

    @NotBlank(message = "phone must be not blank")
    private String phone;

    @NotBlank(message = "email must be not blank")
    private String email;

    @NotBlank(message = "username must be not blank")
    private String username;

    @NotBlank(message = "password must be not blank")
    private String password;

    @EnumNamePattern(name="type", regexp = "(sysadmin|admin|manager|user)")
    private UserType type;

    private Set<Address> addresses;

    @Setter
    @Getter
    public static class Address {
        private String apartmentNumber;
        private String floor;
        private String building;
        private String streetNumber;
        private String street;
        private String city;
        private String country;
        private Integer addressType;
    }
}
