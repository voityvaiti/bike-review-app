package com.myproject.bikereviewapp.entity.dto;

import com.myproject.bikereviewapp.validation.annotation.UserPublicName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PublicNameUpdateDto {

    @UserPublicName
    private String publicName;

}
