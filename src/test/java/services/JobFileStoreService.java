package services;

import models.JobModel;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class JobFileStoreService {

        public static void saveJobs(List<JobModel> jobs) {
                if (jobs == null) {
                        System.out.println("Job list is null.");
                        return;
                }

                // ==========================================
                // TOTAL JOBS RECEIVED
                // ==========================================
                System.out.println("Jobs received for saving : " + jobs.size());

                // ==========================================
                // CREATE OUTPUT DIRECTORY
                // ==========================================
                File outputDir = new File("output");
                if (!outputDir.exists()) {
                        boolean folderCreated = outputDir.mkdirs();
                        System.out.println("Output folder created : " + folderCreated);
                }

                // ==========================================
                // OUTPUT FILE
                // ==========================================
                File outputFile = new File(outputDir, "latest_jobs.xlsx");

                // ==========================================
                // DELETE OLD FILE
                // ==========================================
                if (outputFile.exists()) {
                        boolean deleted = outputFile.delete();
                        System.out.println("Old report deleted : " + deleted);
                }

                // ==========================================
                // CREATE EXCEL WORKBOOK
                // ==========================================
                try (Workbook workbook = new XSSFWorkbook()) {
                        Sheet sheet = workbook.createSheet("Latest Jobs");

                        // ------------------------------------------
                        // STYLES & FONTS
                        // ------------------------------------------
                        // Header Font
                        Font headerFont = workbook.createFont();
                        headerFont.setBold(true);
                        headerFont.setFontHeightInPoints((short) 11);
                        headerFont.setColor(IndexedColors.WHITE.getIndex());

                        // Header Style (Premium Dark Blue)
                        CellStyle headerCellStyle = workbook.createCellStyle();
                        headerCellStyle.setFont(headerFont);
                        headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        headerCellStyle.setBorderBottom(BorderStyle.THIN);
                        headerCellStyle.setBorderTop(BorderStyle.THIN);
                        headerCellStyle.setBorderLeft(BorderStyle.THIN);
                        headerCellStyle.setBorderRight(BorderStyle.THIN);
                        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

                        // Hyperlink Font
                        Font linkFont = workbook.createFont();
                        linkFont.setUnderline(Font.U_SINGLE);
                        linkFont.setColor(IndexedColors.BLUE.getIndex());

                        // Hyperlink Cell Style
                        CellStyle linkCellStyle = workbook.createCellStyle();
                        linkCellStyle.setFont(linkFont);
                        linkCellStyle.setBorderBottom(BorderStyle.THIN);
                        linkCellStyle.setBorderTop(BorderStyle.THIN);
                        linkCellStyle.setBorderLeft(BorderStyle.THIN);
                        linkCellStyle.setBorderRight(BorderStyle.THIN);

                        // Standard Data Cell Style (Thin Borders)
                        CellStyle borderStyle = workbook.createCellStyle();
                        borderStyle.setBorderBottom(BorderStyle.THIN);
                        borderStyle.setBorderTop(BorderStyle.THIN);
                        borderStyle.setBorderLeft(BorderStyle.THIN);
                        borderStyle.setBorderRight(BorderStyle.THIN);

                        // ------------------------------------------
                        // WRITE HEADERS
                        // ------------------------------------------
                        Row headerRow = sheet.createRow(0);
                        String[] columns = { "JOB_NO", "COMPANY_NAME", "ROLE", "PORTAL", "LOCATION", "POSTED_DATE",
                                        "JOB_URL" };
                        for (int i = 0; i < columns.length; i++) {
                                Cell cell = headerRow.createCell(i);
                                cell.setCellValue(columns[i]);
                                cell.setCellStyle(headerCellStyle);
                        }

                        CreationHelper createHelper = workbook.getCreationHelper();

                        // ------------------------------------------
                        // WRITE JOB ROWS
                        // ------------------------------------------
                        int rowNum = 1;
                        for (JobModel job : jobs) {
                                Row row = sheet.createRow(rowNum);

                                // Column 0: JOB number
                                Cell cell0 = row.createCell(0);
                                cell0.setCellValue(rowNum);
                                cell0.setCellStyle(borderStyle);

                                // Column 1: Company Name
                                Cell cell1 = row.createCell(1);
                                cell1.setCellValue(job.getCompany() != null ? job.getCompany() : "");
                                cell1.setCellStyle(borderStyle);

                                // Column 2: Role
                                Cell cell2 = row.createCell(2);
                                cell2.setCellValue(job.getRole() != null ? job.getRole() : "");
                                cell2.setCellStyle(borderStyle);

                                // Column 3: Portal
                                Cell cell3 = row.createCell(3);
                                cell3.setCellValue(job.getPortal() != null ? job.getPortal() : "");
                                cell3.setCellStyle(borderStyle);

                                // Column 4: Location
                                Cell cell4 = row.createCell(4);
                                cell4.setCellValue(job.getLocation() != null ? job.getLocation() : "");
                                cell4.setCellStyle(borderStyle);

                                // Column 5: Posted
                                Cell cell5 = row.createCell(5);
                                cell5.setCellValue(job.getPostedDate() != null ? job.getPostedDate() : "");
                                cell5.setCellStyle(borderStyle);

                                // Column 6: Job URL (as active hyperlink)
                                Cell cell6 = row.createCell(6);
                                String applyLink = job.getApplyLink();
                                if (applyLink != null && !applyLink.trim().isEmpty()
                                                && !applyLink.equalsIgnoreCase("N/A")) {
                                        cell6.setCellValue(applyLink);
                                        try {
                                                Hyperlink link = createHelper.createHyperlink(HyperlinkType.URL);
                                                link.setAddress(applyLink);
                                                cell6.setHyperlink(link);
                                                cell6.setCellStyle(linkCellStyle);
                                        } catch (Exception ex) {
                                                // Fallback to plain text style if hyperlink creation fails
                                                cell6.setCellStyle(borderStyle);
                                        }
                                } else {
                                        cell6.setCellValue("N/A");
                                        cell6.setCellStyle(borderStyle);
                                }

                                rowNum++;
                        }

                        // ------------------------------------------
                        // AUTO-SIZE COLUMNS
                        // ------------------------------------------
                        for (int i = 0; i < columns.length; i++) {
                                sheet.autoSizeColumn(i);
                                int currentWidth = sheet.getColumnWidth(i);
                                // Add small padding for cell aesthetics, capped at max allowed column width
                                sheet.setColumnWidth(i, Math.min(255 * 256, currentWidth + 1024));
                        }

                        // ------------------------------------------
                        // WRITE TO FILE
                        // ------------------------------------------
                        try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                                workbook.write(fileOut);
                        }

                        System.out.println("Jobs saved successfully to Excel.");
                        System.out.println("Saved file path : " + outputFile.getAbsolutePath());
                        System.out.println("Total unique jobs saved : " + jobs.size());

                } catch (Exception e) {
                        System.out.println("Failed to save jobs to Excel.");
                        e.printStackTrace();
                }
        }
}