package com.learning.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.product.entity.ProductDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<ProductDO> {

    /**
     * 按分类查询商品
     */
    @Select("SELECT * FROM t_product WHERE category_id = #{categoryId} AND deleted = 0 ORDER BY sales_count DESC, create_time DESC")
    List<ProductDO> selectByCategoryId(Long categoryId);

    /**
     * 按名称模糊查询商品
     */
    @Select("SELECT * FROM t_product WHERE product_name LIKE CONCAT('%', #{keyword}, '%') AND deleted = 0 LIMIT #{limit}")
    List<ProductDO> selectByKeyword(String keyword, Integer limit);

    /**
     * 统计分类下的商品数量
     */
    @Select("SELECT COUNT(*) FROM t_product WHERE category_id = #{categoryId} AND deleted = 0")
    Integer countByCategoryId(Long categoryId);

    /**
     * 统计在售商品数量
     */
    @Select("SELECT COUNT(*) FROM t_product WHERE status = 1 AND deleted = 0")
    Integer countOnSale();

    /**
     * 查询销量前N的商品
     */
    @Select("SELECT * FROM t_product WHERE status = 1 AND deleted = 0 ORDER BY sales_count DESC LIMIT #{limit}")
    List<ProductDO> selectTopSales(Integer limit);

    /**
     * 查询所有上架商品
     */
    @Select("SELECT * FROM t_product WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<ProductDO> selectAllOnSale();

    /**
     * 检查商品是否在售
     */
    @Select("SELECT COUNT(*) FROM t_product WHERE product_id = #{productId} AND status = 1 AND deleted = 0")
    Integer isOnSale(Long productId);

    /**
     * 检查商品库存是否充足
     */
    @Select("SELECT stock FROM t_product WHERE product_id = #{productId} AND deleted = 0")
    Integer getStock(Long productId);

    /**
     * 更新商品销量
     */
    @Select("UPDATE t_product SET sales_count = sales_count + #{count} WHERE product_id = #{productId}")
    Integer incrementSalesCount(Long productId, Integer count);
}
