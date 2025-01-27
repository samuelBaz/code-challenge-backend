package com.example.backend.repositories;

import com.example.backend.database.models.Note;
import org.springframework.stereotype.Repository;
@Repository
public interface INoteRepository extends IGenericRepository<Note> {
}
