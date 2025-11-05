package com.example.backned.controller;

import com.example.backned.model.PdfFile;
import com.example.backned.service.PdfFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:3000")
public class PdfFileController {
    
    @Autowired
    private PdfFileService pdfFileService;
    
    @GetMapping("/container/{containerId}")
    public List<PdfFile> getFilesByContainer(@PathVariable Long containerId) {
        return pdfFileService.getFilesByContainerId(containerId);
    }
    
    @PostMapping("/upload/{containerId}")
    public PdfFile uploadFile(@PathVariable Long containerId, @RequestParam("file") MultipartFile file) {
        return pdfFileService.uploadFile(containerId, file);
    }
    
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        byte[] fileContent = pdfFileService.downloadFile(fileId);
        PdfFile pdfFile = pdfFileService.getFilesByContainerId(fileId).stream()
            .filter(f -> f.getId().equals(fileId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("File not found"));
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfFile.getFileName() + "\"")
            .body(fileContent);
    }
    
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        boolean deleted = pdfFileService.deleteFile(fileId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}