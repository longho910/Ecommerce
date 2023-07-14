package com.marble.admin.user.export;

import com.marble.admin.AbstratExporter;
import com.marble.common.entity.User;
import com.marble.common.entity.Role;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class UserExcelExporter extends AbstratExporter {
    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", "xslx","users_");

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Users");

        writeHeaderLine(sheet);

        for (User user : listUsers) {
            writeDataLine(user, sheet);
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    private void writeHeaderLine(XSSFSheet sheet) {
        XSSFRow headerRow = sheet.createRow(0);

        XSSFCell cell = headerRow.createCell(0);
        cell.setCellValue("ID");

        cell = headerRow.createCell(1);
        cell.setCellValue("Email");

        cell = headerRow.createCell(2);
        cell.setCellValue("First Name");

        cell = headerRow.createCell(3);
        cell.setCellValue("Last Name");

        cell = headerRow.createCell(4);
        cell.setCellValue("Roles");

        cell = headerRow.createCell(5);
        cell.setCellValue("Enabled");
    }

    private void writeDataLine(User user, XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);

        XSSFCell cell = row.createCell(0);
        cell.setCellValue(user.getId());

        cell = row.createCell(1);
        cell.setCellValue(user.getEmail());

        cell = row.createCell(2);
        cell.setCellValue(user.getFirstName());

        cell = row.createCell(3);
        cell.setCellValue(user.getLastName());

        cell = row.createCell(4);
        // Assuming the getRoles() returns a list, we join them with commas
        cell.setCellValue(String.join(", ", user.getRoles().stream().map(Role::getName).collect(Collectors.toList())));

        cell = row.createCell(5);
        cell.setCellValue(user.isEnabled() ? "True" : "False");
    }

}
