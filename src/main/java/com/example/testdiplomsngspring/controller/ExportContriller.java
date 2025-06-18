//package com.example.testdiplomsngspring.controller;
//
//import com.example.testdiplomsngspring.service.ConnectionManager;
//import com.example.testdiplomsngspring.service.ExcelService;
////import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//import java.io.*;
//import java.sql.Connection;
//
//@RestController
//@RequestMapping("/api")
//
//public class ExportContriller {
//
//    private final ExcelService excelService;
//    private final ConnectionManager connectionManager;
//
//    public ExportContriller(ExcelService excelService, ConnectionManager connectionManager) {
//        this.excelService = excelService;
//        this.connectionManager = connectionManager;
//    }
//
//    //@GetMapping("/export")
////    public ResponseEntity<byte[]> exportExcel() throws Exception {
////        ByteArrayInputStream stream = excelService.generateExcel();
////
////        HttpHeaders headers = new HttpHeaders();
////        headers.add("Content-Disposition", "attachment; filename=employees.xlsx");
////
////        return ResponseEntity.ok()
////                .headers(headers)
////                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
////                .body(stream.readAllBytes());
////    }
////    public ResponseEntity<?> exportExcel(HttpSession session) {
////        String sessionId = session.getId();
////        System.out.printf("sessionId: %s%n", sessionId);
////        System.out.printf("%b%n", connectionManager.isConnected(sessionId));
////        if (!connectionManager.isConnected(sessionId)) {
////            return ResponseEntity.badRequest().body("Пользователь не подключён к базе. Сначала вызовите /connect.");
////        }
////
////        try {
////            Connection conn = connectionManager.getConnection(sessionId);
////            ByteArrayInputStream stream = excelService.generateExcel(conn);
////            byte[] bytes = stream.readAllBytes();
////            HttpHeaders headers = new HttpHeaders();
////            headers.add("Content-Disposition", "attachment; filename=export.xlsx");
////            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
////            return ResponseEntity
////                    .ok()
////                    .headers(headers)
////                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
////                    .body(bytes);
////
////        } catch (Exception e) {
////            return ResponseEntity.status(500).body("Ошибка при экспорте: " + e.getMessage());
////        }
////    }
//}
