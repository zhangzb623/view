package com.learning.product.service;

import com.learning.common.api.result.PageResult;
import com.learning.product.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 商品服务接口
 */
public interface ProductService {

    /**
     * 创建商品
     */
    Long createProduct(CreateProductRequest request);

    /**
     * 更新商品
     */
    void updateProduct(Long productId, UpdateProductRequest request);

    /**
     * 删除商品（软删除）
     */
    void deleteProduct(Long productId);

    /**
     * 根据ID获取商品信息
     */
    ProductDTO getProductById(Long productId);

    /**
     * 根据分类ID获取商品列表（分页）
     */
    PageResult<ProductDTO> getProductByCategoryId(Long categoryId, Integer current, Integer size);

    /**
     * 搜索商品（模糊匹配）
     */
    PageResult<ProductDTO> searchProducts(String keyword, Integer current, Integer size);

    /**
     * 获取所有商品（分页）
     */
    PageResult<ProductDTO> getProductList(Integer current, Integer size);

    /**
     * 获取商品详情（包含分类信息）
     */
    ProductDTO getProductDetail(Long productId);

    /**
     * 获取所有一级分类
     */
    List<CategoryDTO> getAllCategories();

    /**
     * 获取分类下的子分类
     */
    List<CategoryDTO> getChildrenCategories(Long categoryId);

    /**
     * 获取分类的完整路径
     */
    List<CategoryDTO> getCategoryPath(Long categoryId);

    /**
     * 获取在售商品数量
     */
    Integer countOnSaleProducts();

    /**
     * 获取热门商品（销量前N）
     */
    List<ProductDTO> getTopSaleProducts(Integer limit);

    /**
     * 获取最新商品
     */
    List<ProductDTO> getLatestProducts(Integer limit);

    /**
     * 下架商品
     */
    void offlineProduct(Long productId);

    /**
     * 上架商品
     */
    void onlineProduct(Long productId);

    /**
     * 检查商品是否在售
     */
    boolean isProductOnSale(Long productId);

    /**
     * 扣减库存
     */
    void deductStock(Long productId, Integer quantity);

    /**
     * 增加库存
     */
    void addStock(Long productId, Integer quantity);

    /**
     * 检查库存是否充足
     */
    boolean checkStock(Long productId, Integer quantity);

    /**
     * 批量扣减库存
     */
    void batchDeductStock(Map<Long, Integer> productIdToQuantity);

    /**
     * 获取商品数量分布
     */
    List<ProductCountDTO> getProductCountDistribution(Integer limit);
}
