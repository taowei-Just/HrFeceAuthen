package com.test;

/**
 * Created by Arche on 2017/4/7.
 */

public class SENSEIDProtocal {

    public static final String HEAD = "SENSEID";
    public static final byte VERSION = 0x01;
    public static final byte ENCRYPT_TYPE = 0x00;
    public static final String KEY_CMD = "cmd";
    public static final String KEY_DATA = "data";

    public static final String CODE_REQUEST_HEART_BEAT = "1000";
    public static final String CODE_RESPONSE_HEART_BEAT = "2000";
    public static final String CODE_REQUEST_RECOGNIZE = "1001";
    public static final String CODE_RESPONSE_RECOGNIZE = "2001";
    public static final String CODE_PUSH_RECOGNIZE = "2002";

    public static final String CODE_ERROR = "-1";
}
