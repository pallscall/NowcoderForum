package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer(){
        Properties properties = new Properties();
        //验证码
        properties.setProperty("kaptcha.image.width","100"); //长度
        properties.setProperty("kaptcha.image.height","40"); //高度
        properties.setProperty("kaptcha.textproducer.font.size","32"); //字体大小
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0"); //颜色
        properties.setProperty("kaptcha.textproducer.char.string","0123456789qwertyuioplkjhgfdszxcvbnm"); //随机字
        properties.setProperty("kaptcha.textproducer.char.length","4");  //长度
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");  //干扰

        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;

    }
}
