package kr.co.uniess.kto.batch.model;

import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.Setter;

public class ExcelImage {

  @CsvBindByName
  @Setter
  @Getter
  public String contentId;

  @CsvBindByName
  @Setter
  @Getter
  public String title;

  @CsvBindByName
  @Setter
  @Getter
  public String url;

  @CsvBindByName
  @Setter
  @Getter
  public boolean main;

  @Override
  public String toString() {
    return "ExcelImage(" + 
          nameValue("contentId", contentId) + ", " +
          nameValue("title", title) + ", " +
          nameValue("url", url) + ", " +
          nameValue("main", String.valueOf(main)) +
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