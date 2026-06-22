package com.learning.product.controller;

import com.learning.common.api.result.PageResult;
import com.learning.common.api.result.Result;
import com.learning.common.starter.exception.BusinessException;
import com.learning.product.dto.*;
import com.learning.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 */
@Slf4j
@Tag(name = "商品管理", description = "商品CRUD、搜索、库存管理接口")
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "创建商品", description = "创建新商品")
    @PostMapping("/create")
    public Result<Long> createProduct(@Valid @RequestBody CreateProductRequest request) {
        try {
            Long productId = productService.createProduct(request);
            return Result.success("创建成功", productId);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("创建商品失败", e);
            return Result.fail("创建失败");
        }
    }

    @Operation(summary = "更新商品", description = "更新商品信息")
    @PutMapping("/{productId}")
    public Result<Void> updateProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {
        try {
            productService.updateProduct(productId, request);
            return Result.success("更新成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新商品失败", e);
            return Result.fail("更新失败");
        }
    }

    @Operation(summary = "删除商品", description = "删除商品（软删除）")
    @DeleteMapping("/{productId}")
    public Result<Void> deleteProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return Result.success("删除成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除商品失败", e);
            return Result.fail("删除失败");
        }
    }

    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品详情")
    @GetMapping("/{productId}")
    public Result<ProductDTO> getProductById(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        try {
            ProductDTO product = productService.getProductById(productId);
            return Result.success(product);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("获取商品详情失败", e);
            return Result.fail("获取失败");
        }
    }

    @Operation(summary = "获取商品列表（按分类）", description = "根据分类ID获取商品列表（分页）")
    @GetMapping("/category/{categoryId}")
    public Result<PageResult<ProductDTO>> getProductByCategoryId(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<ProductDTO> pageResult = productService.getProductByCategoryId(categoryId, current, size);
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            return Result.fail("获取失败");
        }
    }

    @Operation(summary = "搜索商品", description = "使用Elasticsearch搜索商品")
    @GetMapping("/search")
    public Result<PageResult<ProductDTO>> searchProducts(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<ProductDTO> pageResult = productService.searchProducts(keyword, current, size);
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("搜索商品失败", e);
            return Result.fail("搜索失败");
        }
    }

    @Operation(summary = "获取商品列表", description = "获取所有商品（分页）")
    @GetMapping("/list")
    public Result<PageResult<ProductDTO>> getProductList(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<ProductDTO> pageResult = productService.getProductList(current, size);
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            return Result.fail("获取失败");
        }
    }

    @Operation(summary = "下架商品", description = "下架商品")
    @PostMapping("/{productId}/offline")
    public Result<Void> offlineProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        try {
            productService.offlineProduct(productId);
            return Result.success("下架成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("下架商品失败", e);
            return Result.fail("下架失败");
        }
    }

    @Operation(summary = "上架商品", description = "上架商品")
    @PostMapping("/{productId}/online")
    public Result<Void> onlineProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        try {
            productService.onlineProduct(productId);
            return Result.success("上架成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("上架商品失败", e);
            return Result.fail("上架失败");
        }
    }

    @Operation(summary = "检查商品是否在售", description = "检查商品是否在售")
    @GetMapping("/{productId}/on-sale")
    public Result<Boolean> isProductOnSale(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        try {
            boolean isOnSale = productService.isProductOnSale(productId);
            return Result.success(isOnSale);
        } catch (Exception e) {
            log.error("检查商品状态失败", e);
            return Result.fail("检查失败");
        }
    }

    @Operation(summary = "扣减库存", description = "扣减商品库存")
    @PostMapping("/{productId}/stock/deduct")
    public Result<Void> deductStock(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "扣减数量") @RequestParam Integer quantity) {
        try {
            productService.deductStock(productId, quantity);
            return Result.success("扣减成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("扣减库存失败", e);
            return Result.fail("扣减失败");
        }
    }

    @Operation(summary = "增加库存", description = "增加商品库存")
    @PostMapping("/{productId}/stock/add")
    public Result<Void> addStock(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "增加数量") @RequestParam Integer quantity) {
        try {
            productService.addStock(productId, quantity);
            return Result.success("增加成功", null);
        } catch (Exception e) {
            log.error("增加库存失败", e);
            return Result.fail("增加失败");
        }
    }

    @Operation(summary = "检查库存", description = "检查商品库存是否充足")
    @GetMapping("/{productId}/stock/check")
    public Result<Boolean> checkStock(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "检查数量") @RequestParam Integer quantity) {
        try {
            boolean hasStock = productService.checkStock(productId, quantity);
            return Result.success(hasStock);
        } catch (Exception e) {
            log.error("检查库存失败", e);
            return Result.fail("检查失败");
        }
    }

    @Operation(summary = "批量扣减库存", description = "批量扣减商品库存")
    @PostMapping("/batch/stock/deduct")
    public Result<Void> batchDeductStock(
            @RequestBody Map<Long, Integer> productIdToQuantity) {
        try {
            productService.batchDeductStock(productIdToQuantity);
            return Result.success("扣减成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("批量扣减库存失败", e);
            return Result.fail("扣减失败");
        }
    }

    @Operation(summary = "获取所有分类", description = "获取所有商品分类（树形结构）")
    @GetMapping("/categories")
    public Result<List<CategoryDTO>> getAllCategories() {
        try {
            List<CategoryDTO> categories = productService.getAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return Result.fail("获取失败");
        }
    }

    @Operation(summary = "获取分类下的子分类", description = "获取指定分类的子分类")
    @GetMapping("/categories/{categoryId}/children")
    public Result<List<CategoryDTO>> getChildrenCategories(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        try {
            List<CategoryDTO> categories = productService.getChildrenCategories(categoryId);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取子分类失败", e);
            return Result.fail("获取失败");
        }
    }

    @Operation(summary = "获取分类完整路径", description = "获取分类从根节点到当前节点的完整路径")
    @GetMapping("/categories/{categoryId}/path")
    public Result<List<CategoryDTO>> getCategoryPath(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        try {
            List<CategoryDTO> path = productService.getCategoryPath(categoryId);
            return Result.success(path);
        } catch (Exception e) {
            log.error("获取分类路径失败", e);
            return Result.fail("获取失败");
        }
    }

    @Operation(summary = "获取在售商品数量", description = "获取所有在售商品的数量")
    @GetMapping("/count/on-sale")
    public Result<Integer> countOnSaleProducts() {
        try {
            Integer count = productService.countOnSaleProducts();
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取在售商品数量失败", e);
            return Result.fail("获取失败");
        }
    }

    @Operation(summary = "获取热门商品", description = "获取销量前N的热门商品")
    @GetMapping("/top-sales")
    public Result<List<ProductDTO>> getTopSaleProducts(
            @Parameter(description = "数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ProductDTO> products = productService.getTopSaleProducts(limit);
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取热门商品失败", e);
            return Result.fail("获取失败");
        }
    }

    @Operation(summary = "获取最新商品", description = "获取最新上架的商品")
    @GetMapping("/latest")
    public Result<List<ProductDTO>> getLatestProducts(
            @Parameter(description = "数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ProductDTO> products = productService.getLatestProducts(limit);
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取最新商品失败", e);
            return Result.fail("获取失败");
        }
    }

    @Operation(summary = "获取商品数量分布", description = "获取各分类的商品数量分布")
    @GetMapping("/count/distribution")
    public Result<List<ProductCountDTO>> getProductCountDistribution(
            @Parameter(description = "数量") @RequestParam(defaultValue = "20") Integer limit) {
        try {
            List<ProductCountDTO> distribution = productService.getProductCountDistribution(limit);
            return Result.success(distribution);
        } catch (Exception e) {
            log.error("获取商品数量分布失败", e);
            return Result.fail("获取失败");
        }
    }
}
