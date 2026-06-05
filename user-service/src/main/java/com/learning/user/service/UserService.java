package com.learning.user.service;

import com.learning.common.api.result.PageResult;
import com.learning.user.dto.*;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    Long register(CreateUserRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 根据ID获取用户信息
     */
    UserInfoVO getUserById(Long userId);

    /**
     * 更新用户信息
     */
    void updateUser(Long userId, UpdateUserRequest request);

    /**
     * 获取用户列表 (分页)
     */
    PageResult<UserInfoVO> getUserList(Integer current, Integer size);

    /**
     * 删除用户
     */
    void deleteUser(Long userId);

    /**
     * 查询用户的地址列表
     */
    PageResult<UserAddressDO> getUserAddressList(Long userId, Integer current, Integer size);

    /**
     * 添加地址
     */
    Long addUserAddress(Long userId, UserAddressDO address);

    /**
     * 更新地址
     */
    void updateUserAddress(Long addressId, UserAddressDO address);

    /**
     * 删除地址
     */
    void deleteUserAddress(Long addressId);

    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long userId, Long addressId);

    /**
     * 扣减余额
     */
    void deductBalance(Long userId, BigDecimal amount);

    /**
     * 增加余额
     */
    void addBalance(Long userId, BigDecimal amount);

    /**
     * 查询用户余额
     */
    BigDecimal getBalance(Long userId);
}
