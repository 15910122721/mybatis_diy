package com.wxy.config;

import com.wxy.domain.Configuration;
import com.wxy.domain.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration){
        this.configuration = configuration;
    }


    public void parse(InputStream inputStream) throws DocumentException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();

        /**
         * <select id="selectOne" resultType="com.wxy.domain.User" parameterType="com.wxy.domain.User">
         *         select * from user where id = #{id} and username = #{username}
         *     </select>
         */
        String namespace = rootElement.attributeValue("namespace");
        List<Element> selectList = rootElement.selectNodes("//select");
        for (Element element : selectList) {
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String parameterType = element.attributeValue("parameterType");
            String sql = element.getTextTrim();

            //封装mappedStatement对象
            MappedStatement mappedStatement = new MappedStatement();
            //StatementId: namespace.id
            String statementId = namespace + "." + id;
            mappedStatement.setStatementId(statementId);
            mappedStatement.setParameterType(parameterType);
            mappedStatement.setResultType(resultType);
            mappedStatement.setSql(sql);

            configuration.getMappedStatementMap().put(statementId, mappedStatement);
        }
    }
}
