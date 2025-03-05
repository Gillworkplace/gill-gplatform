package com.gill.datasource;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties.CoreConfiguration;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

/**
 * CustomMybatisAutoConfiguration
 *
 * @author gill
 * @version 2024/02/23
 **/
@Slf4j
public class MybatisConfiguration {

    @Value("${mybatis-plus.long-sql-threshold:1000}")
    private long longSqlThreshold;

    public MybatisConfiguration(MybatisPlusProperties properties) {
        rewriteDefaultProperties(properties);
    }

    private void rewriteDefaultProperties(MybatisPlusProperties properties) {
        log.info("rewrite mybatis properties");
        properties.setMapperLocations(
            new String[]{"classpath*:mappers/*.xml", "classpath*:mapper/*.xml"});
        CoreConfiguration configuration = properties.getConfiguration();
        if (configuration != null) {
            configuration.setMapUnderscoreToCamelCase(true);
        }
    }

    @Bean
    public Interceptor sqlExecutionTimeInterceptor() {
        return new SqlExecutionTimeInterceptor(longSqlThreshold);
    }

    @Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class,
        Object.class, RowBounds.class, ResultHandler.class})})
    public static class SqlExecutionTimeInterceptor implements Interceptor {

        private final long longSqlThreshold;

        public SqlExecutionTimeInterceptor(long longSqlThreshold) {
            this.longSqlThreshold = longSqlThreshold;
        }

        private static String getParameterValue(Object obj) {
            String value;
            if (obj instanceof String) {
                value = "'" + obj + "'";
            } else if (obj instanceof Date) {
                DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
                    DateFormat.DEFAULT, Locale.CHINA);
                value = "'" + formatter.format(obj) + "'";
            } else {
                if (obj != null) {
                    value = obj.toString();
                } else {
                    value = "";
                }
            }
            return value.replace("$", "\\$");
        }

        private static int countParamSize(String sql) {
            int cnt = 0;
            for (char c : sql.toCharArray()) {
                if (c == '?') {
                    cnt++;
                }
            }
            return cnt;
        }

        private static String buildShowSql(String preparedSql, MappedStatement mappedStatement,
            BoundSql boundSql) {
            org.apache.ibatis.session.Configuration configuration = mappedStatement.getConfiguration();
            Object parameterObject = boundSql.getParameterObject();
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            if (parameterMappings.isEmpty() || parameterObject == null
                || countParamSize(preparedSql) > 10) {
                if (preparedSql.length() > 1200) {
                    return preparedSql.substring(0, 1200) + "...";
                }
                return preparedSql;
            }
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            String sql = preparedSql;
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
            return sql;
        }

        @NonNull
        private static String getSql(Invocation invocation) {
            Object handler = invocation.getTarget();

            // 以下方法参考com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor#intercept
            if (!(handler instanceof Executor)) {
                return "";
            }
            Object[] args = invocation.getArgs();
            boolean isUpdate = args.length == 2;
            MappedStatement ms = (MappedStatement) args[0];
            Object parameter = args[1];
            if (isUpdate || ms.getSqlCommandType() != SqlCommandType.SELECT) {
                return "";
            }
            BoundSql boundSql;
            if (args.length == 4) {
                boundSql = ms.getBoundSql(parameter);
            } else {
                boundSql = (BoundSql) args[5];
            }

            if (boundSql == null) {
                return "";
            }
            String preparedSql = Optional.ofNullable(boundSql.getSql())
                .map(s -> s.replaceAll("[\\s]+", " "))
                .orElse("");
            try {
                return buildShowSql(preparedSql, ms, boundSql);
            } catch (Exception ignored) {
                return preparedSql;
            }
        }

        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            long startTime = System.nanoTime();
            try {
                return invocation.proceed();
            } finally {
                long endTime = System.nanoTime();
                long executionTime = (endTime - startTime) / 1000000;
                if (executionTime > longSqlThreshold) {
                    try {
                        String sql = getSql(invocation);
                        String dbKey = "primary";
//                        String dbKey = DynamicDataSourceContextHolder.peek();
//                        if (StrUtil.isEmpty(dbKey)) {
//                            dbKey = "primary";
//                        }
                        log.warn("long sql cost: {}, db: {} sql: {}", executionTime, dbKey, sql);
                    } catch (Exception ignore) {
                    }
                }
            }
        }

        @Override
        public Object plugin(Object target) {
            return Plugin.wrap(target, this);
        }
    }
}

