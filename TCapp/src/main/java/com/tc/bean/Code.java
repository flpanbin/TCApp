package com.tc.bean;

public class Code {
    /**
     * 成功
     */
    public static final int SUCCESS = 1;

    /**
     * 失败
     */
    public static final int FAIL = 0;

    /**
     * 参数错误: 一般是缺少或参数值不符合要求
     */
    public static final int PARAM_ERROR = 2;

    /**
     * 服务器错误
     */
    public static final int ERROR = 500;

    /**
     * 接口不存在
     */
    public static final int NOT_FOUND = 404;

    /**
     * token无效
     */
    public static final int TOKEN_INVALID = 422;

    /**
     * 帐号已存在*
     */
    public static final int ACCOUNT_EXISTS = 3;
    /**
     * 帐号不存在*
     */
    public static final int ACCOUNT_NOT_EXISTS = 5;

    /**
     * 验证码错误
     */
    public static final int CODE_ERROR = 4;

    /**
     * 禁言状态
     */
    public static final int PROHIBIT = 101;
    /**
     * 非法信息
     */
    public static final int ILLEGAL_CONTENT = 102;

}
