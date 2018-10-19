package kr.co.uniess.kto.batch;

public class XlsConfig {

  private String sheetName;
  private int sheetIndex;

  private int startRow;

  private int contentIdColumn;
  private int contentTitleColumn;
  private int imagePathColumn;
  private int mainColumn;

  private final FindMainStrategy defaultFindMainStrategy = new FindMainStrategy() {
    @Override
    public boolean isMain(String value) {
      return "O".equals(value);
    }
  };

  private FindMainStrategy strategy = defaultFindMainStrategy;

  private XlsConfig(Builder builder) {
    this.sheetIndex = builder.sheetIndex;
    this.sheetName = builder.sheetName;
    this.startRow = builder.startRow;
    this.contentIdColumn = builder.contentIdColumn;
    this.contentTitleColumn = builder.contentTitleColumn;
    this.imagePathColumn = builder.imagePathColumn;
    this.mainColumn = builder.mainColumn;
  }

  public FindMainStrategy getFindMainStrategy() {
    return strategy;
  }

  public void setFindMainStrategy(FindMainStrategy strategy) {
    this.strategy = strategy;
  }

  public String getSheetName() {
    return sheetName;
  }

  public int getSheetIndex() {
    return sheetIndex;
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

  public int getMainColumn() {
    return mainColumn;
  }

  public interface FindMainStrategy {
    boolean isMain(String value);
  }

  public static class Builder {
    int mainColumn;
    int imagePathColumn;
    int contentTitleColumn;
    int contentIdColumn;
    int startRow;
    String sheetName;
    int sheetIndex;
    
    public Builder sheetIndex(int index) {
      sheetIndex = index;
      return this;
    }

    public Builder sheetName(String name) {
      sheetName = name;
      return this;
    }

    public Builder startRow(int row) {
      startRow = row;
      return this;
    }

    public Builder colOfContentId(int col) {
      contentIdColumn = col;
      return this;
    }

    public Builder colOfContentTitle(int col) {
      contentTitleColumn = col;
      return this;
    }

    public Builder colOfImagePath(int col) {
      imagePathColumn = col;
      return this;
    }

    public Builder colOfMain(int col) {
      mainColumn = col;
      return this;
    }

    public XlsConfig build() {
      return new XlsConfig(this);
    }
  }
}