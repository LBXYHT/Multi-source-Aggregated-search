package com.hutao.aggregatedsearch.controller;

import com.hutao.aggregatedsearch.common.BaseResponse;
import com.hutao.aggregatedsearch.common.ResultUtils;
import com.hutao.aggregatedsearch.manager.SearchFacade;
import com.hutao.aggregatedsearch.model.dto.search.SearchRequest;
import com.hutao.aggregatedsearch.model.vo.SearchVO;
import com.hutao.aggregatedsearch.service.PictureService;
import com.hutao.aggregatedsearch.service.PostService;
import com.hutao.aggregatedsearch.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 搜索接口
 *
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));
    }
}
