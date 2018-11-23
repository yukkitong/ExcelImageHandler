package kr.co.uniess.kto.batch.repository.model;

public class Image {
    private String imgId;
    private String cotId;
    private String imageDescription;
    private String path;
    private String url;
    private boolean thumbnail;

    public void setImgId(String id) {
        this.imgId = id;
    }

    public void setCotId(String id) {
        this.cotId = id;
    }

    public void setImageDescription(String desc) {
        this.imageDescription = desc;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() { return path; }
    public String getImgId() { return imgId; }
    public String getCotId() { return cotId; }
    public String getImageDesciption() { return imageDescription; }
    public String getUrl() { return url; }
    public boolean getThumbnail() { return thumbnail; }

    @Override
    public String toString() {
        return "Image( imgId: " + imgId
                + ", cotId: " + cotId
                + ", desc: " + imageDescription
                + ", path: " + path
                + ", url: " + url
                + ", thumb: " + thumbnail
                + " )";
    }
}