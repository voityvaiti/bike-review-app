package com.myproject.bikereviewapp.entity.dto;

import com.myproject.bikereviewapp.validation.annotation.UserPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordUpdateDto {

    @NotBlank(message = "Old password is required field.")
    private String oldPassword;

    @UserPassword
    private String newPassword;

}
