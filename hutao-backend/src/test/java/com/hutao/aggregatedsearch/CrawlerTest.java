package com.hutao.aggregatedsearch;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hutao.aggregatedsearch.model.entity.Picture;
import com.hutao.aggregatedsearch.model.entity.Post;
import com.hutao.aggregatedsearch.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CrawlerTest {
    @Resource
    PostService postService;

    @Test
    void testFetchPicture() throws IOException {
        int current = 1;
        String url = String.format("https://www.bing.com/images/search?q=胡桃&form=HDRSC3&first=%d&cw=1177&ch=786", current);
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictures = new ArrayList<>();
        for (Element element : elements) {
            // 取图片地址（murl）
            String m = element.select(".iusc").get(0).attr("m");
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
            // System.out.println(murl);
            // 取标题
            String title = element.select(".inflnk").get(0).attr("aria-label");
            // System.out.println(title);

            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setUrl(murl);
            pictures.add(picture);
        }
        System.out.println(pictures);
    }

    @Test
    void testFetchPassage() {
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

    }
}
