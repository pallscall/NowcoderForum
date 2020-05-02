package com.nowcoder.community.utils;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    // MD5加密
    //真实密码+盐 --md5--> 加密密码
    //盐：随机字符串
    public static String md5(String key){
        if(StringUtils.isBlank(key)) return null;

        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 将浏览器返回的数据转成json字符串
     * @param code  响应码
     * @param msg   响应信息
     * @param map   业务信息
     * @return
     */
    public static String getJsonString (int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map != null){
            for(String key: map.keySet()){
                json.put(key,map.get(key));
            }
        }

        return json.toJSONString();
    }
    public static String getJsonString (int code, String msg){
        return getJsonString(code, msg, null);
    }
    public static String getJsonString (int code){
        return getJsonString(code, null, null);
    }
}
