package com.example.chenzhen.sticker;

/**
 * Created by chenzhen on 2018/9/5.
 */

public class CmdText {


    private String textFitler;

    public CmdText(int x, int y, float size, CmdText.Color color, String ttf, String text, CmdText.Time time) {
        this.textFitler = "drawtext=fontfile=" + ttf + ":fontsize=" + size + ":fontcolor=" + color.getColor() + ":x=" + x + ":y=" + y + ":text='" + text + "'" + (time == null?"":time.getTime());
    }

    public String getTextFitler() {
        return this.textFitler;
    }

    public static enum Color {
        Red("Red"),
        Blue("Blue"),
        Yellow("Yellow"),
        Black("Black"),
        DarkBlue("DarkBlue"),
        Green("Green"),
        SkyBlue("SkyBlue"),
        Orange("Orange"),
        White("White"),
        Cyan("Cyan");

        private String color;

        private Color(String color) {
            this.color = color;
        }

        public String getColor() {
            return this.color;
        }
    }

    public static class Time {
        private String time;

        public Time(int start, int end) {
            this.time = ":enable=between(t\\," + start + "\\," + end + ")";
        }

        public String getTime() {
            return this.time;
        }
    }
}
