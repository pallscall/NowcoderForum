package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 帖子相关
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;
    /**
     * 发布帖子
     * @param title
     * @param content
     * @return  返回响应json
     */
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJsonString(403,"您还没有登录!");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        return CommunityUtil.getJsonString(0,"发布成功!");
    }

    /**
     * 帖子详情
     * @param discussPostId
     * @param model
     * @return
     */
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //帖子，每次查询效率就低了，后期存入redis
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);

        //作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //点赞
        long entityLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeCount",entityLikeCount);

        //点赞状态
        int entityLikeStatus = hostHolder.getUser()==null ? 0 :
                likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeStatus",entityLikeStatus);


        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());

        // 评论：给帖子的评论
        // 回复：给评论的评论
        //根据帖子的id分页找出所有的评论
        List<Comment> comments =
                commentService.selectCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(comments != null){
            //将每条评论的评论人、评论信息插入map
            for(Comment comment: comments){
                Map<String, Object> map = new HashMap<>();
                map.put("comment",comment);
                map.put("user",userService.findUserById(comment.getUserId()));
                //点赞
                entityLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                map.put("likeCount",entityLikeCount);
                //点赞状态
                entityLikeStatus = hostHolder.getUser()==null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                map.put("likeStatus",entityLikeStatus);

                //二级评论
                List<Comment> replyList =
                        commentService.selectCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply: replyList){
                        Map<String, Object> rmap = new HashMap<>();
                        rmap.put("reply",reply);
                        rmap.put("user",userService.findUserById(reply.getUserId()));
                        //回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        rmap.put("target",target);
                        //点赞
                        entityLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        rmap.put("likeCount",entityLikeCount);
                        //点赞状态
                        entityLikeStatus = hostHolder.getUser()==null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        rmap.put("likeStatus",entityLikeStatus);
                        replyVoList.add(rmap);
                    }
                }
                map.put("replys",replyVoList);
                //回复数量
                int commentCount = commentService.selectCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                map.put("replyCount",commentCount);
                commentVoList.add(map);
            }
        }

        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";

    }


}
