package com.backend.lcbapi.auth.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "ID is required")
    @Size(max = 30, message = "ID is invalid")
    private String identifier;


    @NotBlank(message = "Password is required")
    @Size(max = 50, message = "Password is invalid")
    private String password;



}
