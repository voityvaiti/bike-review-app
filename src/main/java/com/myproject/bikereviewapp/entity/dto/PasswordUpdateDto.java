package com.myproject.bikereviewapp.entity.dto;

import com.myproject.bikereviewapp.validation.annotation.UserPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PasswordUpdateDto {

    @NotBlank(message = "Old password is required field.")
    private String oldPassword;

    @UserPassword
    private String newPassword;

}
