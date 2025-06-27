package com.vladte.devhack.controller;

import com.vladte.devhack.dto.UserDTO;
import com.vladte.devhack.mapper.UserMapper;
import com.vladte.devhack.model.User;
import com.vladte.devhack.service.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for handling requests related to users.
 */
@Controller
@RequestMapping("/users")
public class UserController extends BaseCrudController<User, UserDTO, UUID, UserService, UserMapper> {

    private final UserMapper mapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        super(userService, userMapper);
        this.mapper = userMapper;
    }

    @Override
    protected String getListViewName() {
        return "users/list";
    }

    @Override
    protected String getDetailViewName() {
        return "users/view";
    }

    @Override
    protected String getListPageTitle() {
        return "Users";
    }

    @Override
    protected String getDetailPageTitle() {
        return "User Profile";
    }

    @Override
    protected String getEntityName() {
        return "User";
    }

    /**
     * Display the form for creating a new user.
     *
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        setPageTitle(model, "Create New User");
        return "users/form";
    }

    /**
     * Display the form for editing an existing user.
     *
     * @param id    the ID of the user to edit
     * @param model the model to add attributes to
     * @return the name of the view to render
     */
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable UUID id, Model model) {
        User user = getEntityOrThrow(service.findById(id), "User not found");
        model.addAttribute("user", user);
        setPageTitle(model, "Edit User");
        return "users/form";
    }

    /**
     * Process the form submission for creating or updating a user.
     *
     * @param user the user data from the form
     * @return a redirect to the user list
     */
    @PostMapping
    public String saveUser(@ModelAttribute User user) {
        service.save(user);
        return "redirect:/users";
    }

    /**
     * Delete a user.
     *
     * @param id the ID of the user to delete
     * @return a redirect to the user list
     */
    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable UUID id) {
        service.deleteById(id);
        return "redirect:/users";
    }
}
