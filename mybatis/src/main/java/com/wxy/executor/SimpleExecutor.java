package com.wxy.executor;

import com.wxy.domain.Configuration;
import com.wxy.domain.MappedStatement;
import com.wxy.utils.GenericTokenParser;
import com.wxy.utils.ParameterMapping;
import com.wxy.utils.ParameterMappingTokenHandler;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor{

    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object param) throws Exception{
        //1.加载驱动，获取数据库连接
        this.connection = configuration.getDataSource().getConnection();

        //2.获取preparedStatement预编译对象
        /**
         * 获取要执行的sql语句
         * select * from user where id = #{id} and username = #{username}
         * 需要替换成：
         * select * from user where id = ? and username = ?
         * 解析替换的过程中，需要将#{id}里面的值保存下来
         */
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);
        String finalSql = boundSql.getFinalSql();
        preparedStatement = this.connection.prepareStatement(finalSql);
        //3、设置参数
        // 入参类型路径 com.wxy.domain.User
        String parameterType = mappedStatement.getParameterType();
        if (parameterType != null){
            Class<?> parameterTypeClass = Class.forName(parameterType);
            List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
            for (int i = 0; i < parameterMappingList.size(); i++) {
                // id || username
                String paramName = parameterMappingList.get(i).getContent();
                // 反射获取值
                Field declaredField = parameterTypeClass.getDeclaredField(paramName);
                declaredField.setAccessible(true);
                Object value = declaredField.get(param);
                //给占位符赋值
                preparedStatement.setObject(i+1, value);
            }
        }

        //4.执行sql， 发起查询
        resultSet = preparedStatement.executeQuery();
        //5.处理返回结果集
        // 获取返回结果类型路径 com.wxy.domain.User
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = Class.forName(resultType);
        List<E> resultList = new ArrayList<>();
        while (resultSet.next()){
            //元数据信息 包含了 字段名以及字段值
            ResultSetMetaData metaData = resultSet.getMetaData();
            Object o = resultTypeClass.newInstance();
            for (int i = 1; i <= metaData.getColumnCount() ; i++) {
                //字段名
                String columnName = metaData.getColumnName(i);
                //字段值
                Object value = resultSet.getObject(columnName);
                //封装
                //属性描述器，来获取resultTypeClass类中columnName元素的get/set方法
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                //参数1：实例对象，参数2：要设置的值
                writeMethod.invoke(o, value);
            }
            resultList.add((E) o);
        }
        return resultList;
    }

    /**
     * 完成两件事
     * 1.#{}占位符替换成?
     * 2.解析过程中 将#{}中的值保存下来
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //1.创建标记处理器：配合标记处理器完成标记的处理解析工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        //2.创建标记解析器
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //#{}占位符替换成?  解析过程中 将#{}中的值保存下来(ParameterMapping)
        String finalSql = genericTokenParser.parse(sql);
        //#{}里面值的集合
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();
        BoundSql boundSql = new BoundSql(finalSql, parameterMappings);
        return boundSql;
    }

    @Override
    public void close() {
        //释放资源
        if (resultSet !=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (preparedStatement != null){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
