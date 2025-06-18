package com.example.testdiplomsngspring.controller;

import com.example.testdiplomsngspring.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ExcelExportController {

    private final ExcelExportService excelExportService;

    @Autowired
    public ExcelExportController(ExcelExportService excelExportService) {
        this.excelExportService = excelExportService;
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportToExcel(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Получаем IP клиента
            String clientIp = getClientIpAddress(request);
            System.out.println("Export request from IP: " + clientIp);

            // Генерируем Excel файл
            byte[] excelData = excelExportService.generateEmployeeExcel(clientIp);

            // Подготавливаем ответ
            ByteArrayResource resource = new ByteArrayResource(excelData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employees.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(excelData.length)
                    .body(resource);

        } catch (Exception e) {
            System.err.println("Ошибка при формировании Excel: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Ошибка при формировании Excel: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(response);
                    //.body("Ошибка при формировании таблицы: " + e.getMessage());
        }
    }

    /**
     * Получает реальный IP адрес клиента, учитывая возможные прокси
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}