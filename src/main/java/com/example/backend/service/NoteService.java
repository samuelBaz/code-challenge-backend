package com.example.backend.service;

import com.example.backend.database.dtos.NoteDto;
import com.example.backend.database.models.Note;
import com.example.backend.repositories.IGenericRepository;
import com.example.backend.repositories.INoteRepository;
import org.springframework.stereotype.Service;

@Service
public class NoteService extends GenericService<Note, NoteDto> implements INoteService {

    private final INoteRepository noteRepository;

    public NoteService(INoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    protected IGenericRepository<Note> getRepository() {
        return noteRepository;
    }


}
