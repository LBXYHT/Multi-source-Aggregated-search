package com.hutao.aggregatedsearch.model.dto.search;

import com.hutao.aggregatedsearch.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 类型
     */
    private String type; // 需要校验 type 是否被接受

    private static final long serialVersionUID = 1L;
}