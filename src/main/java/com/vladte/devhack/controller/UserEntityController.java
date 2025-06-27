package com.vladte.devhack.controller;

import com.vladte.devhack.model.BasicEntity;
import com.vladte.devhack.model.User;
import com.vladte.devhack.service.domain.BaseService;
import com.vladte.devhack.service.domain.UserService;
import com.vladte.devhack.service.view.BaseViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Base controller for operations on user-owned entities.
 * This class extends BaseController and adds access control based on user roles.
 *
 * @param <E>  the entity type, must extend BasicEntity
 * @param <ID> the entity ID type
 * @param <S>  the service type
 */
public abstract class UserEntityController<E extends BasicEntity, ID, S extends BaseService<E, ID>> extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserEntityController.class);
    private static final String ROLE_MANAGER = "ROLE_MANAGER";

    protected final S service;
    protected final UserService userService;

    /**
     * Constructor with service and userService injection.
     *
     * @param service         the service
     * @param userService     the user service
     * @param baseViewService the base view service
     */
    protected UserEntityController(S service, UserService userService, BaseViewService baseViewService) {
        super(baseViewService);
        this.service = service;
        this.userService = userService;
    }

    /**
     * Constructor with service and userService injection for backward compatibility.
     *
     * @param service     the service
     * @param userService the user service
     */
    protected UserEntityController(S service, UserService userService) {
        super();
        this.service = service;
        this.userService = userService;
    }

    /**
     * Get the user associated with the entity.
     *
     * @param entity the entity
     * @return the user associated with the entity
     */
    protected abstract User getEntityUser(E entity);

    /**
     * Get the current authenticated user.
     *
     * @return the current user
     * @throws IllegalStateException if the current user is not found
     */
    protected User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        return userService.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }

    /**
     * Check if the current user is a manager.
     *
     * @return true if the current user is a manager, false otherwise
     */
    protected boolean isCurrentUserManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_MANAGER));
    }

    /**
     * Check if the current user has access to the entity.
     *
     * @param entity the entity to check
     * @return true if the current user has access to the entity, false otherwise
     */
    protected boolean hasAccessToEntity(E entity) {
        // Managers have access to all entities
        if (isCurrentUserManager()) {
            return true;
        }

        // Users have access only to their own entities
        User entityUser = getEntityUser(entity);
        User currentUser = getCurrentUser();

        return entityUser != null && entityUser.getId().equals(currentUser.getId());
    }

    /**
     * Get the view name for the list page.
     *
     * @return the view name
     */
    protected abstract String getListViewName();

    /**
     * Get the view name for the detail page.
     *
     * @return the view name
     */
    protected abstract String getDetailViewName();

    /**
     * Get the page title for the list page.
     *
     * @return the page title
     */
    protected abstract String getListPageTitle();

    /**
     * Get the page title for the detail page.
     *
     * @return the page title
     */
    protected abstract String getDetailPageTitle();

    /**
     * Get the entity name for error messages.
     *
     * @return the entity name
     */
    protected abstract String getEntityName();

    /**
     * List all entities that the current user has access to.
     *
     * @param model the model
     * @return the view name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String list(Model model) {
        logger.debug("Listing entities with access control");
        setPageTitle(model, getListPageTitle());
        return getListViewName();
    }

    /**
     * View an entity if the current user has access to it.
     *
     * @param id    the entity ID
     * @param model the model
     * @return the view name
     */
    @GetMapping("/{id}")
    public String view(@PathVariable ID id, Model model) {
        logger.debug("Viewing entity with ID: {} with access control", id);
        setPageTitle(model, getDetailPageTitle());
        return getDetailViewName();
    }
}
