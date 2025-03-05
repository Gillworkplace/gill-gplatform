package com.gill.generator;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.gill.generator.config.GeneratorConfig;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * GeneratorApplication
 *
 * @author gill
 */
@SpringBootApplication
@Slf4j
public class GeneratorApplication implements ApplicationRunner {

    @Autowired
    private GeneratorConfig config;

    public static void main(String[] args) {
        SpringApplication.run(GeneratorApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        DataSourceConfig.Builder dsConfig = new DataSourceConfig.Builder(config.getUrl(),
            config.getUsername(), config.getPassword());

        FastAutoGenerator.create(dsConfig)
            .globalConfig((scanner, builder) -> builder.author("zhangzhiyan")
                .outputDir(System.getProperty("user.dir") + StrUtil.format("/{}/src/main/java",
                    config.getModule()))
                .commentDate("yyyy-MM-dd")
                .dateType(DateType.TIME_PACK))
            .packageConfig((builder) -> builder.parent(config.getBasePackage())
                .entity("entity")
                .service("service.mapperservice")
                .serviceImpl("service.mapperservice.impl")
                .mapper("mapper")
                .xml("mapper.xml")
                .pathInfo(Collections.singletonMap(OutputFile.xml,
                    System.getProperty("user.dir") + StrUtil.format(
                        "/{}/src/main/resources/mapper", config.getModule()))))
            .injectionConfig((builder) -> builder.beforeOutputFile(
                (a, b) -> log.info("tableInfo: " + a.getEntityName())))
            .strategyConfig((scanner, builder) -> builder.addInclude()
                .addTablePrefix("t_")

                // controller
                .controllerBuilder()
                .disable()

                // mapper
                .mapperBuilder()
                .enableFileOverride()
                .formatXmlFileName("%sMapper")
                .mapperAnnotation(org.apache.ibatis.annotations.Mapper.class)

                // service
                .serviceBuilder()
                .enableFileOverride()
                .formatServiceFileName("%sService")
                .formatServiceImplFileName("%sServiceImpl")

                // entity
                .entityBuilder()
                .enableFileOverride()
                .disableSerialVersionUID()
                .enableFileOverride()
                .enableLombok()
                .enableFileOverride()
                .enableTableFieldAnnotation()
                .idType(IdType.AUTO)
                .formatFileName("%sEntity")
                .build())
            .execute();
    }
}
