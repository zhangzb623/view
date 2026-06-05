package com.learning.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learning.common.domain.BaseEntityDO;
import lombok.Data;

/**
 * 商品分类实体类
 */
@Data
@TableName("t_category")
public class CategoryDO extends BaseEntityDO {

    /**
     * 分类ID
     */
    @TableId
    private Long categoryId;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 层级: 1-一级分类 2-二级分类 3-三级分类
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态: 0禁用 1启用
     */
    private Integer status;

    /**
     * 是否删除: 0否 1是
     */
    private Integer deleted;
}
