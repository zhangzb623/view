package com.learning.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.user.entity.UserAddressDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户地址Mapper接口
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddressDO> {

    /**
     * 查询用户的地址列表
     */
    @Select("SELECT * FROM t_user_address WHERE user_id = #{userId} AND status = 1 ORDER BY is_default DESC, create_time DESC")
    List<UserAddressDO> selectByUserId(Long userId);

    /**
     * 查询用户的主要地址
     */
    @Select("SELECT * FROM t_user_address WHERE user_id = #{userId} AND is_default = 1 AND status = 1 LIMIT 1")
    UserAddressDO selectDefaultByUserId(Long userId);

    /**
     * 设置地址为默认
     */
    @Select("UPDATE t_user_address SET is_default = 0 WHERE user_id = #{userId}")
    Integer unsetDefault(Long userId);
}
