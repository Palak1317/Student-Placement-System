package service;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.FileWriter;

public class CSVService {

    public static void exportTable(TableView<?> table, String fileName) {

        try (FileWriter writer = new FileWriter(fileName)) {

            // Headers
            for (TableColumn<?, ?> col : table.getColumns()) {
                writer.append(col.getText()).append(",");
            }
            writer.append("\n");

            // Data
            for (Object row : table.getItems()) {
                for (TableColumn col : table.getColumns()) {
                    Object cell = col.getCellData(row);
                    writer.append(cell == null ? "" : cell.toString()).append(",");
                }
                writer.append("\n");
            }

            writer.flush();

            System.out.println("CSV Exported!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}