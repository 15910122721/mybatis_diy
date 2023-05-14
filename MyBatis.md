## 一.jdbc问题分析：

![image-20230513174718271](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230513174718271.png)

1.数据库配置信息硬编码

2.频繁创建释放数据库连接

### 解决思路：

1.配置文件

2.连接池

## 二.思路分析

1.加载配置文件

```
创建Resource类：负责加载配置i文件，加载成字节流，存储到内存中
方法：InputStream getResourceAsStream(String path);
```

2.创建两个JavaBean

```
Configuration:全局配置类:存储sqlMapConfig.xml配置文件解析出来的内容
MapperStatement：映射配置类：存储mapper.xml配置文件解析出来的内容
```

3.解析配置文件，填充容器对象

```
创建SqlSessionFactoryBuilder类
	方法：SqlSessionFactory build（InputStream）；
		1.解析配置文件（dom4j + xpath），封装Configure
		2.创建SqlSessionFactory对象
```

4.创建SqlSessionFactory接口及DefaultSqlSessionFactory

```
方法：SqlSession openSession();
```

5.创建SqlSession接口及DefaultSqlSession实现类

```
方法：
	selectOne();
	selectList();
	update();
	delete();
```

6.创建Executor接口和实现类SimpleExecutor

```
方法：query(Configuration, MapperStatement, Object param); 执行的就是底层的JDBC代码（数据库配置信息、sql配置信息）
```

