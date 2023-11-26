package genesis.goptsii;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelReaderTask2 {
    private static final String EXCEL_FILE_PATH = "C:/Users/daria/Downloads/G.xlsx";
    private static Map<String, UserInfo> userInfoMap = new HashMap<>();
    public static double rounderNumber(double number) {
        return Math.round(number * 100.0) / 100.0;
    }

    private static Map<String, Integer> sortByValueDescending(Map<String, Integer> unsortedMap) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        list.forEach(entry -> sortedMap.put(entry.getKey(), entry.getValue()));
        return sortedMap;
    }

    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream(new File(EXCEL_FILE_PATH));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowIndex = 1; // Start from the second row
            Row currentRow = sheet.getRow(rowIndex);


            while (currentRow != null && !isRowEmpty(currentRow)) {
                String userId = getStringCellValue(currentRow.getCell(0));
                String paymentType = getStringCellValue(currentRow.getCell(1));
                String country = getStringCellValue(currentRow.getCell(2));
                String device = getStringCellValue(currentRow.getCell(3));
                String language = getStringCellValue(currentRow.getCell(4));
                String theme = getStringCellValue(currentRow.getCell(5));
                String refund = getStringCellValue(currentRow.getCell(6));

                UserInfo userInfo = userInfoMap.computeIfAbsent(userId, k -> new UserInfo());
                userInfo.setCountry(country);
                userInfo.setDevice(device);
                userInfo.setLanguage(language);
                userInfo.setRefund(refund);
                userInfo.setTheme(theme);

                if ("free trial".equalsIgnoreCase(paymentType)) {
                    userInfo.setTriedFreeTrial(true);
                    userInfo.incrementFreeTrialCount();
                } else if ("recurrent".equalsIgnoreCase(paymentType)) {
                    userInfo.incrementPurchaseCount();
                }

                userInfoMap.put(userId, userInfo);

                rowIndex++;
                currentRow = sheet.getRow(rowIndex);
            }

            int tier1Count = 0, rwCount = 0, spanishCount = 0, otherCount = 0;
            Map<String, Integer> devices = new HashMap<>();
            Map<String, Integer> language = new HashMap<>();
            Map<String, Integer> themes = new HashMap<>();
            List<UserInfo> gotRefund = new ArrayList<>();

            for (Map.Entry<String, UserInfo> entry : userInfoMap.entrySet()) {
                String userId = entry.getKey();
                UserInfo userInfo = entry.getValue();

                displayUserInfo(userId, userInfo);

                // for users converted from 1st purchase to 2nd
                if (userInfo.getPurchaseCount() >= 2) {
                    categorizeUser(userInfo, devices, language, themes, gotRefund);
                    updateCountByCountry(userInfo, tier1Count, rwCount, spanishCount, otherCount);
                }
            }

            displaySortedMaps(language, devices, themes);
            displayAdditionalInfo(gotRefund, tier1Count, rwCount, spanishCount, otherCount);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayUserInfo(String userId, UserInfo userInfo) {
        System.out.println("User ID: " + userId);
        System.out.println("Tried Free Trial: " + userInfo.hasTriedFreeTrial());
        System.out.println("Number of Free Trials: " + userInfo.getFreeTrialCount());
        System.out.println("Number of Purchases: " + userInfo.getPurchaseCount());
        System.out.println("------------------------------");
    }


    private static void categorizeUser(UserInfo userInfo, Map<String, Integer> devices,
                                       Map<String, Integer> language, Map<String, Integer> themes,
                                       List<UserInfo> gotRefund) {
        String userLanguage = userInfo.getLanguage();
        String userTheme = userInfo.getTheme();
        String userDevice = userInfo.getDevice();

        language.merge(userLanguage, 1, Integer::sum);
        themes.merge(userTheme, 1, Integer::sum);
        devices.merge(userDevice, 1, Integer::sum);

        if (userInfo.getRefund() != null) {
            gotRefund.add(userInfo);
        }
    }

    private static void updateCountByCountry(UserInfo userInfo, int tier1Count,
                                             int rwCount, int spanishCount, int otherCount) {
        switch (userInfo.getCountry()) {
                        case "United States":
                        case "Canada":
                        case "United Kingdom":
                        case "Australia":
                            tier1Count++;
                            break;
                        case "Sweden":
                        case "Ireland":
                        case "Belgium":
                        case "France":
                        case "Italy":
                        case "Austria":
                        case "Germany":
                        case "Switzerland":
                        case "Norway":
                        case "Denmark":
                        case "Netherlands":
                        case "New Zealand":
                        case "Finland":
                        case "Luxembourg":
                        case "Iceland":
                            rwCount++;
                            break;
                        case "Chile":
                        case "Venezuela":
                        case "Spain":
                        case "Puerto Rico":
                        case "Bolivia":
                        case "Dominican Republic":
                        case "Ecuador":
                        case "Panama":
                        case "Paraguay":
                        case "Uruguay":
                            spanishCount++;
                            break;
                        default:
                            otherCount++;
                            break;
                    }

    }

    private static void displaySortedMaps(Map<String, Integer> language,
                                          Map<String, Integer> devices, Map<String, Integer> themes) {
        System.out.println(sortByValueDescending(language));
        System.out.println("---------------------------");
        System.out.println(sortByValueDescending(devices));
        System.out.println("---------------------------");
        System.out.println(sortByValueDescending(themes));
        System.out.println("---------------------------");
    }

    private static void displayAdditionalInfo(List<UserInfo> gotRefund,
                                              int tier1Count, int rwCount, int spanishCount, int otherCount) {
        System.out.println(gotRefund.size());
        System.out.println("---------------------------");
        System.out.println("Tier1: " + tier1Count);
        System.out.println("RW: " + rwCount);
        System.out.println("Spanish: " + spanishCount);
        System.out.println("Other: " + otherCount);
        System.out.println("Total: " + (tier1Count + rwCount + spanishCount + otherCount));
        System.out.println("---------------------------");
        displayConversionMetrics();
    }


    private static void displayConversionMetrics() {
        MetricsCalculator.MetricsSummary metricsSummary = MetricsCalculator.calculateMetrics(userInfoMap);
        System.out.println("Conversion from trial to 1st purchase: " + metricsSummary.getConversionCount(0));
        for (int i = 1; i <= 3; i++) {
            System.out.println(i + "st to " + (i + 1) + "st Conversion: " + metricsSummary.getConversionCount(i));
        }
    }


    private static String getStringCellValue(Cell cell) {
        if (cell == null) {
            return null; // Handle null cells if needed
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            default:
                return null; // Return null for other cell types
        }
    }

    private static double getNumericCellValue(Cell cell) {
        if (cell == null) {
            return 0; // Handle null cells if needed
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                double originalNumber = cell.getNumericCellValue();
                double roundedNumber = Math.round(originalNumber * 100.0) / 100.0;
                return roundedNumber;
            default:
                return 0; // Return 0 for other cell types
        }
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

}

