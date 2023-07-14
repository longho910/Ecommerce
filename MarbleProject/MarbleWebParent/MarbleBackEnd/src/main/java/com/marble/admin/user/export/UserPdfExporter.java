package com.marble.admin.user.export;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.marble.admin.AbstratExporter;
import com.marble.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;


public class UserPdfExporter extends AbstratExporter {
    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/pdf", ".pdf","users_");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLACK);

        // title
        Paragraph paragraph = new Paragraph("List of Users", font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        // table
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);
        table.setWidths(new float[] {1.2f, 3.5f, 3.0f, 3.0f, 3.0f, 1.7f});
        // table header
        writeTableHeader(table);
        // table data
        writeTableData(table, listUsers);

        document.add(table);
        document.close();
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(0,0,128));
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        Stream.of("ID", "Email", "First Name", "Last Name", "Roles", "Enabled")
                .forEach(columnTitle -> {
                    cell.setPhrase(new Phrase(columnTitle, font));
                    table.addCell(cell);
                });
    }

    private void writeTableData(PdfPTable table, List<User> listUsers) {
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA);
        cellFont.setColor(Color.BLACK);

        int count = 1;
        for (User user : listUsers) {
            if (count % 2 == 0) {
                table.getDefaultCell().setBackgroundColor(new Color(224,225,225));
            } else {
                table.getDefaultCell().setBackgroundColor(Color.WHITE);
            }
            table.addCell(new Phrase(String.valueOf(user.getId()), cellFont));
            table.addCell(new Phrase(user.getEmail(), cellFont));
            table.addCell(new Phrase(user.getFirstName(), cellFont));
            table.addCell(new Phrase(user.getLastName(), cellFont));
            table.addCell(new Phrase(user.getRoles().toString(), cellFont));
            table.addCell(new Phrase(String.valueOf(user.isEnabled()), cellFont));
            count++;
        }
    }
}
