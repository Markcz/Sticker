package com.example.chenzhen.sticker;

/**
 * Created by chenzhen on 2018/9/5.
 */

public class CmdPicture {

    private String picPath;
    private int picX;
    private int picY;
    private float picWidth;
    private float picHeight;
    private String time = "";
    private String picFilter;

    public CmdPicture(String picPath, int picX, int picY, float picWidth, float picHeight) {
        this.picPath = picPath;
        this.picX = picX;
        this.picY = picY;
        this.picWidth = picWidth;
        this.picHeight = picHeight;
    }

    public CmdPicture(String picPath, int picX, int picY, float picWidth, float picHeight, int start, int end) {
        this.picPath = picPath;
        this.picX = picX;
        this.picY = picY;
        this.picWidth = picWidth;
        this.picHeight = picHeight;
        this.time = ":enable=between(t\\," + start + "\\," + end + ")";
    }

    public String getPicPath() {
        return this.picPath;
    }

    public int getPicX() {
        return this.picX;
    }

    public int getPicY() {
        return this.picY;
    }

    public float getPicWidth() {
        return this.picWidth;
    }

    public float getPicHeight() {
        return this.picHeight;
    }

    public String getPicFilter() {
        return this.picFilter == null?"":this.picFilter + ",";
    }

    public String getTime() {
        return this.time;
    }

    public void setPicFilter(String picFilter) {
        this.picFilter = picFilter;
    }

}
