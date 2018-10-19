package kr.co.uniess.kto.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XlsReader {
  
  private static final Logger logger = LoggerFactory.getLogger(XlsReader.class);

  public static List<ExcelImage> loadExcelFile(XlsMeta meta) throws IOException {
      return loadExcelFile(new File(meta.filePath), meta);
  }

  private static List<ExcelImage> loadExcelFile(File file, XlsMeta meta) throws IOException {
      final String sheetName = meta.sheetName;
      final int startRowIndex = meta.startRow;

      final int contentIdIndex = meta.contentIdColumn;
      final int contentTitleIndex = meta.contentTitleColumn;
      final int imagePathIndex = meta.imagePathColumn;
      final int primaryIndex = meta.primaryColumn;

      List<ExcelImage> result = new ArrayList<>();
      Workbook wb = null;
      try {
          wb = new HSSFWorkbook(new FileInputStream(file));
          final int numberOfSheets = wb.getNumberOfSheets();
          for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
              Sheet sheet = wb.getSheetAt(sheetIndex);
              if (sheet.getSheetName().equals(sheetName)) {
                int currentRowIndex = 0;
                for (Row row : sheet) {
                    currentRowIndex++;
                    if (currentRowIndex < startRowIndex) continue; // skip row

                    ExcelImage image = new ExcelImage();
                    image.contentId = getCellValue(row.getCell(contentIdIndex));
                    image.title = getCellValue(row.getCell(contentTitleIndex));
                    image.url = getCellValue(row.getCell(imagePathIndex));
                    image.isMain = "O".equalsIgnoreCase(getCellValue(row.getCell(primaryIndex)));

                    result.add(image);
                }
              }
          }
        } finally {
            if (wb != null) {
                wb.close();
            }
        }
        return result;
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
}