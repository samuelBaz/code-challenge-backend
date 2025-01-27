package com.example.backend.database.dtos.response;

import com.example.backend.database.dtos.DtoBase;
import com.example.backend.util.Status;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class ResponseEntitiesPage<T extends DtoBase> extends ResponseBase{
    private Page<T> data;
    public ResponseEntitiesPage(Status status){
        super(status);
    }
}