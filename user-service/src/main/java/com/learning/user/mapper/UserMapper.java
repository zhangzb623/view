package com.learning.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.user.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM t_user_info WHERE username = #{username} AND deleted = 0")
    UserDO selectByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM t_user_info WHERE phone = #{phone} AND deleted = 0")
    UserDO selectByPhone(String phone);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM t_user_info WHERE email = #{email} AND deleted = 0")
    UserDO selectByEmail(String email);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM t_user_info WHERE username = #{username} AND deleted = 0")
    Integer countByUsername(String username);

    /**
     * 检查手机号是否存在
     */
    @Select("SELECT COUNT(*) FROM t_user_info WHERE phone = #{phone} AND deleted = 0")
    Integer countByPhone(String phone);

    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) FROM t_user_info WHERE email = #{email} AND deleted = 0")
    Integer countByEmail(String email);

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM t_user_info WHERE deleted = 0 ORDER BY create_time DESC")
    List<UserDO> selectAll();
}
