package com.example.backned.controller;

import com.example.backned.model.Container;
import com.example.backned.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/containers")
@CrossOrigin(origins = "http://localhost:3000")
public class ContainerController {

    @Autowired
    private ContainerService containerService;

    @GetMapping
    public List<Container> getAllContainers() {
        return containerService.getAllContainersByUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Container> getContainerById(@PathVariable Long id) {
        Optional<Container> container = containerService.getContainerById(id);
        return container.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Container createContainer(@RequestBody Container container) {
        return containerService.createContainer(container);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Container> updateContainer(@PathVariable Long id, @RequestBody Container containerDetails) {
        Container updatedContainer = containerService.updateContainer(id, containerDetails);
        return updatedContainer != null ? ResponseEntity.ok(updatedContainer) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContainer(@PathVariable Long id) {
        boolean deleted = containerService.deleteContainer(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}