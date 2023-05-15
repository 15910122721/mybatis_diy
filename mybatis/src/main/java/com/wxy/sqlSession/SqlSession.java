package com.wxy.sqlSession;

import java.util.List;

public interface SqlSession {

    /**
     * 查询多个结果
     * sqlSession.selectList(); 定位到要执行的sql语句，从而执行
     * select * from user where username like '% ? %'
     * @param statementId
     * @param param
     * @param <E>
     * @return
     */
    <E> List<E> selectList(String statementId, Object param);

    /**
     * 查询单个结果
     * @param statementId
     * @param param
     * @param <T>
     * @return
     */
    <T> T selectOne(String statementId, Object param);

    /**
     * 清楚资源
     */
    void close();

    /**
     * 生成代理对象
     * @param mapperClass
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<?> mapperClass);
}
