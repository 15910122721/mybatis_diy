package com.wxy.executor;

import com.wxy.domain.Configuration;
import com.wxy.domain.MappedStatement;

import java.util.List;

public interface Executor {
    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object param) throws Exception;

    void close();
}
