package kr.co.uniess.kto.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.uniess.kto.batch.model.ExcelImage;

public class XlsReader {

    private final static Logger logger = LoggerFactory.getLogger(XlsReader.class);

    private final XlsConfig config;

    public XlsReader() {
        this(null);
    }

    public XlsReader(XlsConfig config) {
        if (config == null) {
            this.config = XlsConfig.Builder.getNullBuilder().build();
        } else {
            this.config = config;
        }
    }

    public List<ExcelImage> loadExcelFile(String filePath) throws IOException {
        return loadExcelFile(new File(filePath));
    }

    public List<ExcelImage> loadExcelFile(File file) throws IOException {
        final String[] sheetNames = config.getSheetNames();

        List<ExcelImage> result = new ArrayList<>();
        Workbook wb = null;
        try {
            wb = new HSSFWorkbook(new FileInputStream(file));
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

    private void loadSheet(Sheet sheet, List<ExcelImage> result) {
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
            ExcelImage image = new ExcelImage();
            image.contentId = getContentId(row);
            image.title = getContentTitle(row);
            image.url = getImagePath(row);
            image.main = config.getFindMainStrategy().isMain(getMainChk(row));
            result.add(image);
        }
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
        int contentIdIndex = indexCache.get("CONTENTID") == null ? -1 : indexCache.get("CONTENTID");
        if (contentIdIndex > -1) {
            return getCellValue(row.getCell(contentIdIndex));
        }
        throw notFoundColumnException("Not found <CONTENTID> column");
    }

    private String getContentTitle(Row row) {
        int contentTitleIndex = indexCache.get("TITLE") == null ? -1 : indexCache.get("TITLE");
        if (contentTitleIndex > -1) {
            return getCellValue(row.getCell(contentTitleIndex));
        }
        throw notFoundColumnException("Not found <TITLE> column");
    }

    private String getImagePath(Row row) {
        int imagePathIndex = indexCache.get("PATH") == null ? -1 : indexCache.get("PATH");
        if (imagePathIndex > -1) {
            return getCellValue(row.getCell(imagePathIndex));
        }
        throw notFoundColumnException("Not found <PATH> column");
    }

    private String getMainChk(Row row) {
        int mainIndex = indexCache.get("MAINIMGCHK") == null ? -1 : indexCache.get("MAINIMGCHK");
        if (mainIndex > -1) {
            return getCellValue(row.getCell(mainIndex));
        }
        throw notFoundColumnException("Not found <MAINIMGCHK> column");
    }

    private RuntimeException notFoundColumnException(String cause) {
        return new RuntimeException(cause);
    }

    private static String getCellValue(Cell cell) {
        String valueString = "";
        if (cell != null) {
            CellType cellType = cell.getCellType();
            if (cellType.equals(CellType.STRING)) {
                valueString = cell.getStringCellValue();
            } else if (cellType.equals(CellType.BLANK)) {
                valueString = "";
            } else if (cellType.equals(CellType.BOOLEAN)) {
                valueString = String.valueOf(cell.getBooleanCellValue());
            } else if (cellType.equals(CellType.ERROR)) {
                valueString = "ERROR";
            } else if (cellType.equals(CellType.FORMULA)) {
                valueString = cell.getCellFormula();
            } else if (cellType.equals(CellType.NUMERIC)) {
                valueString = String.valueOf(cell.getNumericCellValue());
            }
        }
        return valueString;
    }

    /**
     * 정렬이된 경우에만 정상동작한다.
     * 
     * @param list
     * @return
     */
    private static List<ExcelImage> distinct(List<ExcelImage> list) {
        if (list.size() == 0) {
            return list;
        }

        ArrayList<ExcelImage> result = new ArrayList<>(list.size());
        for (int i = 0, size = list.size(); i < size; i++) {
            ExcelImage item = list.get(i);
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