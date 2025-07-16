package com.hutao.aggregatedsearch.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hutao.aggregatedsearch.model.dto.post.PostQueryRequest;
import com.hutao.aggregatedsearch.model.entity.Post;
import com.hutao.aggregatedsearch.model.vo.PostVO;
import com.hutao.aggregatedsearch.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子服务实现
 *
 */
@Service
@Slf4j
public class PostDataSource implements DataSource<PostVO> {

    @Resource
    private PostService postService;

    @Override
    public Page<PostVO> doSearch(String searchText, long pageNum, long pageSize) {

        // 使用 elasticsearch 和 ik 中文分词器
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setCurrent((int) pageNum);
        postQueryRequest.setPageSize((int) pageSize);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        return postService.getPostVOPage(postPage, request);


        /*
        // 使用 mysql 模糊查询，关闭 es
        // 模糊查询不支持分词
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setCurrent((int) pageNum);
        postQueryRequest.setPageSize((int) pageSize);
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
        return postVOPage;
         */
    }

}




