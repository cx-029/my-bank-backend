package com.mybank.backend.service;

import com.mybank.backend.entity.Notification;
import com.mybank.backend.entity.NotificationComment;

import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotifications();
    Notification getNotificationDetail(Long id);
    List<NotificationComment> getComments(Long notificationId);
    void addComment(Long notificationId, Long userId, String comment);
    int getLikeCount(Long notificationId);
    boolean likeNotification(Long notificationId, Long userId);
    boolean hasUserLiked(Long notificationId, Long userId);
    boolean unlikeNotification(Long notificationId, Long userId);
    boolean deleteComment(Long commentId, Long userId, boolean isAdmin);
    boolean likeComment(Long commentId, Long userId);
    boolean unlikeComment(Long commentId, Long userId);
    int getCommentLikeCount(Long commentId);
    boolean hasUserLikedComment(Long commentId, Long userId);
    List<NotificationComment> getChildComments(Long parentId);
    void addComment(Long notificationId, Long userId, String comment, Long parentId);
}