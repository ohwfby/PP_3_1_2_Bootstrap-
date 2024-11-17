package ru.kata.spring.boot_security.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.security.UserDetailsImpl;
import ru.kata.spring.boot_security.demo.security.UserNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService, UserDetailsServ {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    // Вспомогательный метод для поиска пользователя по ID
    private User getUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            logger.warn("Пользователь с ID {} не найден", id);
            return new UserNotFoundException("User with ID " + id + " not found");
        });
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            logger.warn("Пользователь с именем {} не найден", username);
            return new UsernameNotFoundException("User not found");
        });
        logger.info("Пользователь {} успешно загружен", username);
        return new UserDetailsImpl(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return getUserOrThrow(userId);
    }

    @Override
    @Transactional
    public List<User> allUsers() {
        List<User> users = userRepository.findAll();
        logger.info("Загружено {} пользователей", users.size());
        return users;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = getUserOrThrow(id);
        userRepository.delete(user);
        logger.info("Пользователь с ID {} удален", id);
    }

    @Override
    @Transactional
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        logger.info("Пользователь {} успешно сохранен", user.getUsername());
    }

    @Override
    @Transactional
    public void update(User existingUser, User user) {
        existingUser.setUsername(user.getUsername());
        if (!user.getPassword().equals(existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        Set<Role> roles = new HashSet<>(roleRepository.findAllByNameIn(
                user.getRoles().stream().map(Role::getName).toList()
        ));
        existingUser.setRoles(roles);
        existingUser.setYearOfBirth(user.getYearOfBirth());
        userRepository.save(existingUser);
        logger.info("Пользователь {} успешно обновлен", existingUser.getUsername());

    }
}
