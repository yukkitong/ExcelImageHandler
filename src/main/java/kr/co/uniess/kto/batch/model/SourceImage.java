package kr.co.uniess.kto.batch.model;

import com.opencsv.bean.CsvBindByName;

public class SourceImage {

    @CsvBindByName(column="CONTENTID")
    public String contentId;

    @CsvBindByName(column="TITLE")
    public String title;

    @CsvBindByName(column="URL")
    public String url;

    @CsvBindByName(column="MAIN")
    public boolean main;

    public void setContentId(String id) {
        this.contentId = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    public boolean getMain() {
        return main;
    }

    @Override
    public String toString() {
        return "SourceImage(" + nameValue("contentId", contentId) + ", " + nameValue("title", title) + ", "
                + nameValue("url", url) + ", " + nameValue("isMain", String.valueOf(main)) + ")";
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