package com.myproject.bikereviewapp.entity.dto;

import com.myproject.bikereviewapp.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdateDto {

    @NotNull
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role must be selected")
    private Role role;

}
