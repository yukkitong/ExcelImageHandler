package kr.co.uniess.kto.batch.model;

import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.Setter;

public class SourceImage {

    @CsvBindByName(column="CONTENTID")
    @Setter
    @Getter
    public String contentId;

    @CsvBindByName(column="TITLE")
    @Setter
    @Getter
    public String title;

    @CsvBindByName(column="URL")
    @Setter
    @Getter
    public String url;

    @CsvBindByName(column="MAIN")
    @Setter
    @Getter
    public boolean main;

    @Override
    public String toString() {
        return "SourceImage(" + nameValue("contentId", contentId) + ", " + nameValue("title", title) + ", "
                + nameValue("url", url) + ", " + nameValue("main", String.valueOf(main)) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof SourceImage))
            return false;
        if (o == this)
            return true;
            SourceImage t = (SourceImage) o;
        return contentId.equals(t.contentId) && url.equals(t.url);
    }

    private String nameValue(String name, String value) {
        return name + ": " + value;
    }
}