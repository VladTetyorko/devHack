package com.vladte.devhack.service.view.impl;

import com.vladte.devhack.model.BasicEntity;
import com.vladte.devhack.service.view.BaseCrudViewService;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

/**
 * Implementation of the BaseCrudViewService interface.
 * This class handles CRUD view-related operations.
 */
@Service
public class BaseCrudViewServiceImpl extends BaseViewServiceImpl implements BaseCrudViewService {

    @Override
    public <T extends BasicEntity> void prepareListModel(List<T> entities, String entityName, String pageTitle, Model model) {
        model.addAttribute(getModelAttributeName(entityName), entities);
        setPageTitle(model, pageTitle);
    }

    @Override
    public <T extends BasicEntity> void prepareDetailModel(T entity, String entityName, String pageTitle, Model model) {
        model.addAttribute(getModelAttributeName(entityName, false), entity);
        setPageTitle(model, pageTitle);
    }
}