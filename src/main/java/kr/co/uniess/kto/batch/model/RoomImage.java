package kr.co.uniess.kto.batch.model;

public class RoomImage {
    private String cotId;
    private String roomImg1;
    private String roomImg2;
    private String roomImg3;
    private String roomImg4;
    private String roomImg5;

    public void setCotId(String cotId) {
        this.cotId = cotId;
    }

    public String getCotId() {
        return cotId;
    }

    public void setRoomImg1(String roomImg1) {
        this.roomImg1 = roomImg1;
    }

    public String getRoomImg1() {
        return roomImg1;
    }

    public void setRoomImg2(String roomImg2) {
        this.roomImg2 = roomImg2;
    }

    public String getRoomImg2() {
        return roomImg2;
    }

    public void setRoomImg3(String roomImg3) {
        this.roomImg3 = roomImg3;
    }

    public String getRoomImg3() {
        return roomImg3;
    }

    public void setRoomImg4(String roomImg4) {
        this.roomImg4 = roomImg4;
    }

    public String getRoomImg4() {
        return roomImg4;
    }

    public void setRoomImg5(String roomImg5) {
        this.roomImg5 = roomImg5;
    }

    public String getRoomImg5() {
        return roomImg5;
    }

    public boolean contains(String imageId) {
        if (imageId == null) return false;
        if (roomImg1 != null && roomImg1.equals(imageId)) {
            return true;
        }
        if (roomImg2 != null && roomImg2.equals(imageId)) {
            return true;
        }
        if (roomImg3 != null && roomImg3.equals(imageId)) {
            return true;
        }
        if (roomImg4 != null && roomImg4.equals(imageId)) {
            return true;
        }
        if (roomImg5 != null && roomImg5.equals(imageId)) {
            return true;
        }
        return false;
    }
}
