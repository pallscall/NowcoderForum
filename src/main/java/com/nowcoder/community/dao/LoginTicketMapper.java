package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated //废弃该组件
public interface LoginTicketMapper {

    @Insert("insert into login_ticket(user_id,ticket,status,expired) " +
            "values" +
            "(#{userId},#{ticket},#{status},#{expired})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoinTicket(LoginTicket loginTicket);

    @Select("select * from login_ticket where ticket=#{ticket}")
    LoginTicket selectByTicket(String ticket);

    @Update("update login_ticket set status=#{status} where ticket=#{ticket}")
    int updateStatus(String ticket,int status);  //更改凭证状态
}
