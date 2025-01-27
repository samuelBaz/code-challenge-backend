package com.example.backend.database.dtos.response;

import com.example.backend.database.dtos.DtoBase;
import com.example.backend.util.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBaseEntity<T extends DtoBase> extends ResponseBase{
    private T data;

    public ResponseBaseEntity(Status status){
        super(status);
    }
}
