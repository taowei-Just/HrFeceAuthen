package com.tao.face.data;

/**
 * Created by Tao on 2018/6/7 0007.
 */


public   class FaceResoult {
    FeatureBeen featureBeen;
    byte[] sourceData;
    byte[] sourceFeacture;
    byte[] sceneFeacture;
    int scro;

    public FeatureBeen getFeatureBeen() {
        return featureBeen;
    }

    public void setFeatureBeen(FeatureBeen featureBeen) {
        this.featureBeen = featureBeen;
    }

    public byte[] getSourceFeacture() {
        return sourceFeacture;
    }

    public void setSourceFeacture(byte[] sourceFeacture) {
        this.sourceFeacture = sourceFeacture;
    }

    public byte[] getSceneFeacture() {
        return sceneFeacture;
    }

    public void setSceneFeacture(byte[] sceneFeacture) {
        this.sceneFeacture = sceneFeacture;
    }

    public int getScro() {
        return scro;
    }

    public void setScro(int scro) {
        this.scro = scro;
    }

    public byte[] getSourceData() {
        return sourceData;
    }

    public void setSourceData(byte[] sourceData) {
        this.sourceData = sourceData;
    }
}