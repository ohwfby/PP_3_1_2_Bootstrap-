package ru.kata.spring.boot_security.demo.repository;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
    List<Role> findAllByNameIn(Collection<String> names);

}
