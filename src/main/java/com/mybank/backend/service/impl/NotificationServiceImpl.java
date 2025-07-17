package com.mybank.backend.service.impl;

import com.mybank.backend.entity.CommentLike;
import com.mybank.backend.repository.CommentLikeRepository;
import com.mybank.backend.entity.Notification;
import com.mybank.backend.entity.NotificationComment;
import com.mybank.backend.entity.NotificationLike;
import com.mybank.backend.repository.NotificationCommentRepository;
import com.mybank.backend.repository.NotificationLikeRepository;
import com.mybank.backend.repository.NotificationRepository;
import com.mybank.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationCommentRepository commentRepository;
    @Autowired
    private NotificationLikeRepository likeRepository;
    @Autowired
    private CommentLikeRepository commentLikeRepository;

    // ====== 通知相关 ======
    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Notification getNotificationDetail(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    @Override
    public int getLikeCount(Long notificationId) {
        return likeRepository.countByNotificationId(notificationId);
    }

    @Override
    public boolean likeNotification(Long notificationId, Long userId) {
        if (likeRepository.existsByNotificationIdAndUserId(notificationId, userId)) {
            return false; // 已点赞
        }
        NotificationLike nl = new NotificationLike();
        nl.setNotificationId(notificationId);
        nl.setUserId(userId);
        nl.setCreatedAt(new Date());
        likeRepository.save(nl);
        return true;
    }

    @Override
    public boolean hasUserLiked(Long notificationId, Long userId) {
        return likeRepository.existsByNotificationIdAndUserId(notificationId, userId);
    }

    @Override
    public boolean unlikeNotification(Long notificationId, Long userId) {
        NotificationLike like = likeRepository.findByNotificationIdAndUserId(notificationId, userId);
        if (like != null) {
            likeRepository.delete(like);
            return true;
        }
        return false;
    }

    // ====== 评论相关 ======

    // 获取所有一级评论（parentId为null且未删除）
    @Override
    public List<NotificationComment> getComments(Long notificationId) {
        return commentRepository.findByNotificationIdAndParentIdAndDeleted(notificationId, null, 0);
    }

    // 获取某个评论的子评论（未删除）
    @Override
    public List<NotificationComment> getChildComments(Long parentId) {
        return commentRepository.findByParentIdAndDeleted(parentId, 0);
    }

    // 新增评论/回复
    @Override
    public void addComment(Long notificationId, Long userId, String comment, Long parentId) {
        NotificationComment nc = new NotificationComment();
        nc.setNotificationId(notificationId);
        nc.setUserId(userId);
        nc.setComment(comment);
        nc.setCreatedAt(new Date());
        nc.setDeleted(0);
        nc.setParentId(parentId);
        commentRepository.save(nc);
    }

    // 删除评论（仅本人或管理员可删）
    @Override
    public boolean deleteComment(Long commentId, Long userId, boolean isAdmin) {
        NotificationComment c = commentRepository.findById(commentId).orElse(null);
        if (c == null) return false;
        if (isAdmin || (c.getUserId() != null && c.getUserId().equals(userId))) {
            c.setDeleted(1);
            commentRepository.save(c);
            return true;
        }
        return false;
    }

    // ====== 评论点赞相关 ======
    @Override
    public boolean likeComment(Long commentId, Long userId) {
        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            return false;
        }
        CommentLike cl = new CommentLike();
        cl.setCommentId(commentId);
        cl.setUserId(userId);
        cl.setCreatedAt(new Date());
        commentLikeRepository.save(cl);
        return true;
    }

    @Override
    public boolean unlikeComment(Long commentId, Long userId) {
        CommentLike cl = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
        if (cl != null) {
            commentLikeRepository.delete(cl);
            return true;
        }
        return false;
    }

    @Override
    public int getCommentLikeCount(Long commentId) {
        return commentLikeRepository.countByCommentId(commentId);
    }

    @Override
    public boolean hasUserLikedComment(Long commentId, Long userId) {
        return commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    // ========== 兼容旧接口 ==========
    // 若有老版本addComment方法（无parentId），保持兼容
    @Override
    public void addComment(Long notificationId, Long userId, String comment) {
        addComment(notificationId, userId, comment, null);
    }
    @Override
    public Page<Notification> getNotificationsPaged(Pageable pageable) {
        return notificationRepository.findAll(pageable);
    }

    @Override
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Notification updateNotification(Notification notification) {
        Notification old = notificationRepository.findById(notification.getId()).orElse(null);
        if (old == null) return null;
        old.setTitle(notification.getTitle());
        old.setContent(notification.getContent());
        old.setImageUrl(notification.getImageUrl());
        // 可加其他字段更新
        return notificationRepository.save(old);
    }

    @Override
    @Transactional
    public boolean deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) return false;
        // 先删除评论
        commentRepository.deleteByNotificationId(id);
        // 先删除点赞
        likeRepository.deleteByNotificationId(id);
        // 最后删除通知
        notificationRepository.deleteById(id);
        return true;
    }
}