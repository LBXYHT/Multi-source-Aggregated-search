package com.hutao.aggregatedsearch.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hutao.aggregatedsearch.model.entity.Post;
import com.hutao.aggregatedsearch.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取初始帖子列表
 *
 */
// 取消注释后，每次启动 springboot 项目时会执行一次 run 方法
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Override
    public void run(String... args) {
        // 1、获取数据
        String json = "{\"pageSize\":12,\"sortOrder\":\"descend\",\"sortField\":\"createTime\",\"tags\":[],\"current\":1,\"reviewStatus\":1,\"category\":\"文章\",\"hiddenContent\":true}";
        String url = "https://api.codefather.cn/api/post/list/page/vo";
        String result = HttpRequest
                .post(url)
                .body(json)
                .execute().body();
        // System.out.println(result);
        // 2、json转对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");

        List<Post> postList = new ArrayList<>();
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            // 判断 title 不为空，写入 title ，否则写入“”
            if (tempRecord.getStr("title") != null) {
                post.setTitle(tempRecord.getStr("title"));
            } else {
                post.setTitle("");
            }
            // 判断 content 不为空，写入 content ，否则写入“”
            if (tempRecord.getStr("content") != null) {
                post.setContent(tempRecord.getStr("content"));
            } else {
                post.setContent("");
            }
            // 判断 tags 不为空，进行 tags 从 JSONArray 到 List 的类型转换然后写入 tags ，否则写入“”
            if (tempRecord.get("tags") != null) {
                JSONArray tags = (JSONArray) tempRecord.get("tags");
                List<String> tagList = tags.toList(String.class);
                post.setTags(JSONUtil.toJsonStr(tagList));
            } else {
                post.setTags("");
            }
            // 写入用户id
            post.setUserId(1L);
            // 将post添加到postList
            postList.add(post);
        }
        // System.out.println(postList);

        // 3.数据入库
        boolean b = postService.saveBatch(postList);
        if (b) {
            log.info("获取初始化帖子列表成功，条数 = {}", postList.size());
        } else {
            log.error("获取初始化列表失败");
        }
    }
}
