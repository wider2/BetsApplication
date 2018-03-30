package march.marchappl.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.AppCompatButton;
import android.widget.Button;

import java.util.ArrayList;

import march.marchappl.BetsApplication;


public class ColorUtility {

    public static int getColor(@ColorRes int res) {
        return BetsApplication.getApp().getResources().getColor(res);
    }

    public static String getHexColor(int color) {
        String hex = Integer.toHexString(color);
        int startFrom = hex.length() - 6;
        return "#" + hex.substring(startFrom);
    }


    public static int getColorInverse(int color) {
        return Color.rgb(255 - Color.red(color), 255 - Color.green(color), 255 - Color.blue(color));
    }


    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.6f;
        color = Color.HSVToColor(hsv);
        return color;
    }


    public static int mediumLightColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] *= 0.6f;
        color = Color.HSVToColor(hsv);
        return color;
    }


    public static int lightenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] *= 0.1f;
        color = Color.HSVToColor(hsv);
        return color;
    }


    public static int modifyColorAlpha(int color, float alpha) {
        return Color.argb((int) (Color.alpha(color) * alpha), Color.red(color), Color.green(color), Color.blue(color));
    }

}