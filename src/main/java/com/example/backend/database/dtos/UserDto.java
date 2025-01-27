package com.example.backend.database.dtos;

import com.example.backend.database.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto extends DtoBase<User>{
    private String password;
    private String email;
}
