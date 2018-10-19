package kr.co.uniess.kto.batch;

public class XlsMeta {

  String filePath;

  String sheetName;

  int startRow;

  int contentIdColumn;
  int contentTitleColumn;
  int imagePathColumn;
  int primaryColumn;

  PrimaryStrategy strategy;

  XlsMeta() {
  }

  PrimaryStrategy getPrimaryStrategy() {
    if (strategy == null) {
      strategy = new PrimaryStrategy() {
        @Override
        public boolean isPrimary(String value) {
          return "O".equals(value);
        }
      };
    }
    return strategy;
  }

  void setPrimaryStrategy(PrimaryStrategy strategy) {
    this.strategy = strategy;
  }

  public String getFilePath() {
    return filePath;
  }

  public String getSheetName() {
    return sheetName;
  }

  public int getStartRow() {
    return startRow;
  }

  public int getContentIdColumn() {
    return contentIdColumn;
  }

  public int getContentTitleColumn() {
    return contentTitleColumn;
  }

  public int getImagePathColumn() {
    return imagePathColumn;
  }

  public int getPrimaryColumn() {
    return primaryColumn;
  }

  public interface PrimaryStrategy {
    boolean isPrimary(String value);
  }
}