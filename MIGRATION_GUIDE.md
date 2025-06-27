# Migration Guide: BaseCrudController with DTO Mapping

This guide provides instructions for migrating existing controllers to use the new BaseCrudController with DTO mapping.

## Overview of Changes

The BaseCrudController has been updated to use DTOs (Data Transfer Objects) for better separation of concerns and to follow SOLID principles. The main changes are:

1. BaseCrudController now requires a DTO type and a mapper
2. UserOwnedCrudController has been updated to work with the new BaseCrudController
3. New interfaces have been added: BaseDTO and EntityDTOMapper

## Migration Steps

### 1. Create a DTO for your entity

If you don't already have a DTO for your entity, create one that implements the BaseDTO interface:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YourEntityDTO implements BaseDTO {
    private UUID id;
    // Add other fields from your entity that should be exposed to the view
}
```

### 2. Create a mapper for your entity and DTO

Create a mapper that implements the EntityDTOMapper interface:

```java
@Component
public class YourEntityMapper implements EntityDTOMapper<YourEntity, YourEntityDTO> {
    
    @Override
    public YourEntityDTO toDTO(YourEntity entity) {
        if (entity == null) {
            return null;
        }
        
        YourEntityDTO dto = new YourEntityDTO();
        dto.setId(entity.getId());
        // Map other fields from entity to DTO
        return dto;
    }
    
    @Override
    public YourEntity toEntity(YourEntityDTO dto) {
        if (dto == null) {
            return null;
        }
        
        YourEntity entity = new YourEntity();
        entity.setId(dto.getId());
        // Map other fields from DTO to entity
        return entity;
    }
    
    @Override
    public YourEntity updateEntityFromDTO(YourEntity entity, YourEntityDTO dto) {
        if (entity == null || dto == null) {
            return entity;
        }
        
        // Update entity fields from DTO
        return entity;
    }
}
```

### 3. Update your controller

Update your controller to use the new BaseCrudController with DTO mapping:

#### For controllers extending BaseCrudController:

```java
@Controller
@RequestMapping("/your-entities")
public class YourEntityController extends BaseCrudController<YourEntity, YourEntityDTO, UUID, YourEntityService, YourEntityMapper> {
    
    @Autowired
    public YourEntityController(YourEntityService service, YourEntityMapper mapper) {
        super(service, mapper);
    }
    
    // Implement abstract methods and add your controller-specific methods
}
```

#### For controllers extending UserOwnedCrudController:

```java
@Controller
@RequestMapping("/your-user-owned-entities")
public class YourUserOwnedEntityController extends UserOwnedCrudController<YourEntity, YourEntityDTO, UUID, YourEntityService, YourEntityMapper> {
    
    @Autowired
    public YourUserOwnedEntityController(YourEntityService service, YourEntityMapper mapper, UserService userService) {
        super(service, mapper, userService);
    }
    
    @Override
    protected User getEntityUser(YourEntity entity) {
        return entity.getUser();
    }
    
    // Implement abstract methods and add your controller-specific methods
}
```

### 4. Update your templates

If your templates access entity properties directly, you may need to update them to use the DTO properties instead. In most cases, the property names should be the same, so no changes are needed.

## Benefits of Using DTOs

1. **Separation of Concerns**: DTOs separate the presentation layer from the domain model
2. **Security**: DTOs allow you to expose only the data that should be visible to the client
3. **Flexibility**: DTOs can be tailored to specific view requirements
4. **Performance**: DTOs can reduce the amount of data transferred between layers

## Example

Here's a complete example of migrating a controller:

### Before:

```java
@Controller
@RequestMapping("/notes")
public class NoteController extends UserOwnedCrudController<Note, UUID, NoteService> {
    
    @Autowired
    public NoteController(NoteService service, UserService userService) {
        super(service, userService);
    }
    
    @Override
    protected User getEntityUser(Note entity) {
        return entity.getUser();
    }
    
    // Other methods...
}
```

### After:

```java
@Controller
@RequestMapping("/notes")
public class NoteController extends UserOwnedCrudController<Note, NoteDTO, UUID, NoteService, NoteMapper> {
    
    @Autowired
    public NoteController(NoteService service, NoteMapper mapper, UserService userService) {
        super(service, mapper, userService);
    }
    
    @Override
    protected User getEntityUser(Note entity) {
        return entity.getUser();
    }
    
    // Other methods...
}
```

## Need Help?

If you have any questions or need assistance with the migration, please contact the development team.