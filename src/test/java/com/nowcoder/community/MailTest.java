package com.nowcoder.community;

import com.nowcoder.community.utils.MailClient;
import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("306698601@qq.com","test","emailtest");
    }

    /**
     * 发送html邮件
     */
    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","sandy");

        String content = templateEngine.process("/mail/demo", context);
        mailClient.sendMail("306698601@qq.com","test",content);

    }
}
