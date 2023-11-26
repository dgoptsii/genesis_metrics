package genesis.goptsii;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReaderTask1 {
    private static final String EXCEL_FILE_PATH = "C:/Users/daria/Downloads/G.xlsx";

    public static double rounderNumber(double number) {
        return Math.round(number * 100.0) / 100.0;
    }

    private static String getStringCellValue(Cell cell) {
        return (cell != null && cell.getCellType() == CellType.STRING) ? cell.getStringCellValue() : null;
    }

    private static double getNumericCellValue(Cell cell) {
        return (cell != null && cell.getCellType() == CellType.NUMERIC) ?
                rounderNumber(cell.getNumericCellValue()) : 0;
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream(new File(EXCEL_FILE_PATH));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowIndex = 1; // Start from the second row to exclude header
            Row currentRow = sheet.getRow(rowIndex);

            Map<String, Integer> impressionsBySource = new HashMap<>();
            Map<String, Integer> clicksBySource = new HashMap<>();
            Map<String, Integer> installsBySource = new HashMap<>();
            Map<String, Double> crToInstallBySource = new HashMap<>();
            Map<String, Integer> conversionActionBySource = new HashMap<>();
            Map<String, Integer> totalCostBySource = new HashMap<>();
            Map<String, Integer> totalRevenueBySource = new HashMap<>();

            while (currentRow != null && !isRowEmpty(currentRow)) {
                String mediaSource = getStringCellValue(currentRow.getCell(1));
                int impressions = (int) getNumericCellValue(currentRow.getCell(2));
                int clicks = (int) getNumericCellValue(currentRow.getCell(3));
                int installs = (int) getNumericCellValue(currentRow.getCell(4));
                double crToInstall = getNumericCellValue(currentRow.getCell(5));
                int conversionAction = (int) getNumericCellValue(currentRow.getCell(6));
                int totalCost = (int) getNumericCellValue(currentRow.getCell(7));
                int totalRevenue = (int) getNumericCellValue(currentRow.getCell(8));

                impressionsBySource.merge(mediaSource, impressions, Integer::sum);
                clicksBySource.merge(mediaSource, clicks, Integer::sum);
                installsBySource.merge(mediaSource, installs, Integer::sum);
                crToInstallBySource.merge(mediaSource, crToInstall, Double::sum);
                conversionActionBySource.merge(mediaSource, conversionAction, Integer::sum);
                totalCostBySource.merge(mediaSource, totalCost, Integer::sum);
                totalRevenueBySource.merge(mediaSource, totalRevenue, Integer::sum);

                rowIndex++;
                currentRow = sheet.getRow(rowIndex);
            }

            // Display the results for each media source
            for (String source : impressionsBySource.keySet()) {
                System.out.println("Media Source: " + source);
                System.out.println("Total Impressions: " + impressionsBySource.get(source));
                System.out.println("Total Clicks: " + clicksBySource.get(source));
                System.out.println("Total Installs: " + installsBySource.get(source));
                System.out.println("Total CR to Install: " + rounderNumber(crToInstallBySource.get(source)));
                System.out.println("Total Conversion Action: " + conversionActionBySource.get(source));
                System.out.println("Total Total Cost: " + totalCostBySource.get(source));
                System.out.println("Total Total Revenue: " + totalRevenueBySource.get(source));
                System.out.println("------------------------------");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
