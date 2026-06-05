package com.learning.user.controller;

import com.learning.common.api.result.PageResult;
import com.learning.common.api.result.Result;
import com.learning.common.starter.exception.BusinessException;
import com.learning.user.dto.*;
import com.learning.user.entity.UserAddressDO;
import com.learning.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 用户控制器
 */
@Slf4j
@Tag(name = "用户管理", description = "用户注册、登录、信息查询接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册", description = "注册新用户")
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody CreateUserRequest request) {
        try {
            Long userId = userService.register(request);
            return Result.success("注册成功", userId);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return Result.fail("注册失败");
        }
    }

    @Operation(summary = "用户登录", description = "用户登录获取Token")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return Result.success("登录成功", response);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return Result.fail("登录失败");
        }
    }

    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    @GetMapping("/{userId}")
    public Result<UserInfoVO> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            UserInfoVO user = userService.getUserById(userId);
            return Result.success(user);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.fail("获取用户信息失败");
        }
    }

    @Operation(summary = "更新用户信息", description = "更新用户基本信息")
    @PutMapping("/{userId}")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            userService.updateUser(userId, request);
            return Result.success("更新成功");
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.fail("更新用户信息失败");
        }
    }

    @Operation(summary = "获取用户列表", description = "分页获取用户列表")
    @GetMapping("/list")
    public Result<PageResult<UserInfoVO>> getUserList(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<UserInfoVO> pageResult = userService.getUserList(current, size);
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.fail("获取用户列表失败");
        }
    }

    @Operation(summary = "删除用户", description = "删除用户（软删除）")
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return Result.success("删除成功");
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.fail("删除用户失败");
        }
    }

    @Operation(summary = "添加地址", description = "为用户添加收货地址")
    @PostMapping("/address/add")
    public Result<Long> addUserAddress(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Valid @RequestBody UserAddressDO address) {
        try {
            Long addressId = userService.addUserAddress(userId, address);
            return Result.success("添加成功", addressId);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("添加地址失败", e);
            return Result.fail("添加地址失败");
        }
    }

    @Operation(summary = "获取地址列表", description = "获取用户的收货地址列表")
    @GetMapping("/address/list")
    public Result<PageResult<UserAddressDO>> getUserAddressList(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<UserAddressDO> pageResult = userService.getUserAddressList(userId, current, size);
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("获取地址列表失败", e);
            return Result.fail("获取地址列表失败");
        }
    }

    @Operation(summary = "更新地址", description = "更新收货地址")
    @PutMapping("/address/{addressId}")
    public Result<Void> updateUserAddress(
            @Parameter(description = "地址ID") @PathVariable Long addressId,
            @Valid @RequestBody UserAddressDO address) {
        try {
            userService.updateUserAddress(addressId, address);
            return Result.success("更新成功");
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新地址失败", e);
            return Result.fail("更新地址失败");
        }
    }

    @Operation(summary = "删除地址", description = "删除收货地址")
    @DeleteMapping("/address/{addressId}")
    public Result<Void> deleteUserAddress(
            @Parameter(description = "地址ID") @PathVariable Long addressId) {
        try {
            userService.deleteUserAddress(addressId);
            return Result.success("删除成功");
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除地址失败", e);
            return Result.fail("删除地址失败");
        }
    }

    @Operation(summary = "设置默认地址", description = "设置用户的默认收货地址")
    @PostMapping("/address/default/{addressId}")
    public Result<Void> setDefaultAddress(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "地址ID") @PathVariable Long addressId) {
        try {
            userService.setDefaultAddress(userId, addressId);
            return Result.success("设置成功");
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("设置默认地址失败", e);
            return Result.fail("设置默认地址失败");
        }
    }

    @Operation(summary = "扣减余额", description = "扣减用户余额")
    @PostMapping("/balance/deduct")
    public Result<Void> deductBalance(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "扣减金额") @RequestParam BigDecimal amount) {
        try {
            userService.deductBalance(userId, amount);
            return Result.success("扣减成功");
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("扣减余额失败", e);
            return Result.fail("扣减余额失败");
        }
    }

    @Operation(summary = "增加余额", description = "增加用户余额")
    @PostMapping("/balance/add")
    public Result<Void> addBalance(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "增加金额") @RequestParam BigDecimal amount) {
        try {
            userService.addBalance(userId, amount);
            return Result.success("增加成功");
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("增加余额失败", e);
            return Result.fail("增加余额失败");
        }
    }

    @Operation(summary = "查询余额", description = "查询用户余额")
    @GetMapping("/balance")
    public Result<BigDecimal> getBalance(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            BigDecimal balance = userService.getBalance(userId);
            return Result.success(balance);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询余额失败", e);
            return Result.fail("查询余额失败");
        }
    }
}
