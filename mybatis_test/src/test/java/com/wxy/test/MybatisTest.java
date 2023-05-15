package com.wxy.test;

import com.wxy.domain.User;
import com.wxy.io.Resources;
import com.wxy.mapper.UserMapper;
import com.wxy.sqlSession.SqlSession;
import com.wxy.sqlSession.SqlSessionFactory;
import com.wxy.sqlSession.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class MybatisTest {

    @Test
    public void test1() throws DocumentException {
        //1.根据配置文件的路径，加载成字节输入流，存到内存中：注意配置文件还未解析
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

        //2.解析配置文件，封装了Configuration并且创建了sqlSessionFactory工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        //3.生产sqlSession 创建了执行器
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //4.调用sqlSession方法
        User user = new User();
        user.setId(1);
        user.setUsername("张飞");
        User bean = sqlSession.selectOne("user.selectOne", user);
        System.out.println(bean.toString());

        //5.释放资源
        sqlSession.close();
    }

    @Test
    public void test2() throws DocumentException {
        //1.根据配置文件的路径，加载成字节输入流，存到内存中：注意配置文件还未解析
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

        //2.解析配置文件，封装了Configuration并且创建了sqlSessionFactory工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        //3.生产sqlSession 创建了执行器
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //4.调用sqlSession方法
        List<User> beanList = sqlSession.selectList("user.selectList", null);
        for (User user : beanList) {
            System.out.println(user.toString());
        }

        //5.释放资源
        sqlSession.close();
    }

    @Test
    public void test3() throws DocumentException {
        //1.根据配置文件的路径，加载成字节输入流，存到内存中：注意配置文件还未解析
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

        //2.解析配置文件，封装了Configuration并且创建了sqlSessionFactory工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        //3.生产sqlSession 创建了执行器
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> users = userMapper.selectList();
        for (User user : users) {
            System.out.println(user.toString());
        }

        //5.释放资源
        sqlSession.close();
    }

    @Test
    public void test4() throws DocumentException {
        //1.根据配置文件的路径，加载成字节输入流，存到内存中：注意配置文件还未解析
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

        //2.解析配置文件，封装了Configuration并且创建了sqlSessionFactory工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        //3.生产sqlSession 创建了执行器
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = new User();
        user.setId(2);
        user.setUsername("孙尚香");
        User user1 = userMapper.selectOne(user);
        System.out.println(user1);

        //5.释放资源
        sqlSession.close();
    }
}
