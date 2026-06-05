package com.learning.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.common.api.result.PageResult;
import com.learning.common.starter.exception.BusinessException;
import com.learning.common.starter.utils.CacheHelper;
import com.learning.common.starter.utils.LockHelper;
import com.learning.product.dto.*;
import com.learning.product.entity.CategoryDO;
import com.learning.product.entity.ProductDO;
import com.learning.product.mapper.CategoryMapper;
import com.learning.product.mapper.ProductMapper;
import com.learning.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CacheHelper cacheHelper;

    @Autowired
    private LockHelper lockHelper;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Override
    @Transactional
    public Long createProduct(CreateProductRequest request) {
        ProductDO product = new ProductDO();
        BeanUtils.copyProperties(request, product);
        product.setStatus(1);
        product.setSalesCount(0);
        product.setDeleted(0);

        // 检查商品名称是否已存在
        // 注意：这里简化处理，实际应检查当前用户名下的商品
        LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductDO::getProductName, request.getProductName())
               .eq(ProductDO::getDeleted, 0);
        if (productMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("商品名称已存在");
        }

        productMapper.insert(product);

        // 清除缓存
        cacheHelper.delete("product:" + product.getProductId());
        clearCategoryCache();

        log.info("商品创建成功: productId={}, productName={}", product.getProductId(), product.getProductName());
        return product.getProductId();
    }

    @Override
    @Transactional
    public void updateProduct(Long productId, UpdateProductRequest request) {
        ProductDO product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 更新商品信息
        if (request.getCategoryId() != null) {
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getProductName() != null && !request.getProductName().isEmpty()) {
            // 检查商品名称是否与其他商品重复
            LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductDO::getProductName, request.getProductName())
                   .ne(ProductDO::getProductId, productId)
                   .eq(ProductDO::getDeleted, 0);
            if (productMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("商品名称已存在");
            }
            product.setProductName(request.getProductName());
        }
        if (request.getProductDesc() != null) {
            product.setProductDesc(request.getProductDesc());
        }
        if (request.getProductImage() != null) {
            product.setProductImage(request.getProductImage());
        }
        if (request.getUnitPrice() != null) {
            product.setUnitPrice(request.getUnitPrice());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        productMapper.updateById(product);

        // 清除缓存
        cacheHelper.delete("product:" + productId);
        clearCategoryCache();

        // 更新Elasticsearch
        updateElasticsearch(product);

        log.info("商品更新成功: productId={}", productId);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        ProductDO product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setDeleted(1);
        productMapper.updateById(product);

        // 清除缓存
        cacheHelper.delete("product:" + productId);
        clearCategoryCache();

        log.info("商品删除成功: productId={}", productId);
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        // 先从缓存获取
        ProductDTO dto = cacheHelper.get("product:" + productId, ProductDTO.class);
        if (dto != null) {
            return dto;
        }

        ProductDO product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        dto = convertToProductDTO(product);
        cacheHelper.set("product:" + productId, dto, 3600L, TimeUnit.SECONDS);

        return dto;
    }

    @Override
    public PageResult<ProductDTO> getProductByCategoryId(Long categoryId, Integer current, Integer size) {
        // 使用MyBatis Plus分页
        PageResult<ProductDO> pageResult = productMapper.selectPage(current, size,
                "SELECT * FROM t_product WHERE category_id = " + categoryId + " AND status = 1 AND deleted = 0 ORDER BY sales_count DESC, create_time DESC");

        PageResult<ProductDTO> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setRecords(pageResult.getRecords().stream()
                .map(this::convertToProductDTO)
                .toList());

        return result;
    }

    @Override
    public PageResult<ProductDTO> searchProducts(String keyword, Integer current, Integer size) {
        // 使用Elasticsearch搜索
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "productName", "productDesc"))
                .withFilter(QueryBuilders.termQuery("status", 1))
                .withFilter(QueryBuilders.termQuery("deleted", 0))
                .withPageable(PageRequest.of(current - 1, size))
                .build();

        SearchHits<ProductDO> searchHits = elasticsearchTemplate.search(query, ProductDO.class);

        PageResult<ProductDTO> result = new PageResult<>();
        result.setCurrent(current);
        result.setSize(size);
        result.setTotal(searchHits.getTotalHits());
        result.setRecords(searchHits.get().map(SearchHit::getContent)
                .map(this::convertToProductDTO)
                .collect(Collectors.toList()));

        // 更新缓存
        searchHits.forEach(hit -> {
            ProductDO product = hit.getContent();
            ProductDTO dto = convertToProductDTO(product);
            cacheHelper.set("product:" + product.getProductId(), dto, 3600L, TimeUnit.SECONDS);
        });

        return result;
    }

    @Override
    public PageResult<ProductDTO> getProductList(Integer current, Integer size) {
        LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductDO::getDeleted, 0)
               .orderByDesc(ProductDO::getCreateTime);

        PageResult<ProductDO> pageResult = productMapper.selectPage(current, size, wrapper);

        PageResult<ProductDTO> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setRecords(pageResult.getRecords().stream()
                .map(this::convertToProductDTO)
                .toList());

        return result;
    }

    @Override
    public ProductDTO getProductDetail(Long productId) {
        return getProductById(productId);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryMapper.selectAllCategories().stream()
                .map(this::convertToCategoryDTO)
                .toList();
    }

    @Override
    public List<CategoryDTO> getChildrenCategories(Long categoryId) {
        return categoryMapper.selectChildrenCategories(categoryId).stream()
                .map(this::convertToCategoryDTO)
                .toList();
    }

    @Override
    public List<CategoryDTO> getCategoryPath(Long categoryId) {
        return categoryMapper.selectCategoryPath(categoryId).stream()
                .map(this::convertToCategoryDTO)
                .toList();
    }

    @Override
    public Integer countOnSaleProducts() {
        return productMapper.countOnSale();
    }

    @Override
    public List<ProductDTO> getTopSaleProducts(Integer limit) {
        return productMapper.selectTopSales(limit).stream()
                .map(this::convertToProductDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> getLatestProducts(Integer limit) {
        LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductDO::getStatus, 1)
               .eq(ProductDO::getDeleted, 0)
               .orderByDesc(ProductDO::getCreateTime)
               .last("LIMIT " + limit);

        return productMapper.selectList(wrapper).stream()
                .map(this::convertToProductDTO)
                .toList();
    }

    @Override
    @Transactional
    public void offlineProduct(Long productId) {
        ProductDO product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStatus(0);
        productMapper.updateById(product);

        // 清除缓存
        cacheHelper.delete("product:" + productId);
        clearCategoryCache();
        updateElasticsearch(product);

        log.info("商品下架成功: productId={}", productId);
    }

    @Override
    @Transactional
    public void onlineProduct(Long productId) {
        ProductDO product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStatus(1);
        productMapper.updateById(product);

        // 清除缓存
        cacheHelper.delete("product:" + productId);
        clearCategoryCache();
        updateElasticsearch(product);

        log.info("商品上架成功: productId={}", productId);
    }

    @Override
    public boolean isProductOnSale(Long productId) {
        return productMapper.isOnSale(productId) > 0;
    }

    @Override
    @Transactional
    public void deductStock(Long productId, Integer quantity) {
        // 使用分布式锁防止并发扣减
        lockHelper.executeWithLock("product:stock:" + productId, () -> {
            Integer stock = productMapper.getStock(productId);
            if (stock == null || stock < quantity) {
                throw new BusinessException("商品库存不足");
            }

            productMapper.updateById(productId, stock - quantity);

            // 更新缓存
            ProductDTO dto = getProductById(productId);
            cacheHelper.set("product:" + productId, dto, 3600L, TimeUnit.SECONDS);

            log.info("商品库存扣减成功: productId={}, quantity={}", productId, quantity);
        });
    }

    @Override
    @Transactional
    public void addStock(Long productId, Integer quantity) {
        ProductDO product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStock(product.getStock() + quantity);
        productMapper.updateById(product);

        // 更新缓存
        ProductDTO dto = convertToProductDTO(product);
        cacheHelper.set("product:" + productId, dto, 3600L, TimeUnit.SECONDS);

        log.info("商品库存增加成功: productId={}, quantity={}", productId, quantity);
    }

    @Override
    public boolean checkStock(Long productId, Integer quantity) {
        Integer stock = productMapper.getStock(productId);
        if (stock == null) {
            return false;
        }
        return stock >= quantity;
    }

    @Override
    @Transactional
    public void batchDeductStock(Map<Long, Integer> productIdToQuantity) {
        lockHelper.executeWithLock("product:batch:stock", () -> {
            for (Map.Entry<Long, Integer> entry : productIdToQuantity.entrySet()) {
                Long productId = entry.getKey();
                Integer quantity = entry.getValue();

                Integer stock = productMapper.getStock(productId);
                if (stock == null || stock < quantity) {
                    throw new BusinessException("商品[" + productId + "]库存不足");
                }

                productMapper.updateById(productId, stock - quantity);

                // 更新缓存
                ProductDTO dto = getProductById(productId);
                cacheHelper.set("product:" + productId, dto, 3600L, TimeUnit.SECONDS);
            }

            log.info("批量扣减库存成功: {}", productIdToQuantity);
        });
    }

    @Override
    public List<ProductCountDTO> getProductCountDistribution(Integer limit) {
        return categoryMapper.countProductsByCategory(limit);
    }

    /**
     * 更新Elasticsearch文档
     */
    private void updateElasticsearch(ProductDO product) {
        try {
            ProductDO productDO = productMapper.selectById(product.getProductId());
            if (productDO != null) {
                elasticsearchTemplate.save(productDO);
            }
        } catch (Exception e) {
            log.error("更新Elasticsearch失败", e);
        }
    }

    /**
     * 清除分类缓存
     */
    private void clearCategoryCache() {
        cacheHelper.delete("categories:*");
    }

    /**
     * 转换为商品DTO
     */
    private ProductDTO convertToProductDTO(ProductDO product) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);

        // 查询分类名称
        if (product.getCategoryId() != null) {
            CategoryDO category = categoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getCategoryName());
            }
        }

        return dto;
    }

    /**
     * 转换为分类DTO
     */
    private CategoryDTO convertToCategoryDTO(CategoryDO category) {
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}
