package com.vison.mapper;

import com.vison.entity.Config;

public interface ConfigMapper {
    int deleteByPrimaryKey(Integer configId);

    int insert(Config record);

    int insertSelective(Config record);

    Config selectByPrimaryKey(Integer configId);

    int updateByPrimaryKeySelective(Config record);

    int updateByPrimaryKey(Config record);
}