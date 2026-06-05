package com.learning.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.product.entity.CategoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 商品分类Mapper接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<CategoryDO> {

    /**
     * 查询所有一级分类
     */
    @Select("SELECT * FROM t_category WHERE level = 1 AND status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<CategoryDO> selectFirstLevelCategories();

    /**
     * 查询分类下的子分类
     */
    @Select("SELECT * FROM t_category WHERE parent_id = #{parentId} AND level = 2 AND status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<CategoryDO> selectSecondLevelCategories(Long parentId);

    /**
     * 查询分类的子分类（所有层级）
     */
    @Select("SELECT * FROM t_category WHERE parent_id = #{parentId} AND status = 1 AND deleted = 0 ORDER BY level, sort_order ASC")
    List<CategoryDO> selectChildrenCategories(Long parentId);

    /**
     * 查询所有分类（树形结构）
     */
    @Select("SELECT * FROM t_category WHERE deleted = 0 ORDER BY level, sort_order ASC")
    List<CategoryDO> selectAllCategories();

    /**
     * 统计分类下的商品数量
     */
    @Select("SELECT COUNT(*) FROM t_product WHERE category_id = #{categoryId} AND deleted = 0")
    Integer countByCategoryId(Long categoryId);

    /**
     * 查询分类的商品数量分布
     */
    @Select("SELECT category_id, COUNT(*) as count FROM t_product WHERE deleted = 0 GROUP BY category_id ORDER BY count DESC LIMIT #{limit}")
    List<Map<String, Object>> countProductsByCategory(Integer limit);

    /**
     * 查询分类的完整路径（从根节点到当前节点）
     */
    @Select("WITH RECURSIVE category_tree AS (" +
            "  SELECT * FROM t_category WHERE category_id = #{categoryId} AND deleted = 0" +
            "  UNION ALL" +
            "  SELECT c.* FROM t_category c" +
            "  INNER JOIN category_tree ct ON c.category_id = ct.parent_id" +
            "  WHERE c.deleted = 0" +
            ") SELECT * FROM category_tree ORDER BY level ASC")
    List<CategoryDO> selectCategoryPath(Long categoryId);

    /**
     * 检查分类是否存在
     */
    @Select("SELECT COUNT(*) FROM t_category WHERE category_id = #{categoryId} AND deleted = 0")
    Integer countById(Long categoryId);

    /**
     * 检查分类名称是否已存在
     */
    @Select("SELECT COUNT(*) FROM t_category WHERE category_name = #{categoryName} AND parent_id = #{parentId} AND deleted = 0")
    Integer countByName(Long parentId, String categoryName);
}
