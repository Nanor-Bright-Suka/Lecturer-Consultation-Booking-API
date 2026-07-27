package com.backend.lcbapi.auth.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentRegisterRequestDto {

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 50, message = "First name is invalid")
    private String firstName;


    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 50, message = "Last name  is invalid")
    private String lastName;


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;


    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password is invalid")
    private String password;


    @NotBlank(message = "Student ID is required")
    @Size(min = 5, max = 20, message = "Student ID is invalid")
    private String studentId;

}
