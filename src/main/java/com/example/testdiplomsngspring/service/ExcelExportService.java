package com.example.testdiplomsngspring.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class ExcelExportService {

    private final ConnectionManager connectionManager;

    @Autowired
    public ExcelExportService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Генерирует Excel файл с данными сотрудников для указанного IP
     *
     * @param ip IP адрес для получения подключения к БД
     * @return byte[] содержимое Excel файла
     * @throws Exception в случае ошибки генерации
     */
    public byte[] generateEmployeeExcel(String ip) throws Exception {
        // Получаем подключение к БД
        ConnectionWrapper connectionWrapper = connectionManager.getConnection(ip);
        JdbcTemplate jdbcTemplate = connectionWrapper.getJdbcTemplate();

        // Выполняем запрос к БД
        List<Map<String, Object>> employeeData = fetchEmployeeData(jdbcTemplate);

        // Создаем Excel файл
        return createExcelFile(employeeData);
    }

    /**
     * Выполняет запрос к базе данных для получения данных о сотрудниках
     *
     * @param jdbcTemplate JdbcTemplate для выполнения запроса
     * @return List<Map<String, Object>> данные сотрудников
     */
    private List<Map<String, Object>> fetchEmployeeData(JdbcTemplate jdbcTemplate) {
//        try {
//            // Пример запроса - измените под вашу структуру БД
//            String sql = """
//                SELECT
//                    id,
//                    first_name,
//                    last_name,
//                    email,
//                    phone,
//                    department,
//                    position,
//                    hire_date,
//                    salary
//                FROM employees
//                ORDER BY last_name, first_name
//                """;
//
//            System.out.println("Executing SQL query: " + sql);
//
//            return jdbcTemplate.queryForList(sql);
//
//        } catch (DataAccessException e) {
//            System.err.println("Ошибка при выполнении запроса к БД: " + e.getMessage());
//
//            // Если таблица employees не существует, попробуем альтернативный запрос
//
//        }
        try {
            String alternativeSql = "SELECT * FROM parameters LIMIT 100";
            System.out.println("Trying alternative query: " + alternativeSql);
            return jdbcTemplate.queryForList(alternativeSql);
        } catch (DataAccessException e) {
            throw new RuntimeException("Не удалось получить данные из базы данных: " + e.getMessage(), e);
        }
    }

    /**
     * Создает Excel файл на основе данных
     *
     * @param data данные для записи в Excel
     * @return byte[] содержимое Excel файла
     * @throws IOException в случае ошибки создания файла
     */
    private byte[] createExcelFile(List<Map<String, Object>> data) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Параметры");

            // Создаем стили
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle sectionStyle = createSectionStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);


            // Создаем заголовки на основе первой строки данных
            //createHeaders(sheet, data.get(0).keySet().toArray(new String[0]), headerStyle);

            // Заполняем данными
            //fillData(sheet, data, dataStyle);

            // Автоматически подстраиваем ширину колонок
            //autoSizeColumns(sheet, data.get(0).size());

            // Создаем заголовки таблицы
            createParameterHeaders(sheet, headerStyle);

            // Заполняем данными (в реальном приложении здесь будет обработка данных из БД)
            //fillParameterData(sheet, data, sectionStyle, dataStyle);

            // Настраиваем ширину колонок
            setupColumnWidths(sheet);


            workbook.write(outputStream);
            System.out.println("Excel файл успешно создан. Размер: " + outputStream.size() + " байт");

            return outputStream.toByteArray();
        }
    }

    /**
     * Создает стиль для заголовков
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    /**
     * Создает стиль для данных
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private void createParameterHeaders(Sheet sheet, CellStyle headerStyle) {
        Row headerRow1 = sheet.createRow(2);
        Row headerRow2 = sheet.createRow(3);
        Row headerRow3 = sheet.createRow(4);

        // Первая строка заголовков (объединенные ячейки)
        createMergedHeader(sheet, headerRow1, headerStyle,
                new String[]{"№ п/п", "Наименование параметра", "Источник / приемник сигнала", "", "Диапазон измерения*", "", "Ед. изм.", "Описание в БД", "", "", "Шкаф контроллера", "", "", "", ""},
                new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                new int[]{0, 0, 2, 3, 4, 5, 0, 7, 9, 10, 10, 12, 13, 14, 15});

        // Вторая строка заголовков
        createHeaderRow(headerRow2, headerStyle,
                new String[]{"", "", "Позиция", "Наименование", "", "", "", "Код типа объекта", "Название типа объекта", "Код параметра", "Название параметра", "Тип сигнала", "Шкаф", "Тип модуля", "№ модуля", "Канал"});

        // Третья строка заголовков (диапазоны измерения)
        createHeaderRow(headerRow3, headerStyle,
                new String[]{"", "", "", "", "мин.", "макс.", "", "", "", "", "", "", "", "", "", ""});

        // Нумерация столбцов
        Row columnNumbersRow = sheet.createRow(5);
        for (int i = 0; i < 16; i++) {
            Cell cell = columnNumbersRow.createCell(i);
            cell.setCellValue(String.valueOf(i + 1));
            cell.setCellStyle(headerStyle);
        }
    }

    private void createMergedHeader(Sheet sheet, Row row, CellStyle style,
                                    String[] values, int[] cols, int[] mergePairs) {
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(cols[i]);
            cell.setCellValue(values[i]);
            cell.setCellStyle(style);
        }

        for (int i = 0; i < mergePairs.length; i += 2) {
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), mergePairs[i], mergePairs[i+1]));
        }
    }

    private void createHeaderRow(Row row, CellStyle style, String[] values) {
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
            cell.setCellStyle(style);
        }
    }

    private void setupColumnWidths(Sheet sheet) {
        // Устанавливаем специальные ширины для колонок
        sheet.setColumnWidth(0, 2000);  // № п/п
        sheet.setColumnWidth(1, 6000);  // Наименование параметра
        sheet.setColumnWidth(2, 2500);  // Позиция
        sheet.setColumnWidth(3, 5000);  // Наименование
        sheet.setColumnWidth(4, 2500);  // мин.
        sheet.setColumnWidth(5, 2500);  // макс.
        sheet.setColumnWidth(6, 2000);  // Ед. изм.
        sheet.setColumnWidth(7, 3000);  // Код типа объекта
        sheet.setColumnWidth(8, 4500);  // Название типа объекта
        sheet.setColumnWidth(9, 3000);  // Код параметра
        sheet.setColumnWidth(10, 5000); // Название параметра
        sheet.setColumnWidth(11, 4000); // Тип сигнала
        sheet.setColumnWidth(12, 2500); // Шкаф
        sheet.setColumnWidth(13, 3500); // Тип модуля
        sheet.setColumnWidth(14, 2500); // № модуля
        sheet.setColumnWidth(15, 2500); // Канал
    }

    private CellStyle createSectionStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * Создает заголовки таблицы
     */
    private void createHeaders(Sheet sheet, String[] headers, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            //cell.setCellValue(formatColumnName(headers[i]));
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Заполняет лист данными
     */
    private void fillData(Sheet sheet, List<Map<String, Object>> data, CellStyle dataStyle) {
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Map<String, Object> rowData = data.get(i);

            int cellIndex = 0;
            for (Object value : rowData.values()) {
                Cell cell = row.createCell(cellIndex++);
                setCellValue(cell, value);
                cell.setCellStyle(dataStyle);
            }
        }
    }

    /**
     * Устанавливает значение ячейки в зависимости от типа данных
     */
    private void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof java.util.Date) {
            cell.setCellValue((java.util.Date) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * Автоматически подстраивает ширину колонок
     */
    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            // Устанавливаем минимальную ширину
            if (sheet.getColumnWidth(i) < 2000) {
                sheet.setColumnWidth(i, 2000);
            }
            // Ограничиваем максимальную ширину
            if (sheet.getColumnWidth(i) > 8000) {
                sheet.setColumnWidth(i, 8000);
            }
        }
    }

    /**
     * Форматирует название колонки для отображения
     */
//    private String formatColumnName(String columnName) {
//        // Преобразуем имена колонок в более читаемый вид
//        return switch (columnName.toLowerCase()) {
//            case "id" -> "ID";
//            case "first_name" -> "Имя";
//            case "last_name" -> "Фамилия";
//            case "email" -> "Email";
//            case "phone" -> "Телефон";
//            case "department" -> "Отдел";
//            case "position" -> "Должность";
//            case "hire_date" -> "Дата найма";
//            case "salary" -> "Зарплата";
//            case "table_name" -> "Название таблицы";
//            case "table_schema" -> "Схема";
//            case "table_type" -> "Тип таблицы";
//            default -> columnName.substring(0, 1).toUpperCase() +
//                    columnName.substring(1).replace("_", " ");
//        };
//    }
}