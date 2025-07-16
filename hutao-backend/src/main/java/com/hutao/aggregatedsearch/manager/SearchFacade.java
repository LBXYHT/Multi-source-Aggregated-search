package com.hutao.aggregatedsearch.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hutao.aggregatedsearch.common.ErrorCode;
import com.hutao.aggregatedsearch.datasource.*;
import com.hutao.aggregatedsearch.exception.BusinessException;
import com.hutao.aggregatedsearch.exception.ThrowUtils;
import com.hutao.aggregatedsearch.model.dto.post.PostQueryRequest;
import com.hutao.aggregatedsearch.model.dto.search.SearchRequest;
import com.hutao.aggregatedsearch.model.dto.user.UserQueryRequest;
import com.hutao.aggregatedsearch.model.entity.Picture;
import com.hutao.aggregatedsearch.model.enums.SearchTypeEnum;
import com.hutao.aggregatedsearch.model.vo.PostVO;
import com.hutao.aggregatedsearch.model.vo.SearchVO;
import com.hutao.aggregatedsearch.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * 搜索门面
 * 
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    public SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        // 参数：搜索请求，抽象类请求
        // 获取请求对象类型
        String type = searchRequest.getType();
        // 文件上传业务类型枚举
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        // 获取搜索词
        String searchText = searchRequest.getSearchText();
        // 获取 pageNum 和 pageSize
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();
        // 搜索出所有数据
        if (searchTypeEnum == null) {
            // 并发对象
            CompletableFuture<Page<UserVO>> userTask =  CompletableFuture.supplyAsync(() -> {
                // 创建用户查询请求对象
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                // 设置搜索词为用户名
                userQueryRequest.setUserName(searchText);
                // 分页查询用户
                Page<UserVO> userVOPage = userDataSource.doSearch(searchText, current, pageSize);
                return userVOPage;
            });

            CompletableFuture<Page<PostVO>> postTask =  CompletableFuture.supplyAsync(() -> {
                // 创建帖子查询请求对象
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                // 设置搜索词为搜索词
                postQueryRequest.setSearchText(searchText);
                // 分页查询帖子
                Page<PostVO> postVOPage = postDataSource.doSearch(searchText, current, pageSize);
                return postVOPage;
            });

            CompletableFuture<Page<Picture>> pictureTask =  CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureDataSource.doSearch(searchText, 1, 10);
                return picturePage;
            });

            // 以上三个对象是异步的，下面用 allOf 函数组合起来
            // join 相当于断点，只有当三个对象都完成之后才会执行后续代码，实现多线程
            CompletableFuture.allOf(userTask, postTask, pictureTask).join();
            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();

                SearchVO searchVO = new SearchVO();
                searchVO.setPictureList(picturePage.getRecords());
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());

                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {

            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());

            return searchVO;
        }
    }

}
