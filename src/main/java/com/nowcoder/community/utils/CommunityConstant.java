package com.nowcoder.community.utils;

/**
 * 常量接口
 */
public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 登录信息的默认超时时间，单位s
     */
    int DEFAULT_EXPIRED_SECONDS = 3600*12; //12个小时

    /**
     * 记住我后的超时时间，单位s
     */
    int REMEMBER_EXPIRED_SECONDS = 3600*24*100;  //100天

    /**
     * 实体类型:帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型:评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    int ENTITY_TYPE_USER = 3;
}


