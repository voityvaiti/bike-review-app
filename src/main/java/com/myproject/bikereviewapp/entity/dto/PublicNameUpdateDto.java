package com.myproject.bikereviewapp.entity.dto;

import com.myproject.bikereviewapp.validation.annotation.UserPublicName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicNameUpdateDto {

    @UserPublicName
    private String publicName;

}
