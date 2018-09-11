package com.tao;

import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKGender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tao on 2018/7/3 0003.
 */

public class AfdData {

    // preview indext

    int index = 0 ;

    // face 检测数据
    ArrayList<AFD_FSDKFace> afd_Resoults = new ArrayList<>();
    List<ASGE_FSDKGender> ages =new ArrayList<>();

    // face检测源数据
    byte[] data ;

    public List<ASGE_FSDKGender> getAges() {
        return ages;
    }

    public void setAges(List<ASGE_FSDKGender> ages) {
        this.ages = ages;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ArrayList<AFD_FSDKFace> getAfd_Resoults() {
        return afd_Resoults;
    }

    public void setAfd_Resoults(ArrayList<AFD_FSDKFace> afd_Resoults) {
        this.afd_Resoults = afd_Resoults;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
