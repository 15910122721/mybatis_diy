package com.wxy.sqlSession;

import com.wxy.domain.Configuration;
import com.wxy.domain.MappedStatement;
import com.wxy.executor.Executor;

import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;
    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }


    @Override
    public <E> List<E> selectList(String statementId, Object param) {
        //将查询操作委派为底层的执行器
        //query():执行底层的JDBC 1.数据库配置信息 2.sql配置信息
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<E> list = null;
        try {
            list = executor.query(configuration, mappedStatement, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public <T> T selectOne(String statementId, Object param) {
        //去调用selectList()；
        List<Object> list = this.selectList(statementId, param);
        if (list.size() == 1){
            return (T) list.get(0);
        }else if (list.size() > 1){
            throw new RuntimeException("返回结果过多");
        }else {
            return null;
        }
    }

    @Override
    public void close() {
        executor.close();
    }
}
