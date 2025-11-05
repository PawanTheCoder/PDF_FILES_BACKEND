package com.example.backned.service;

import com.example.backned.model.Container;
import com.example.backned.model.User;
import com.example.backned.repository.ContainerRepository;
import com.example.backned.repository.UserRepository;
import com.example.backned.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ContainerService {

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserContext userContext;

    public List<Container> getAllContainersByUser() {
        String username = userContext.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("User not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return containerRepository.findByUser(user);
    }

    public Optional<Container> getContainerById(Long id) {
        String username = userContext.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("User not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Optional<Container> container = containerRepository.findById(id);
        if (container.isPresent() && container.get().getUser().getId().equals(user.getId())) {
            return container;
        }
        return Optional.empty();
    }

    public Container createContainer(Container container) {
        String username = userContext.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("User not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        container.setUser(user);
        return containerRepository.save(container);
    }

    public Container updateContainer(Long id, Container containerDetails) {
        Optional<Container> optionalContainer = getContainerById(id);
        if (optionalContainer.isPresent()) {
            Container container = optionalContainer.get();
            container.setName(containerDetails.getName());
            container.setDescription(containerDetails.getDescription());
            return containerRepository.save(container);
        }
        return null;
    }

    public boolean deleteContainer(Long id) {
        Optional<Container> container = getContainerById(id);
        if (container.isPresent()) {
            containerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
