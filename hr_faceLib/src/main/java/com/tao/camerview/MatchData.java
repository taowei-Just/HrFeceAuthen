package com.tao.camerview;

import android.graphics.Rect;


/**
 * Created by Tao on 2018/7/3 0003.
 */

public class MatchData {
    float mScore = 0.0F;
    byte [] soursData ;
    int sourWidth ;
    int sourHeidht ;
     Rect  rect ;

    public float getmScore() {
        return mScore;
    }

    public void setmScore(float mScore) {
        this.mScore = mScore;
    }

    public byte[] getSoursData() {
        return soursData;
    }

    public void setSoursData(byte[] soursData) {
        this.soursData = soursData;
    }

    public int getSourWidth() {
        return sourWidth;
    }

    public void setSourWidth(int sourWidth) {
        this.sourWidth = sourWidth;
    }

    public int getSourHeidht() {
        return sourHeidht;
    }

    public void setSourHeidht(int sourHeidht) {
        this.sourHeidht = sourHeidht;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    @Override
    public String toString() {
        return "MatchData{" +
                "mScore=" + mScore +
                ", sourWidth=" + sourWidth +
                ", sourHeidht=" + sourHeidht +
                ", rect=" + rect +
                '}';
    }
}
