package com.example.backned.repository;

import com.example.backned.model.Container;
import com.example.backned.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {
    List<Container> findByUser(User user);

    List<Container> findByUserId(Long userId);
}