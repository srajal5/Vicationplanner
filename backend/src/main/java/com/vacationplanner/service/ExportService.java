package com.vacationplanner.service;

import com.vacationplanner.model.TripPlan;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    
    public Resource exportToPdf(TripPlan tripPlan, String filename) throws IOException {
        File tempFile = File.createTempFile("trip-plan-", ".pdf");
        tempFile.deleteOnExit();
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Add PDF content here
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Trip Plan: " + tripPlan.getDestination());
                contentStream.endText();
                
                // Add more trip details
                float yPosition = 680;
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Start Date: " + tripPlan.getStartDate().format(DATE_FORMATTER));
                contentStream.endText();
                
                yPosition -= 15;
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("End Date: " + tripPlan.getEndDate().format(DATE_FORMATTER));
                contentStream.endText();
            }
            
            document.save(tempFile);
        }
        
        return new FileSystemResource(tempFile);
    }
    
    public Resource exportToExcel(TripPlan tripPlan, String filename) throws IOException {
        File tempFile = File.createTempFile("trip-plan-", ".xlsx");
        tempFile.deleteOnExit();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Trip Plan");
            
            // Add headers
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Destination");
            headerRow.createCell(1).setCellValue("Start Date");
            headerRow.createCell(2).setCellValue("End Date");
            headerRow.createCell(3).setCellValue("Theme");
            
            // Add data
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(tripPlan.getDestination());
            dataRow.createCell(1).setCellValue(tripPlan.getStartDate().format(DATE_FORMATTER));
            dataRow.createCell(2).setCellValue(tripPlan.getEndDate().format(DATE_FORMATTER));
            dataRow.createCell(3).setCellValue(tripPlan.getTheme());
            
            // Auto-size columns
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            
            try (FileOutputStream fileOut = new FileOutputStream(tempFile)) {
                workbook.write(fileOut);
            }
        }
        
        return new FileSystemResource(tempFile);
    }
}