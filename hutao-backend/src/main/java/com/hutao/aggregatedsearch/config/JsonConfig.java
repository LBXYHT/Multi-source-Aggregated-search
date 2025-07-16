package com.hutao.aggregatedsearch.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Spring MVC Json 配置
 *
 */
@JsonComponent
public class JsonConfig {

    /**
     * 添加 Long 转 json 精度丢失的配置
     */
    // long 类型的精度超过了前端 javascript 的最大安全整数值（2^53-1）会发生精度丢失
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // 创建 objectMapper 对象
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        // 创建简单模块 simple module
        SimpleModule module = new SimpleModule();
        // 将 long 类型和原生类型都使用 ToStringSerializer 序列化器
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        // 将模块注册到 objectMapper
        objectMapper.registerModule(module);
        return objectMapper;
    }
}