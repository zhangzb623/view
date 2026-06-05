package com.learning.common.api.result;

import com.learning.common.domain.BaseEntityVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 *
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> extends BaseEntityVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 构造方法
     */
    public PageResult() {
    }

    /**
     * 构造方法
     */
    public PageResult(List<T> records, Long total) {
        this.records = records;
        this.total = total;
    }

    /**
     * 创建空分页结果
     */
    public static <T> PageResult<T> empty() {
        PageResult<T> result = new PageResult<>();
        result.setRecords(List.of());
        result.setTotal(0L);
        result.setPages(0L);
        result.setCurrent(1L);
        result.setSize(10L);
        return result;
    }

    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long size) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setCurrent(current);
        result.setSize(size);
        result.setPages((total + size - 1) / size);
        return result;
    }

    /**
     * 设置总页数
     */
    public void setTotal(Long total) {
        this.total = total;
        this.pages = (total + getSize() - 1) / getSize();
    }
}
