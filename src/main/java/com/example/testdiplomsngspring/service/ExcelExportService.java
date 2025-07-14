package com.example.testdiplomsngspring.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
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
import java.util.HashMap;
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
    public byte[] generateParametersExcel(String ip) throws Exception {
        // Получаем подключение к БД
        ConnectionWrapper connectionWrapper = connectionManager.getConnection(ip);
        JdbcTemplate jdbcTemplate = connectionWrapper.getJdbcTemplate();

        // Выполняем запрос к БД
        List<Map<String, Object>> employeeData = fetchParametersData(jdbcTemplate);

        // Создаем Excel файл
        return createExcelFile(employeeData);
    }

    /**
     * Выполняет запрос к базе данных для получения данных о сотрудниках
     *
     * @param jdbcTemplate JdbcTemplate для выполнения запроса
     * @return List<Map<String, Object>> данные сотрудников
     */
    private List<Map<String, Object>> fetchParametersData(JdbcTemplate jdbcTemplate) {
        try {
            String alternativeSql = "SELECT * FROM parameters";
            System.out.println("Trying alternative query: " + alternativeSql);
            return jdbcTemplate.queryForList(alternativeSql);
        } catch (DataAccessException e) {
            throw new RuntimeException("Не удалось получить данные из базы данных: " + e.getMessage(), e);
        }
    }

    private byte[] createExcelFile(List<Map<String, Object>> data) throws IOException {
        Map<String, Object> response = new HashMap<>();
        //System.out.println("data.toString() " + data.toString());
        try (XSSFWorkbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Параметры");

            // Создаем стили
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle sectionStyle = createSectionStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            //Строим левую часть шапки
            createLeftParameterHeaders(sheet, headerStyle, workbook);
            //Строим правую часть шапки
            createRightParameterHeaders(sheet, headerStyle, workbook);

            // Заполняем данными
            fillParameterData(sheet, data, sectionStyle, dataStyle, workbook);

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
        font.setBold(false);
        font.setFontHeightInPoints((short) 11);
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
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private void createRightParameterHeaders(Sheet sheet, CellStyle headerStyle, Workbook workbook) {// Создаем или получаем строки для правой части шапки
        Row headerRow1 = sheet.getRow(2) != null ? sheet.getRow(2) : sheet.createRow(2);
        Row headerRow2 = sheet.getRow(3) != null ? sheet.getRow(3) : sheet.createRow(3);
        Row headerRow3 = sheet.getRow(4) != null ? sheet.getRow(4) : sheet.createRow(4);

        // Устанавливаем высоту строк
        headerRow1.setHeight((short)(2 * sheet.getDefaultRowHeight()));
        headerRow2.setHeight((short)(2 * sheet.getDefaultRowHeight()));
        headerRow3.setHeight((short)(1 * sheet.getDefaultRowHeight()));

        // Создаем стиль с переносом слов
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.cloneStyleFrom(headerStyle);
        wrapStyle.setWrapText(true);

        // Первая строка правой части заголовков (колонки 8-16)
        String[] rightHeadersRow1 = {
                "Описание в БД",
                "",
                "",
                "",
                "Шкаф контроллера",
                "",
                "",
                "",
                ""
        };

        // Начинаем с колонки 8 (индекс 7)
        for (int i = 0; i < rightHeadersRow1.length; i++) {
            Cell cell = headerRow1.createCell(i + 7);
            cell.setCellValue(rightHeadersRow1[i]);
            cell.setCellStyle(wrapStyle);
        }

        // Вторая строка правой части заголовков
        String[] rightHeadersRow2 = {
                "Код типа объекта",
                "Название типа объекта",
                "Код параметра",
                "Название параметра",
                "Тип сигнала",
                "Шкаф",
                "Тип модуля",
                "№ модуля",
                "Канал"
        };

        for (int i = 0; i < rightHeadersRow2.length; i++) {
            Cell cell = headerRow2.createCell(i + 7);
            cell.setCellValue(rightHeadersRow2[i]);
            cell.setCellStyle(wrapStyle);
        }

        // Третья строка правой части заголовков (нумерация колонок)
        String[] rightHeadersRow3 = {
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",
                "16"
        };

        for (int i = 0; i < rightHeadersRow3.length; i++) {
            Cell cell = headerRow3.createCell(i);
            cell.setCellValue(rightHeadersRow3[i]);
            cell.setCellStyle(wrapStyle);
        }

        // Объединяем ячейки в правой части
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 7, 10)); // Описание в БД (H2:K2)
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 11, 15)); // Шкаф контроллера (L2:P2)
    }

    private void createLeftParameterHeaders(Sheet sheet, CellStyle headerStyle, Workbook workbook) {
        // Создаем строки для левой части шапки
        Row headerRow1 = sheet.createRow(2);
        Row headerRow2 = sheet.createRow(3);
        Row headerRow3 = sheet.createRow(4);

        // Устанавливаем высоту строк
        headerRow1.setHeight((short)(2 * sheet.getDefaultRowHeight()));
        headerRow2.setHeight((short)(2 * sheet.getDefaultRowHeight()));
        headerRow3.setHeight((short)(1 * sheet.getDefaultRowHeight()));

        // Создаем стиль с переносом слов
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.cloneStyleFrom(headerStyle);
        wrapStyle.setWrapText(true);

        // Ячейка A2-A4: "№ п/п" (объединенная вертикально)
        Cell cellA2 = headerRow1.createCell(0);
        cellA2.setCellValue("№\nп/п");
        cellA2.setCellStyle(wrapStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));

        Cell cellB2 = headerRow1.createCell(1);
        cellB2.setCellValue("Наименование\nпараметра");
        cellB2.setCellStyle(wrapStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 1, 1));

        Cell cellC1 = headerRow1.createCell(2);
        cellC1.setCellValue("Источник / приемник\nсигнала");
        cellC1.setCellStyle(wrapStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, 3));

        Cell cellC2 = headerRow2.createCell(2);
        cellC2.setCellValue("Позиция");
        cellC2.setCellStyle(wrapStyle);

        Cell cellD2 = headerRow2.createCell(3);
        cellD2.setCellValue("Наименование");
        cellD2.setCellStyle(wrapStyle);

        Cell cellE1 = headerRow1.createCell(4);
        cellE1.setCellValue("Диапазон\nизмерения*");
        cellE1.setCellStyle(wrapStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 5));

        Cell cellE2 = headerRow2.createCell(4);
        cellE2.setCellValue("мин.");
        cellE2.setCellStyle(wrapStyle);

        Cell cellF2 = headerRow2.createCell(5);
        cellF2.setCellValue("макс.");
        cellF2.setCellStyle(wrapStyle);

        Cell cellG1 = headerRow1.createCell(6);
        cellG1.setCellValue("Ед.\nизм.");
        cellG1.setCellStyle(wrapStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 6, 6));
    }

    private void setupColumnWidths(Sheet sheet) {
        // Устанавливаем специальные ширины для колонок
        sheet.setColumnWidth(0, 2000);  // № п/п
        sheet.setColumnWidth(1, 4000);  // Наименование параметра
        sheet.setColumnWidth(2, 2500);  // Позиция
        sheet.setColumnWidth(3, 3300);  // Наименование
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
        font.setBold(false);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private void fillParameterData(Sheet sheet, List<Map<String, Object>> data,
                                   CellStyle sectionStyle, CellStyle dataStyle, Workbook workbook) {
        int currentRow = 5; // Начинаем после заголовков

        // Создаем стиль для объединенных ячеек
        CellStyle mergedStyle = workbook.createCellStyle();
        mergedStyle.cloneStyleFrom(dataStyle);
        mergedStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        mergedStyle.setAlignment(HorizontalAlignment.CENTER);
        mergedStyle.setWrapText(true);

        createSectionRow(sheet, currentRow++, sectionStyle, "Операторная");
        fillParameterRow(sheet, currentRow, data, dataStyle, mergedStyle);
        //currentRow += data.size() * 2;
        //createSectionRow(sheet, currentRow++, sectionStyle, "Вход ДНС линия 1");
    }

    private void createSectionRow(Sheet sheet, int rowNum, CellStyle style, String sectionName) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(sectionName);
        cell.setCellStyle(style);

    }

    private void fillParameterRow(Sheet sheet, int startRow, List<Map<String, Object>> data,
                                  CellStyle dataStyle, CellStyle mergedStyle) {
        int rowNum = startRow;
        String[] keys = {"id", "parameter_name_raw", "position", "device_name", "range_min", "range_max", "unit", "object_type_code", "object_type_name","parameter_code",
                "parameter_description", "signal_type","cabinet","module_type", "module_number", "channel"};

        for (Map<String, Object> rowData : data) {
            // Создаем две строки для каждой записи
            Row row1 = sheet.createRow(rowNum++);
            Row row2 = sheet.createRow(rowNum++);
            //System.out.println("data: " + rowData);

            row1.setHeight((short)(280 + sheet.getDefaultRowHeight()));

            for (int i = 0; i < keys.length; i++) {
                Object value = rowData.get(keys[i]);
                String cellValue = (value != null) ? value.toString() : "203204";
                //System.out.println("rowData" + rowData.get(keys[i]));
                createAndMergeCell(sheet, row1, cellValue, dataStyle, rowNum - 2, i);
            }

            RegionUtil.setBorderTop(BorderStyle.THIN, new CellRangeAddress(rowNum-2, rowNum-1, 0, 0), sheet);
            RegionUtil.setBorderBottom(BorderStyle.THIN, new CellRangeAddress(rowNum-2, rowNum-1, 0, 0), sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, new CellRangeAddress(rowNum-2, rowNum-1, 0, 0), sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, new CellRangeAddress(rowNum-2, rowNum-1, 0, 0), sheet);
        }
    }

    private void createAndMergeCell(Sheet sheet, Row row, String value, CellStyle style, int rowNum, int colNum) {
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 1, colNum, colNum));
    }
}