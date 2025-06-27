package com.vladte.devhack.mapper;

import com.vladte.devhack.dto.UserDTO;
import com.vladte.devhack.model.Answer;
import com.vladte.devhack.model.Note;
import com.vladte.devhack.model.User;
import com.vladte.devhack.model.VacancyResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for converting between User entity and UserDTO.
 */
@Component
public class UserMapper implements EntityDTOMapper<User, UserDTO> {

    @Override
    public UserDTO toDTO(User entity) {
        if (entity == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setName(entity.getName());
        dto.setRole(entity.getRole());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getAnswers() != null) {
            dto.setAnswerIds(entity.getAnswers().stream()
                    .map(Answer::getId)
                    .collect(Collectors.toList()));
        }

        if (entity.getNotes() != null) {
            dto.setNoteIds(entity.getNotes().stream()
                    .map(Note::getId)
                    .collect(Collectors.toList()));
        }

        if (entity.getVacancyResponses() != null) {
            dto.setVacancyResponseIds(entity.getVacancyResponses().stream()
                    .map(VacancyResponse::getId)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    @Override
    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User entity = new User();
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        entity.setName(dto.getName());
        entity.setRole(dto.getRole());

        // Note: Password is not set from DTO for security reasons
        // Note: Answers, notes, and vacancy responses need to be set by the service layer
        // as they require fetching the related entities from the database

        return entity;
    }

    @Override
    public User updateEntityFromDTO(User entity, UserDTO dto) {
        if (entity == null || dto == null) {
            return entity;
        }

        entity.setEmail(dto.getEmail());
        entity.setName(dto.getName());
        entity.setRole(dto.getRole());

        // Note: Password is not updated from DTO for security reasons
        // Note: Answers, notes, and vacancy responses need to be updated by the service layer
        // as they require fetching the related entities from the database

        return entity;
    }
}