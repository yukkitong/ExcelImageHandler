package kr.co.uniess.kto.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import kr.co.uniess.kto.batch.model.SourceImage;

public class XlsReader {

    private final XlsConfig config;

    XlsReader(XlsConfig config) {
        if (config == null) {
            this.config = XlsConfig.Builder.getNullBuilder().build();
        } else {
            this.config = config;
        }
    }

    public List<SourceImage> read(String filePath) throws IOException {
        return read(new File(filePath));
    }

    public List<SourceImage> read(File file) throws IOException {
        final String[] sheetNames = config.getSheetNames();

        List<SourceImage> result = new ArrayList<>();
        Workbook wb = null;
        try {
            // wb = new HSSFWorkbook(new FileInputStream(file));
            wb = WorkbookFactory.create(file);
            final int numberOfSheets = wb.getNumberOfSheets();
            if (sheetNames != null) {
                for (String sheetName : sheetNames) {
                    for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                        Sheet sheet = wb.getSheetAt(sheetIndex);
                        if (sheet.getSheetName().equals(sheetName)) {
                            loadSheet(sheet, result);
                        }
                    }
                }
            } else {
                for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                    Sheet sheet = wb.getSheetAt(sheetIndex);
                    loadSheet(sheet, result);
                }
            }
        } finally {
            if (wb != null) {
                wb.close();
            }
        }
        return distinct(result);
    }

    private void loadSheet(Sheet sheet, List<SourceImage> result) {
        clearIndexCache();

        int startRowIndex = -1;
        for (int i = sheet.getFirstRowNum(), size = sheet.getLastRowNum(); i < size; i++) {
            Row row = sheet.getRow(i);
            if (findStartRowAndCacheColumn(row)) {
                startRowIndex = i;
            }
        }

        if (startRowIndex == -1) {
            throw new RuntimeException("Can't found Starting-row position. Please check the sheet of the excel file.");
        }

        for (int i = startRowIndex + 1 /* skip header + 1 */, size = sheet.getLastRowNum(); i < size; i++) {
            Row row = sheet.getRow(i);
            SourceImage image = new SourceImage();
            image.contentId = getContentId(row);
            image.title = getContentTitle(row);
            image.url = getImagePath(row);
            image.main = config.getFindMainStrategy().isMain(getMainChk(row));

            if (!isEmpty(image.url)) {
                result.add(image);
            }
        }
    }

    private boolean isEmpty(String str) {
        if (str == null) return true;
        return str.trim().isEmpty();
    }

    private boolean findStartRowAndCacheColumn(Row row) {
        boolean found = false;
        for (int col = row.getFirstCellNum(); col < row.getLastCellNum(); col++) {
            if (getCellValue(row.getCell(col)).equals("CONTENTID")) {
                indexCache.put("CONTENTID", col);
                found = true;
            }
            if (getCellValue(row.getCell(col)).equals("TITLE")) {
                indexCache.put("TITLE", col);
            }
            if (getCellValue(row.getCell(col)).equals("PATH")) {
                indexCache.put("PATH", col);
            }
            if (getCellValue(row.getCell(col)).equals("MAINIMGCHK")) {
                indexCache.put("MAINIMGCHK", col);
            }
        }
        return found;
    }

    private Map<String, Integer> indexCache = new HashMap<>();

    private void clearIndexCache() {
        indexCache.clear();
    }

    private String getContentId(Row row) {
        return getCellData(row, "CONTENTID");
    }

    private String getContentTitle(Row row) {
        return getCellData(row, "TITLE");
    }

    private String getImagePath(Row row) {
        return getCellData(row, "PATH");
    }

    private String getMainChk(Row row) {
        return getCellData(row, "MAINIMGCHK");
    }

    private String getCellData(Row row, String cellTitle) {
        int index = indexCache.get(cellTitle) == null ? -1 : indexCache.get(cellTitle);
        if (index > -1) {
            return getCellValue(row.getCell(index));
        }
        throw notFoundColumnException("Not found <" + cellTitle + "> column");
    }

    private RuntimeException notFoundColumnException(String cause) {
        return new RuntimeException(cause);
    }

    private static String getCellValue(Cell cell) {
        String valueString = "";
        if (cell != null) {
            CellType cellType = cell.getCellType();
            if (cellType.equals(CellType.STRING)) {
                valueString = cell.getStringCellValue().replace(";", " ");
            } else if (cellType.equals(CellType.BOOLEAN)) {
                valueString = String.valueOf(cell.getBooleanCellValue());
            } else if (cellType.equals(CellType.ERROR)) {
                valueString = "ERROR";
            } else if (cellType.equals(CellType.FORMULA)) {
                valueString = cell.getCellFormula();
            } else if (cellType.equals(CellType.NUMERIC)) {
                valueString = String.valueOf(cell.getNumericCellValue()).replace(".0", "");
            }
        }
        return valueString;
    }

    /**
     * 정렬이된 경우에만 정상동작한다.
     * @param list source list
     * @return list
     */
    private static List<SourceImage> distinct(List<SourceImage> list) {
        if (list.size() == 0) {
            return list;
        }

        ArrayList<SourceImage> result = new ArrayList<>(list.size());
        for (int i = 0, size = list.size(); i < size; i++) {
            SourceImage item = list.get(i);
            boolean first = i == 0;
            if (first) {
                result.add(item);
            } else {
                if (!result.get(result.size() - 1).equals(item)) {
                    result.add(item);
                }
            }
        }
        return result;
    }
}