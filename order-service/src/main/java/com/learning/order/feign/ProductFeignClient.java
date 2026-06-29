package com.learning.order.feign;

import com.learning.common.api.result.Result;
import com.learning.order.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 商品服务Feign客户端
 */
@FeignClient(
    name = "product-service",
    contextId = "productFeignClient"
)
public interface ProductFeignClient {

    /**
     * 根据商品ID获取商品信息
     */
    @GetMapping("/api/product/{productId}")
    Result<OrderDTO> getProductById(
        @PathVariable("productId") Long productId,
        @RequestHeader("Authorization") String token
    );

    /**
     * 扣减商品库存
     */
    @GetMapping("/api/product/{productId}/stock/deduct")
    Result<Void> deductStock(
        @PathVariable("productId") Long productId,
        @RequestParam("quantity") Integer quantity,
        @RequestHeader("Authorization") String token
    );

    /**
     * 增加商品库存
     */
    @GetMapping("/api/product/{productId}/stock/add")
    Result<Void> addStock(
        @PathVariable("productId") Long productId,
        @RequestParam("quantity") Integer quantity,
        @RequestHeader("Authorization") String token
    );

    /**
     * 检查商品是否在售
     */
    @GetMapping("/api/product/{productId}/on-sale")
    Result<Boolean> isProductOnSale(
        @PathVariable("productId") Long productId,
        @RequestHeader("Authorization") String token
    );
}
