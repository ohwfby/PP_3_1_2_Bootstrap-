package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.security.UserDetailsImpl;
import ru.kata.spring.boot_security.demo.service.UserDetailsServiceImpl;

import java.util.stream.Collectors;

@Controller
public class UserController {
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public UserController(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @GetMapping("/user")
    public String getCurrentUserInfo(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetailsServiceImpl.findUserById(((UserDetailsImpl) authentication.
                    getPrincipal()).getUser().getId());
            model.addAttribute("user", user);
        }
        model.addAttribute("username", userDetails.getUser().getUsername());
        String roles = userDetails.getUser().getRoles()
                .stream()
                .map(role -> role.getName().replace("ROLE_", "")) // Убираем ROLE_
                .collect(Collectors.joining(" ")); // Объединяем с пробелами
        model.addAttribute("roles", roles);

        return "user";
    }

    public String getCurrentInfo(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetailsServiceImpl.findUserById(userDetails.getUser().getId());
            model.addAttribute("user", user);
        }
        return "user";

    }
}
