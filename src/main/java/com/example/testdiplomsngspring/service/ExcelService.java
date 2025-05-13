package com.example.testdiplomsngspring.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ExcelService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ByteArrayInputStream generateExcel() throws Exception {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM employees");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");
        Row header = sheet.createRow(0);

        if (!rows.isEmpty()) {
            int cellIdx = 0;
            for (String key : rows.get(0).keySet()) {
                Cell cell = header.createCell(cellIdx++);
                cell.setCellValue(key);
            }

            int rowIdx = 1;
            for (Map<String, Object> row : rows) {
                Row dataRow = sheet.createRow(rowIdx++);
                int colIdx = 0;
                for (Object value : row.values()) {
                    dataRow.createCell(colIdx++).setCellValue(value.toString());
                }
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
