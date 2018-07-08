package com.tc.conf;


/**
 * Created by PB on 2017/7/20.
 */

public class Config {

    //腾讯云
    public static final String IP_ADDRESS = "http://118.24.100.105/";
    //阿里云
    // public static final String IP_ADDRESS = "http://139.224.9.85/";
    //微软云
    // public static final String IP_ADDRESS = "http://vm21819.chinacloudapp.cn/";

//    public static final String TEST_IP_ADDRESS = "http://139.224.9.85/";
//    public static final String TEST_SERVER_HOST = TEST_IP_ADDRESS + "TCAPI_test/";
    //版本更新地址
    public static final String VERSION_SERVER_HOST = "http://118.24.100.105/TCAPI/";
    //  public static final String SERVER_HOST = "http://vm21819.chinacloudapp.cn/TCAPI/";
    public static final String SERVER_HOST = IP_ADDRESS + "TCAPI/";
    //接口地址
    public static final String SERVER_API_URL = SERVER_HOST + "api/";
    //版本更新地址
    public static final String VERSION_SERVER_API_URL = VERSION_SERVER_HOST + "api/";
    //图标地址
    public static final String ICON_PATH = SERVER_HOST + "icon/";
    //头像地址
    public static final String AVATAR_PATH = SERVER_HOST + "upload/avatar/";
    //上传内容的图片地址
    public static final String PIC_PATH = SERVER_HOST + "upload/tc_content/";
    //活动图片地址
    public static final String ACTIVITY_PATH = SERVER_HOST + "upload/activity/";

}
