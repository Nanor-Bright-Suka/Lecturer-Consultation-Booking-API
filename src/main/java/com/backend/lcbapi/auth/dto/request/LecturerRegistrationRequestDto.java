package com.backend.lcbapi.auth.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LecturerRegistrationRequestDto {


    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "Invalid first name")
    private String firstName;


    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Invalid last name")
    private String lastName;


    @NotBlank(message = "Staff ID is required")
    @Size(min = 3, max = 20, message = "Invalid staff ID")
    private String staffId;


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;


    @NotBlank(message = "Department is required")
    private String department;


    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Invalid password")
    private String password;











}
