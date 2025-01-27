package com.example.backend.database.dtos;

import com.example.backend.database.models.Note;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NoteDto extends DtoBase<Note>{
    private String title;
    private String content;
    private UserDto user;
    private List<String> tags;
    private boolean archived = false;
    private boolean deleted = false;
    private boolean pinned = false;
}
