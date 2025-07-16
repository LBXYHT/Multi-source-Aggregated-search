package com.hutao.aggregatedsearch.job.cycle;

import com.hutao.aggregatedsearch.esdao.PostEsDao;
import com.hutao.aggregatedsearch.mapper.PostMapper;
import com.hutao.aggregatedsearch.model.dto.post.PostEsDTO;
import com.hutao.aggregatedsearch.model.entity.Post;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.collection.CollUtil;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 增量同步帖子到 es
 *
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class IncSyncPostToEs {

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostEsDao postEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        // 设置查询时间周期
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - 5 * 60 * 1000L);
        // 从 postMapper 取数据（updateTime 发生改变的数据）
        List<Post> postList = postMapper.listPostWithDelete(fiveMinutesAgoDate);
        // 没有更新
        if (CollUtil.isEmpty(postList)) {
            log.info("no inc post");
            return;
        }
        // 将 List<Post> 对象静态转换为 List<PostEsDTO> 对象
        List<PostEsDTO> postEsDTOList = postList.stream()
                .map(PostEsDTO::objToDto)
                .collect(Collectors.toList());
        // 设置每批写入 ES 的条数为500
        // 获取总条数 total
        final int pageSize = 500;
        int total = postEsDTOList.size();
        // 记录日志，输出总条数
        log.info("IncSyncPostToEs start, total {}", total);
        // 逐条数据
        for (int i = 0; i < total; i += pageSize) {
            // 定位末端，防止越界
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            // 将当前批次数据写入 ES
            postEsDao.saveAll(postEsDTOList.subList(i, end));
        }
        log.info("IncSyncPostToEs end, total {}", total);
    }
}
