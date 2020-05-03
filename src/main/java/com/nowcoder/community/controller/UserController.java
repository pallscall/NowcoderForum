package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;
    /**
     * 跳转设置界面
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    /**
     * 上传头像
     * @param headImg
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headImg, Model model){
        if(headImg == null){
            model.addAttribute("error","您还没有选择图片!");
            return "/site/setting";
        }

        //原始文件名
        String originalFilename = headImg.getOriginalFilename();
        //文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确!");
            return "/site/setting";
        }

        //生成随机文件名，防止文件名冲突
        String filename = CommunityUtil.generateUUID() + suffix;
        //确定文件存放路径
        File dest = new File(uploadPath + "/" + filename);
        try {
            //存储文件
            headImg.transferTo(dest);
        } catch (IOException e) {
            LOGGER.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！",e);
        }

        //更新当前用户的头像的路径（web访问路径）
        //http:localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain+contextPath+"/user/header/"+filename;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}",method = RequestMethod.GET)
    public void getHeadImg(@PathVariable("filename") String filename, HttpServletResponse response){
        // 获取服务器存放路径
        filename = uploadPath + "/" + filename;
        //文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try (FileInputStream fileInputStream = new FileInputStream(filename);
             ServletOutputStream os = response.getOutputStream();
        ){
            byte[] buffer = new byte[1024];  //建一个缓冲区
            int b = 0;
            while((b=fileInputStream.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            LOGGER.error("读取头像失败："+e.getMessage());
        }

    }

    @RequestMapping(path = "/modifyPwd",method = RequestMethod.POST)
    public String modifyPwd(String oldPassword,String newPassword,String checkPassword, Model model){

        User user = hostHolder.getUser();
        String old = CommunityUtil.md5(oldPassword + user.getSalt());
        if(!old.equals(user.getPassword())){
            model.addAttribute("pwdError1","原密码错误！");
            return "/site/setting";
        }
        if(!newPassword.equals(checkPassword)){
            model.addAttribute("pwdError2","两次输入的密码不一致！");
            return "/site/setting";
        }

        String New = CommunityUtil.md5(newPassword+user.getSalt());
        userService.updatePassword(user.getId(), New);
        return "redirect:/index";
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){

        User user = userService.findUserById(userId);
        if(user == null){
            throw new IllegalArgumentException("该用户不存在！");
        }

        //用户
        model.addAttribute("user",user);
        int userLikeCount = likeService.findUserLikeCount(userId);
        //点赞数量
        model.addAttribute("likeCount",userLikeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followerCount", followerCount);
        //是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }


}
