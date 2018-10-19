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

import kr.co.uniess.kto.batch.model.ExcelImage;


public class XlsReader {

  private final XlsConfig config;
  
  public XlsReader(XlsConfig config) {
    this.config = config;
  }

  public List<ExcelImage> loadExcelFile(String filePath) throws IOException {
      return loadExcelFile(new File(filePath));
  }

  public List<ExcelImage> loadExcelFile(File file) throws IOException {
      final String sheetName = config.getSheetName();
      final int startRowIndex = config.getStartRow();

      final int contentIdIndex = config.getContentIdColumn();
      final int contentTitleIndex = config.getContentTitleColumn();
      final int imagePathIndex = config.getImagePathColumn();
      final int mainIndex = config.getImagePathColumn();

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
                    image.isMain = "O".equalsIgnoreCase(getCellValue(row.getCell(mainIndex)));

                    result.add(image);
                }
              }
          }
        } finally {
            if (wb != null) {
                wb.close();
            }
        }

        return distinct(result);
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
	 * @param list
	 * @return
	 */
	private static List<ExcelImage> distinct(List<ExcelImage> list) {
        if (list.size() == 0) return list;
		ArrayList<ExcelImage> result = new ArrayList<>(list.size());
		for (int i = 0, size = list.size(); i < size; i ++) {
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