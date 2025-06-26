package com.vladte.devhack.controller;

import com.vladte.devhack.model.BasicEntity;
import com.vladte.devhack.service.domain.BaseService;
import com.vladte.devhack.service.view.BaseCrudViewService;
import com.vladte.devhack.service.view.BaseViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Base controller for CRUD operations.
 *
 * @param <T> the entity type
 * @param <ID> the entity ID type
 * @param <S> the service type
 */
public abstract class BaseCrudController<T extends BasicEntity, ID, S extends BaseService<T, ID>> extends BaseController {

    protected final S service;
    protected BaseCrudViewService baseCrudViewService;

    /**
     * Constructor with service and view service injection.
     *
     * @param service the service
     * @param baseViewService the base view service
     * @param baseCrudViewService the base CRUD view service
     */
    protected BaseCrudController(S service, 
                               BaseViewService baseViewService,
                               BaseCrudViewService baseCrudViewService) {
        super(baseViewService);
        this.service = service;
        this.baseCrudViewService = baseCrudViewService;
    }

    /**
     * Constructor with service injection for backward compatibility.
     *
     * @param service the service
     */
    protected BaseCrudController(S service) {
        super();
        this.service = service;
    }

    /**
     * Setter for baseCrudViewService, used for autowiring after construction.
     *
     * @param baseCrudViewService the base CRUD view service
     */
    @Autowired
    public void setBaseCrudViewService(BaseCrudViewService baseCrudViewService) {
        this.baseCrudViewService = baseCrudViewService;
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
     * List all entities.
     *
     * @param model the model
     * @return the view name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String list(Model model) {
        List<T> entities = service.findAll();
        if (baseCrudViewService != null) {
            baseCrudViewService.prepareListModel(entities, getEntityName(), getListPageTitle(), model);
        } else {
            // Fallback for backward compatibility
            model.addAttribute(getModelAttributeName(), entities);
            setPageTitle(model, getListPageTitle());
        }
        return getListViewName();
    }

    /**
     * View an entity.
     *
     * @param id the entity ID
     * @param model the model
     * @return the view name
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String view(@PathVariable ID id, Model model) {
        T entity = getEntityOrThrow(service.findById(id), getEntityName() + " not found");
        if (baseCrudViewService != null) {
            baseCrudViewService.prepareDetailModel(entity, getEntityName(), getDetailPageTitle(), model);
        } else {
            // Fallback for backward compatibility
            model.addAttribute(getModelAttributeName(false), entity);
            setPageTitle(model, getDetailPageTitle());
        }
        return getDetailViewName();
    }

    /**
     * Get the model attribute name for the entity list.
     *
     * @return the model attribute name
     */
    protected String getModelAttributeName() {
        return getModelAttributeName(true);
    }

    /**
     * Get the model attribute name for the entity or entity list.
     *
     * @param plural whether the name should be plural
     * @return the model attribute name
     */
    protected String getModelAttributeName(boolean plural) {
        String name = getEntityName().toLowerCase();
        return plural ? name + "s" : name;
    }

}
