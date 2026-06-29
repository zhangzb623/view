package com.learning.message.controller;

import com.learning.common.api.result.Result;
import com.learning.common.starter.exception.BusinessException;
import com.learning.message.dto.CreateMessageRequest;
import com.learning.message.dto.MessageDTO;
import com.learning.message.dto.MessageQueryRequest;
import com.learning.message.dto.MarkAsReadRequest;
import com.learning.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息控制器
 */
@Slf4j
@Tag(name = "消息管理", description = "消息CRUD、标记已读、删除等接口")
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Operation(summary = "创建消息", description = "创建新消息")
    @PostMapping("/create")
    public Result<Long> createMessage(@Valid @RequestBody CreateMessageRequest request) {
        try {
            Long messageId = messageService.createMessage(request);
            return Result.success("消息创建成功", messageId);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("创建消息失败", e);
            return Result.fail("创建消息失败");
        }
    }

    @Operation(summary = "查询消息详情", description = "根据消息ID查询消息详情")
    @GetMapping("/{messageId}")
    public Result<MessageDTO> getMessageById(
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        try {
            MessageDTO message = messageService.getMessageById(messageId);
            return Result.success(message);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询消息详情失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "查询用户消息列表", description = "分页查询用户的消息列表")
    @GetMapping("/user/list")
    public Result<Map<String, Object>> getMessagesByUserId(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "消息类型") @RequestParam(required = false) Integer messageType,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            MessageQueryRequest request = new MessageQueryRequest();
            request.setUserId(userId);
            request.setMessageType(messageType);
            request.setPage(page);
            request.setSize(size);

            return Result.success(messageService.getMessagesByUserIdWithPage(request));
        } catch (Exception e) {
            log.error("查询消息列表失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "标记消息为已读", description = "标记指定消息为已读")
    @PostMapping("/{messageId}/read")
    public Result<Void> markAsRead(
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        try {
            MarkAsReadRequest request = new MarkAsReadRequest();
            request.setMessageId(messageId);
            messageService.markAsRead(request);
            return Result.success("标记成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("标记消息失败", e);
            return Result.fail("标记失败");
        }
    }

    @Operation(summary = "标记所有消息为已读", description = "标记用户的所有消息为已读")
    @PostMapping("/user/{userId}/read-all")
    public Result<Void> markAllAsRead(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            messageService.markAllAsRead(userId);
            return Result.success("标记成功", null);
        } catch (Exception e) {
            log.error("标记所有消息失败", e);
            return Result.fail("标记失败");
        }
    }

    @Operation(summary = "删除消息", description = "删除消息（软删除）")
    @DeleteMapping("/{messageId}")
    public Result<Void> deleteMessage(
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        try {
            messageService.deleteMessage(messageId);
            return Result.success("删除成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除消息失败", e);
            return Result.fail("删除失败");
        }
    }

    @Operation(summary = "统计未读消息", description = "统计用户的未读消息数量")
    @GetMapping("/user/{userId}/unread/count")
    public Result<Integer> countUnread(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            int count = messageService.countUnread(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计未读消息失败", e);
            return Result.fail("统计失败");
        }
    }
}
