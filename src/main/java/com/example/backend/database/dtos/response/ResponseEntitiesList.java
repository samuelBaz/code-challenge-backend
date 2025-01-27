package com.example.backend.database.dtos.response;

import com.example.backend.database.dtos.DtoBase;
import com.example.backend.util.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseEntitiesList<T extends DtoBase> extends ResponseBase{
    private List<T> data;
    public ResponseEntitiesList(Status status){
        super(status);
    }
}