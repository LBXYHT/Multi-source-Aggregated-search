package com.hutao.aggregatedsearch.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信开放平台配置
 *
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "wx.open")
@Data
public class WxOpenConfig {

    private String appId;

    private String appSecret;

    private WxMpService wxMpService;

    /**
     * 单例模式（不用 @Bean 是为了防止和公众号的 service 冲突）
     *
     * @return
     */
    public WxMpService getWxMpService() {
        if (wxMpService != null) {
            return wxMpService;
        }
        // 线程安全
        synchronized (this) {
            if (wxMpService != null) {
                return wxMpService;
            }
            // 初始化配置
            // 配置对象
            WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
            // 配置 appId 和 appSecret
            config.setAppId(appId);
            config.setSecret(appSecret);
            // 创建 WxMpService实例
            WxMpService service = new WxMpServiceImpl();
            // 保存配置
            service.setWxMpConfigStorage(config);
            wxMpService = service;
            return wxMpService;
        }
    }
}