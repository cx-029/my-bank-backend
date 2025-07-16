package com.mybank.backend.controller;

import com.mybank.backend.entity.Notification;
import com.mybank.backend.entity.NotificationComment;
import com.mybank.backend.entity.User;
import com.mybank.backend.service.NotificationService;
import com.mybank.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    // 获取通知列表
    @GetMapping
    public List<Notification> listNotifications() {
        return notificationService.getAllNotifications();
    }

    // 获取通知详情
    @GetMapping("/{id}")
    public Notification getNotification(@PathVariable Long id) {
        return notificationService.getNotificationDetail(id);
    }

    // 获取通知点赞数
    @GetMapping("/{id}/likes")
    public int getLikeCount(@PathVariable Long id) {
        return notificationService.getLikeCount(id);
    }

    // 通知点赞
    @PostMapping("/{id}/likes")
    public boolean likeNotification(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Long userId = user != null ? user.getId() : null;
        return notificationService.likeNotification(id, userId);
    }

    // 取消通知点赞
    @DeleteMapping("/{id}/likes")
    public boolean unlikeNotification(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Long userId = user != null ? user.getId() : null;
        return notificationService.unlikeNotification(id, userId);
    }

    // 查询用户是否点赞通知
    @GetMapping("/{id}/likes/me")
    public boolean hasUserLiked(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Long userId = user != null ? user.getId() : null;
        return notificationService.hasUserLiked(id, userId);
    }

    // ==================== 评论相关 ========================

    // 获取通知的评论（一级评论）
    @GetMapping("/{id}/comments")
    public List<NotificationComment> getComments(@PathVariable Long id) {
        return notificationService.getComments(id);
    }

    // 获取评论的子评论（回复）
    @GetMapping("/comments/{parentId}/replies")
    public List<NotificationComment> getChildComments(@PathVariable Long parentId) {
        return notificationService.getChildComments(parentId);
    }

    // 新增评论或回复（parentId可选）
    @PostMapping("/{id}/comments")
    public void addComment(@PathVariable Long id, @RequestBody Map<String, Object> body, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Long userId = user != null ? user.getId() : null;
        String comment = body.get("comment").toString();
        Long parentId = body.get("parentId") == null ? null : Long.valueOf(body.get("parentId").toString());
        notificationService.addComment(id, userId, comment, parentId);
    }

    // 删除评论（仅本人或管理员可删）
    @DeleteMapping("/comments/{commentId}")
    public boolean deleteComment(@PathVariable Long commentId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        boolean isAdmin = user != null && "admin".equalsIgnoreCase(user.getRole());
        Long userId = user != null ? user.getId() : null;
        return notificationService.deleteComment(commentId, userId, isAdmin);
    }

    // ==================== 评论点赞相关 =====================

    // 评论点赞
    @PostMapping("/comments/{commentId}/likes")
    public boolean likeComment(@PathVariable Long commentId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Long userId = user != null ? user.getId() : null;
        return notificationService.likeComment(commentId, userId);
    }

    // 取消评论点赞
    @DeleteMapping("/comments/{commentId}/likes")
    public boolean unlikeComment(@PathVariable Long commentId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Long userId = user != null ? user.getId() : null;
        return notificationService.unlikeComment(commentId, userId);
    }

    // 查询评论点赞数
    @GetMapping("/comments/{commentId}/likes")
    public int getCommentLikeCount(@PathVariable Long commentId) {
        return notificationService.getCommentLikeCount(commentId);
    }

    // 查询用户是否点赞评论
    @GetMapping("/comments/{commentId}/likes/me")
    public boolean hasUserLikedComment(@PathVariable Long commentId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Long userId = user != null ? user.getId() : null;
        return notificationService.hasUserLikedComment(commentId, userId);
    }
}