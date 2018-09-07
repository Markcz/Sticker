package com.example.chenzhen.sticker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenzhen on 2018/9/6.
 */

public class Cmd {

    private StringBuilder text;

    private List<CmdPicture> pictures ;

    private String videoPath;


    public Cmd(String path) {
        this.videoPath = path;
        this.pictures = new ArrayList<>();
    }


    /**
     * 获取视频路径
     * @return
     */
    public String getVideoPath() {
        return videoPath;
    }

    /**
     * 为视频添加文字(新增可以控制显示周期)
     *
     * @param ctxt  添加文字类
     */
    public Cmd addText(CmdText ctxt) {
        text = getText();
        text.append(ctxt.getTextFitler());
        return this;
    }

    /**
     * 获取添加文字命令
     * @return
     */
    private StringBuilder getText() {
        if (text == null || text.toString().equals("")) {
            text = new StringBuilder();
        } else {
            text.append(",");
        }
        return text;
    }


    /**
     * 获取滤镜效果
     *
     * @return
     */
    public StringBuilder getTexts() {
        return text;
    }

    /**
     * 为视频添加图片
     *
     * @param picture 添加的图片类
     * @return
     */
    public Cmd addPicture(CmdPicture picture) {
        pictures.add(picture);
        return this;
    }

    /**
     * 获取添加的图片类
     *
     * @return
     */
    public List<CmdPicture> getPictures() {
        return pictures;
    }

}
