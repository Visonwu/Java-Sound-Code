package com.vison.dao;

import com.vison.entity.OtherTable;

import java.util.List;

/**
 * @Author: vison
 * @Description:
 */
public interface OtherTableDao {

    long addOne(OtherTable table);

    List<OtherTable> getAll();

}
