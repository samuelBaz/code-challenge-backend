package com.example.backend.database.dtos.response;

import com.example.backend.database.dtos.DtoBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto extends DtoBase {
    private String email;
    private Long id;
}
