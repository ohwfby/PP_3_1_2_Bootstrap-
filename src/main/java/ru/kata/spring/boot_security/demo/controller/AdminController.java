package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.security.UserDetailsImpl;
import ru.kata.spring.boot_security.demo.service.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.service.UserDetailsServiceImpl;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final RoleServiceImpl roleServiceImpl;

    @Autowired
    public AdminController(UserDetailsServiceImpl userDetailsServiceImpl, RoleServiceImpl roleServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.roleServiceImpl = roleServiceImpl;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String admin(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails, Authentication authentication) {
        model.addAttribute("users", userDetailsServiceImpl.allUsers());
        model.addAttribute("username", authentication.getName());
        String roles = userDetails.getUser().getRoles()
                .stream()
                .map(role -> role.getName().replace("ROLE_", ""))
                .collect(Collectors.joining(" "));
        model.addAttribute("roles", roles);
        model.addAttribute("roleList", roleServiceImpl.findAllRoles());
        model.addAttribute("user", userDetails.getUser());
        return "admin";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roleList", roleServiceImpl.findAllRoles());
            return "admin"; // Имя HTML шаблона
// Для повторного отображения списка ролей
        }
        userDetailsServiceImpl.save(user);
        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("user") User user) {
        User existingUser = userDetailsServiceImpl.findUserById(user.getId());
        userDetailsServiceImpl.update(existingUser, user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        userDetailsServiceImpl.delete(id);
        return "redirect:/admin";
    }
}
