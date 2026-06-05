package com.learning.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.common.api.result.PageResult;
import com.learning.common.starter.exception.BusinessException;
import com.learning.common.starter.utils.CacheHelper;
import com.learning.common.starter.utils.LockHelper;
import com.learning.user.dto.*;
import com.learning.user.entity.UserAddressDO;
import com.learning.user.entity.UserDO;
import com.learning.user.mapper.UserAddressMapper;
import com.learning.user.mapper.UserMapper;
import com.learning.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CacheHelper cacheHelper;

    @Autowired
    private LockHelper lockHelper;

    @Value("${jwt.expiration:7200000}")
    private Long jwtExpiration;

    @Value("${jwt.secret:learning-system-secret-key-2024}")
    private String jwtSecret;

    @Override
    @Transactional
    public Long register(CreateUserRequest request) {
        // 检查用户名是否已存在
        if (userMapper.countByUsername(request.getUsername()) > 0) {
            throw new BusinessException(10002, "用户名已存在");
        }

        // 检查手机号是否已存在
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            if (userMapper.countByPhone(request.getPhone()) > 0) {
                throw new BusinessException(10002, "手机号已被注册");
            }
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (userMapper.countByEmail(request.getEmail()) > 0) {
                throw new BusinessException(10002, "邮箱已被注册");
            }
        }

        UserDO user = new UserDO();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setBalance(BigDecimal.ZERO);
        user.setStatus(1);
        user.setDeleted(0);

        userMapper.insert(user);

        log.info("用户注册成功: userId={}, username={}", user.getUserId(), user.getUsername());
        return user.getUserId();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        UserDO user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(10003, "用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(10003, "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(10004, "账号已被锁定");
        }

        // 生成Token
        String token = generateToken(user.getUserId(), user.getUsername());

        // 缓存用户信息
        cacheHelper.set("user:" + user.getUserId(), user, 1800L, TimeUnit.SECONDS);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        log.info("用户登录成功: userId={}, username={}", user.getUserId(), user.getUsername());
        return response;
    }

    @Override
    public UserInfoVO getUserById(Long userId) {
        // 先从缓存获取
        UserInfoVO vo = cacheHelper.get("user:" + userId, UserInfoVO.class);
        if (vo != null) {
            return vo;
        }

        // 从数据库获取
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(10001, "用户不存在");
        }

        // 转换为VO
        vo = convertToUserInfoVO(user);
        cacheHelper.set("user:" + userId, vo, 1800L, TimeUnit.SECONDS);

        return vo;
    }

    @Override
    public void updateUser(Long userId, UpdateUserRequest request) {
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(10001, "用户不存在");
        }

        // 更新邮箱
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (userMapper.countByEmail(request.getEmail()) > 0) {
                throw new BusinessException(10002, "邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }

        // 更新头像
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        // 更新手机号
        if (request.getPhone() != null) {
            if (userMapper.countByPhone(request.getPhone()) > 0) {
                throw new BusinessException(10002, "手机号已被使用");
            }
            user.setPhone(request.getPhone());
        }

        userMapper.updateById(user);

        // 更新缓存
        cacheHelper.delete("user:" + userId);

        log.info("用户信息更新成功: userId={}", userId);
    }

    @Override
    public PageResult<UserInfoVO> getUserList(Integer current, Integer size) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getDeleted, 0)
               .orderByDesc(UserDO::getCreateTime);

        PageResult<UserDO> pageResult = userMapper.selectPage(current, size, wrapper);

        PageResult<UserInfoVO> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setRecords(pageResult.getRecords().stream()
                .map(this::convertToUserInfoVO)
                .toList());

        return result;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(10001, "用户不存在");
        }

        user.setDeleted(1);
        userMapper.updateById(user);

        // 清除缓存
        cacheHelper.delete("user:" + userId);

        log.info("用户删除成功: userId={}", userId);
    }

    @Override
    public PageResult<UserAddressDO> getUserAddressList(Long userId, Integer current, Integer size) {
        PageResult<UserAddressDO> pageResult = userAddressMapper.selectPage(current, size,
                "SELECT * FROM t_user_address WHERE user_id = " + userId + " AND status = 1 ORDER BY is_default DESC, create_time DESC");

        return pageResult;
    }

    @Override
    @Transactional
    public Long addUserAddress(Long userId, UserAddressDO address) {
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(10001, "用户不存在");
        }

        address.setUserId(userId);
        address.setDeleted(0);
        address.setStatus(1);

        // 如果是第一个地址，设置为默认地址
        if (userAddressMapper.selectByUserId(userId).isEmpty()) {
            address.setIsDefault(1);
        } else {
            address.setIsDefault(0);
        }

        userAddressMapper.insert(address);
        log.info("用户地址添加成功: userId={}, addressId={}", userId, address.getAddressId());
        return address.getAddressId();
    }

    @Override
    @Transactional
    public void updateUserAddress(Long addressId, UserAddressDO address) {
        UserAddressDO addressDO = userAddressMapper.selectById(addressId);
        if (addressDO == null) {
            throw new BusinessException(10001, "地址不存在");
        }

        BeanUtils.copyProperties(address, addressDO, "addressId", "userId", "createTime");
        userAddressMapper.updateById(addressDO);

        log.info("用户地址更新成功: addressId={}", addressId);
    }

    @Override
    @Transactional
    public void deleteUserAddress(Long addressId) {
        UserAddressDO addressDO = userAddressMapper.selectById(addressId);
        if (addressDO == null) {
            throw new BusinessException(10001, "地址不存在");
        }

        addressDO.setStatus(0);
        userAddressMapper.updateById(addressDO);

        log.info("用户地址删除成功: addressId={}", addressId);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        // 先取消所有默认地址
        userAddressMapper.unsetDefault(userId);

        // 设置新默认地址
        UserAddressDO addressDO = userAddressMapper.selectById(addressId);
        if (addressDO == null) {
            throw new BusinessException(10001, "地址不存在");
        }

        addressDO.setIsDefault(1);
        userAddressMapper.updateById(addressDO);

        log.info("默认地址设置成功: userId={}, addressId={}", userId, addressId);
    }

    @Override
    public void deductBalance(Long userId, BigDecimal amount) {
        // 使用分布式锁防止并发扣款
        lockHelper.executeWithLock("user:balance:" + userId, () -> {
            UserDO user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(10001, "用户不存在");
            }

            if (user.getBalance().compareTo(amount) < 0) {
                throw new BusinessException(40001, "余额不足");
            }

            user.setBalance(user.getBalance().subtract(amount));
            userMapper.updateById(user);

            // 更新缓存
            cacheHelper.set("user:" + userId, convertToUserInfoVO(user), 1800L, TimeUnit.SECONDS);

            log.info("用户余额扣减成功: userId={}, amount={}", userId, amount);
        });
    }

    @Override
    public void addBalance(Long userId, BigDecimal amount) {
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(10001, "用户不存在");
        }

        user.setBalance(user.getBalance().add(amount));
        userMapper.updateById(user);

        // 更新缓存
        cacheHelper.set("user:" + userId, convertToUserInfoVO(user), 1800L, TimeUnit.SECONDS);

        log.info("用户余额增加成功: userId={}, amount={}", userId, amount);
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        // 先从缓存获取
        UserInfoVO vo = cacheHelper.get("user:" + userId, UserInfoVO.class);
        if (vo != null) {
            return vo.getBalance();
        }

        // 从数据库获取
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(10001, "用户不存在");
        }

        return user.getBalance();
    }

    /**
     * 生成Token
     */
    private String generateToken(Long userId, String username) {
        long now = System.currentTimeMillis();
        long exp = now + jwtExpiration;

        // 简单的JWT实现（生产环境建议使用JwtUtils类）
        String header = "Bearer ";
        String payload = userId + ":" + username + ":" + now + ":" + exp;
        String signature = encode(payload + jwtSecret);

        return header + payload + "." + signature;
    }

    /**
     * 简单的JWT编码（示例实现）
     */
    private String encode(String data) {
        // 在实际项目中，应该使用JWT库进行编码
        // 这里使用简单的Base64编码作为示例
        return java.util.Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(data.getBytes());
    }

    /**
     * 转换为用户信息VO
     */
    private UserInfoVO convertToUserInfoVO(UserDO user) {
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}
