package com.example.backned.repository;


import com.example.backned.model.PdfFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PdfFileRepository extends JpaRepository<PdfFile, Long> {
    List<PdfFile> findByContainerId(Long containerId);
}