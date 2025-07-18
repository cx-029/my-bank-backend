package com.mybank.backend.controller;

import com.mybank.backend.entity.NotificationComment;
import com.mybank.backend.repository.NotificationCommentRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 管理员通知评论管理控制器
 */
@RestController
@RequestMapping("/api/admin/notification-comments")
public class AdminNotificationCommentController {

    @Autowired
    private NotificationCommentRepository commentRepository;

    /**
     * 分页、条件查询所有评论
     * 可按通知ID、用户ID、内容模糊匹配、删除状态筛选
     */
    @GetMapping
    public Page<NotificationComment> listComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long notificationId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Integer deleted
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentRepository.findAll((root, query, cb) -> {
            Predicate p = cb.conjunction();
            if (notificationId != null) {
                p = cb.and(p, cb.equal(root.get("notificationId"), notificationId));
            }
            if (userId != null) {
                p = cb.and(p, cb.equal(root.get("userId"), userId));
            }
            if (content != null && !content.isEmpty()) {
                p = cb.and(p, cb.like(root.get("comment"), "%" + content + "%"));
            }
            if (deleted != null) {
                p = cb.and(p, cb.equal(root.get("deleted"), deleted));
            }
            return p;
        }, pageable);
    }

    /**
     * 管理员逻辑删除评论
     */
    @DeleteMapping("/{id}")
    public boolean deleteComment(@PathVariable Long id) {
        Optional<NotificationComment> commentOpt = commentRepository.findById(id);
        if (commentOpt.isPresent()) {
            NotificationComment comment = commentOpt.get();
            comment.setDeleted(1);
            commentRepository.save(comment);
            return true;
        }
        return false;
    }

    /**
     * 管理员恢复评论（如需要，可选）
     */
    @PutMapping("/{id}/restore")
    public boolean restoreComment(@PathVariable Long id) {
        Optional<NotificationComment> commentOpt = commentRepository.findById(id);
        if (commentOpt.isPresent()) {
            NotificationComment comment = commentOpt.get();
            comment.setDeleted(0);
            commentRepository.save(comment);
            return true;
        }
        return false;
    }

    /**
     * 查询单条评论详情
     */
    @GetMapping("/{id}")
    public NotificationComment getComment(@PathVariable Long id) {
        return commentRepository.findById(id).orElse(null);
    }
}