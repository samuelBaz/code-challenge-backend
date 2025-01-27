package com.example.backend.database.dtos.response;

import com.example.backend.util.Status;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResponseBase implements Serializable{
    private static final long serialVersionUID = 1L;

    private Long status;
    private String message;

    public ResponseBase(Status status) {
        this.status = status.getCode();
        this.message = status.getMessage();
    }

    public ResponseBase() {
        this.status = 0L;
        this.message = "";
    }

    public ResponseBase(Long status, String message) {
        this.status = status;
        this.message = message;
    }
}
