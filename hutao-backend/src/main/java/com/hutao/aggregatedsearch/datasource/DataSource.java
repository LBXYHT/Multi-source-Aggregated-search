package com.hutao.aggregatedsearch.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface DataSource<T> {

    /**
     * 数据源接口（新接入的数据源必须实现）
     * @param searchText
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);

}
