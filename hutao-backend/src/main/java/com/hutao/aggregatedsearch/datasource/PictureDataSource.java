package com.hutao.aggregatedsearch.datasource;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hutao.aggregatedsearch.common.ErrorCode;
import com.hutao.aggregatedsearch.exception.BusinessException;
import com.hutao.aggregatedsearch.model.entity.Picture;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片服务实现类
 */
@Service
public class PictureDataSource implements DataSource<Picture> {

    @Override
    public Page<Picture> doSearch(String searchText, long pageNum, long pageSize) {
        long current = (pageNum - 1) * pageSize;
        String url = String.format("https://www.bing.com/images/search?q=%s&form=HDRSC3&first=%d&cw=1177&ch=786", searchText, current);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据获取异常");
        }
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictures = new ArrayList<>();

        for (Element element : elements) {
            if (pictures.size() == pageSize) {
                break;
            }
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
        Page<Picture> picturePage = new Page<>(pageNum, pageSize);
        picturePage.setRecords(pictures);
        return picturePage;
    }
}
