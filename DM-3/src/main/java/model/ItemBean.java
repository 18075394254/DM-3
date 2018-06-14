package model;

/**
 * Created by Administrator on 2017/1/9 0009.
 */
public class ItemBean {
    private String msg;
    private int imageId;
    private boolean isShow; // 是否显示CheckBox
    private boolean isChecked; // 是否选中CheckBox

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public boolean isShow() {
        return isShow;
    }
    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public ItemBean(String msg, int imageId, boolean isShow, boolean isChecked) {
        this.msg = msg;
        this.imageId = imageId;
        this.isShow = isShow;
        this.isChecked = isChecked;
    }

}