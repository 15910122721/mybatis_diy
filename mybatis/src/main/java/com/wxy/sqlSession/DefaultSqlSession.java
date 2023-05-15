package com.wxy.sqlSession;

import com.wxy.domain.Configuration;
import com.wxy.domain.MappedStatement;
import com.wxy.executor.Executor;

import java.lang.reflect.*;
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
        if (list.size() == 1) {
            return (T) list.get(0);
        } else if (list.size() > 1) {
            throw new RuntimeException("返回结果过多");
        } else {
            return null;
        }
    }

    @Override
    public void close() {
        executor.close();
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        //使用JDK动态代理生成基于接口的代理对象
        Object proxy = Proxy.newProxyInstance(
                DefaultSqlSession.class.getClassLoader(),
                new Class[]{mapperClass},
                new InvocationHandler() {
                    /**
                     * @param proxy 代理对象的引用，很少用
                     * @param method 被调用的方法的字节码对象
                     * @param args 调用的方法的参数
                     * @return
                     * @throws Throwable
                     */
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //具体逻辑：执行底层的JDBC
                        //通过调用sqlSession里面的方法来完成方法调用
                        //参数的准备：1.statementId  2.param
                        String methodName = method.getName();
                        String className = method.getDeclaringClass().getName();
                        String statementId = className + "." + methodName;

                        //方法调用,通过sqlCommandType来判断应该调用哪个方法
                        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
                        String sqlCommandType = mappedStatement.getSqlCommandType();
                        switch (sqlCommandType) {
                            case "select":
                                //执行查询方法的调用
                                //需要判断调用selectList还是selectOne
                                Type genericReturnType = method.getGenericReturnType();
                                if (genericReturnType instanceof ParameterizedType) {
                                    //判断是否实现了 泛型类型参数化
                                    if (args != null) {
                                        return selectList(statementId, args[0]);
                                    }
                                    return selectList(statementId, null);
                                }
                                return selectOne(statementId, args[0]);
                            case "update":
                                //执行更新方法的调用
                                break;
                            case "delete":
                                //执行delete方法的调用
                                break;
                            case "insert":
                                //执行insert方法的调用
                                break;
                        }
                        return null;
                    }
                }
        );
        return (T) proxy;
    }
}
