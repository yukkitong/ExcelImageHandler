package kr.co.uniess.kto.batch;

public class ExcelImage {
  String contentId;
  String title;
  String url;
  boolean isMain;

  @Override
  public String toString() {
    return "ExcelImage(" + 
          nameValue("contentId", contentId) + ", " +
          nameValue("title", title) + ", " +
          nameValue("url", url) + ", " +
          nameValue("isMain", String.valueOf(isMain)) +
        ")";
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof ExcelImage)) return false;
    if (o == this) return true;
    ExcelImage t = (ExcelImage) o;
    return contentId.equals(t.contentId) && url.equals(t.url);
  }

  private String nameValue(String name, String value) {
    return name + ": " + value;
  }
}