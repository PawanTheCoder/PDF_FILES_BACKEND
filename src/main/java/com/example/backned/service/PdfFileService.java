package com.example.backned.service;

import com.example.backned.model.Container;
import com.example.backned.model.PdfFile;
import com.example.backned.model.User;
import com.example.backned.repository.ContainerRepository;
import com.example.backned.repository.PdfFileRepository;
import com.example.backned.repository.UserRepository;
import com.example.backned.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PdfFileService {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @Autowired
    private PdfFileRepository pdfFileRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserContext userContext;

    public PdfFileService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    public List<PdfFile> getFilesByContainerId(Long containerId) {
        String username = userContext.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("User not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Optional<Container> containerOpt = containerRepository.findById(containerId);
        if (!containerOpt.isPresent() || !containerOpt.get().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Container not found or access denied");
        }
        return pdfFileRepository.findByContainerId(containerId);
    }

    public PdfFile uploadFile(Long containerId, MultipartFile file) {
        try {
            String username = userContext.getCurrentUsername();
            if (username == null) {
                throw new RuntimeException("User not authenticated");
            }
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Optional<Container> containerOpt = containerRepository.findById(containerId);
            if (!containerOpt.isPresent() || !containerOpt.get().getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Container not found or access denied");
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            PdfFile pdfFile = new PdfFile();
            pdfFile.setFileName(file.getOriginalFilename());
            pdfFile.setFilePath(targetLocation.toString());
            pdfFile.setFileSize(file.getSize());
            pdfFile.setContainer(containerOpt.get());

            return pdfFileRepository.save(pdfFile);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file", ex);
        }
    }

    public PdfFile getFileById(Long fileId) {
        String username = userContext.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("User not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Optional<PdfFile> pdfFileOpt = pdfFileRepository.findById(fileId);
        if (!pdfFileOpt.isPresent() || !pdfFileOpt.get().getContainer().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("File not found or access denied");
        }
        return pdfFileOpt.get();
    }

    public byte[] downloadFile(Long fileId) {
        String username = userContext.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("User not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Optional<PdfFile> pdfFileOpt = pdfFileRepository.findById(fileId);
        if (!pdfFileOpt.isPresent() || !pdfFileOpt.get().getContainer().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("File not found or access denied");
        }
        try {
            Path filePath = Paths.get(pdfFileOpt.get().getFilePath());
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("File not found", ex);
        }
    }

    public boolean deleteFile(Long fileId) {
        String username = userContext.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("User not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Optional<PdfFile> pdfFileOpt = pdfFileRepository.findById(fileId);
        if (!pdfFileOpt.isPresent() || !pdfFileOpt.get().getContainer().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("File not found or access denied");
        }
        try {
            Path filePath = Paths.get(pdfFileOpt.get().getFilePath());
            Files.deleteIfExists(filePath);
            pdfFileRepository.deleteById(fileId);
            return true;
        } catch (IOException ex) {
            throw new RuntimeException("Error deleting file", ex);
        }
    }
}