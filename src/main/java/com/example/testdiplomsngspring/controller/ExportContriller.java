package com.example.testdiplomsngspring.controller;

import com.example.testdiplomsngspring.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.*;

@RestController
@RequestMapping("/api")

public class ExportContriller {
    @Autowired
    private ExcelService excelService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() throws Exception {
        ByteArrayInputStream stream = excelService.generateExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=employees.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(stream.readAllBytes());
    }
}
