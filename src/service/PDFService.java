package service;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.File;

public class PDFService {

    // 🔥 EXPORT TABLE (already working)
    public static void exportTable(TableView<?> tableView, String fileName) {

        try {
            PdfWriter writer = new PdfWriter(new File(fileName));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Exported Data\n\n"));

            int columnCount = tableView.getColumns().size();
            Table table = new Table(columnCount);

            // Headers
            for (TableColumn<?, ?> col : tableView.getColumns()) {
                table.addCell(col.getText());
            }

            // Data
            for (Object row : tableView.getItems()) {
                for (TableColumn col : tableView.getColumns()) {
                    Object cell = col.getCellData(row);
                    table.addCell(cell == null ? "" : cell.toString());
                }
            }

            document.add(table);
            document.close();

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(new File(fileName));
            }

            System.out.println("PDF Exported!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 ADD THIS METHOD (FIX YOUR ERROR)
    public static void exportTextToPDF(String content, String fileName) {

        try {
            PdfWriter writer = new PdfWriter(new File(fileName));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph(content));

            document.close();

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(new File(fileName));
            }

            System.out.println("Text PDF Exported!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}