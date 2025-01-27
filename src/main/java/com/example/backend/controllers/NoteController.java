package com.example.backend.controllers;

import com.example.backend.database.dtos.NoteDto;
import com.example.backend.database.dtos.response.ResponseEntitiesPage;
import com.example.backend.database.models.ModelBase;
import com.example.backend.database.models.Note;
import com.example.backend.exceptions.InternalErrorException;
import com.example.backend.repositories.specifications.GenericSpecification;
import com.example.backend.repositories.specifications.GenericSpecificationsBuilder;
import com.example.backend.repositories.specifications.SearchCriteria;
import com.example.backend.service.IGenericService;
import com.example.backend.service.INoteService;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController extends GenericController<Note, NoteDto> {

    private final INoteService noteService;

    public NoteController(INoteService noteService) {
        this.noteService = noteService;
    }

    @Override
    protected IGenericService getService() {
        return noteService;
    }

    @GetMapping("getAll/{id}")
    protected ResponseEntitiesPage getAllVehiclesByCity(@PathVariable("id") @NotNull Long id,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(required = false) String order,
                                                        @RequestParam(required = false) String filter) {
        try {
            Sort sort = getSort(order);
            Specification<ModelBase> spec = new GenericSpecificationsBuilder<>().build(filter);
            SearchCriteria criteria2 = new SearchCriteria("deleted", "eq", false);
            SearchCriteria criteria3 = new SearchCriteria("user id", "eq", id);
            Specification<ModelBase> specification = Specification.where(new GenericSpecification<>(criteria2).or(new GenericSpecification<>(criteria3)))
                    .and(spec);
            Pageable pageable = PageRequest.of(page, size, sort);
            ResponseEntitiesPage responseEntitiesPage = noteService.findAll(pageable, specification);
            responseEntitiesPage.setMessage(getTypeName() + "s were successfully listed.");
            return responseEntitiesPage;
        } catch (Exception e) {
            String messageError = "An error occurred while found entities of " + getService().getClass().getTypeName();
            logger.error(messageError);
            throw new InternalErrorException(messageError, e);
        }
    }
}
