//package com.example.testdiplomsngspring.service;
//
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.sql.Connection;
//import java.util.ArrayList;
//
//@Service
//public class ExcelService {
////    private final JdbcTemplate jdbcTemplate;
////
////    @Autowired
////    public ExcelService(JdbcTemplate jdbcTemplate) {
////        this.jdbcTemplate = jdbcTemplate;
////    }
//
//    public ByteArrayInputStream generateExcel(Connection connection) throws Exception {
//        //List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM employees");
//        List<Map<String, Object>> rows = new ArrayList<>();
//
//        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM parameters LIMIT 100");
//             ResultSet rs = stmt.executeQuery()) {
//
//            ResultSetMetaData meta = rs.getMetaData();
//            int columnCount = meta.getColumnCount();
//
//            while (rs.next()) {
//                Map<String, Object> row = new LinkedHashMap<>();
//                for (int i = 1; i <= columnCount; i++) {
//                    row.put(meta.getColumnName(i), rs.getObject(i));
//                }
//                rows.add(row);
//            }
//        }
//
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Employees");
//
//        Font headerFont = workbook.createFont();
//        headerFont.setBold(true);
//
//        CellStyle headerStyle = workbook.createCellStyle();
//        headerStyle.setFont(headerFont);
//        headerStyle.setBorderTop(BorderStyle.THIN);
//        headerStyle.setBorderBottom(BorderStyle.THIN);
//        headerStyle.setBorderLeft(BorderStyle.THIN);
//        headerStyle.setBorderRight(BorderStyle.THIN);
//
//        // Стиль для обычных ячеек с границей
//        CellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setBorderTop(BorderStyle.THIN);
//        cellStyle.setBorderBottom(BorderStyle.THIN);
//        cellStyle.setBorderLeft(BorderStyle.THIN);
//        cellStyle.setBorderRight(BorderStyle.THIN);
//
//        Row header = sheet.createRow(0);
//
//        if (!rows.isEmpty()) {
//            int cellIdx = 0;
//            for (String key : rows.get(0).keySet()) {
//                Cell cell = header.createCell(cellIdx++);
//                cell.setCellValue(key);
//                cell.setCellStyle(headerStyle);
//            }
//
//            int rowIdx = 1;
//            for (Map<String, Object> row : rows) {
//                Row dataRow = sheet.createRow(rowIdx++);
//                int colIdx = 0;
//                for (Object value : row.values()) {
//                    //dataRow.createCell(colIdx++).setCellValue(value.toString());
//                    Cell cell = dataRow.createCell(colIdx++);
//                    cell.setCellValue(value != null ? value.toString() : "");
//                    cell.setCellStyle(cellStyle);
//                }
//            }
//            // Автоподгонка ширины колонок
//            for (int i = 0; i < rows.get(0).size(); i++) {
//                sheet.autoSizeColumn(i);
//            }
//        }
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        workbook.write(out);
//        workbook.close();
//        return new ByteArrayInputStream(out.toByteArray());
//    }
//}
