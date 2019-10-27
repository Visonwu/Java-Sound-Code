package com.vison.service;

import com.vison.dao.DictionaryDao;
import com.vison.entity.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: vison
 * @Description:
 */
@Service
public class DictionaryService {

    @Autowired
    private DictionaryDao dictionaryDao;

    public long addOne(Dictionary dictionary) {
        this.dictionaryDao.addOne(dictionary);
        return dictionary.getDictionaryId();
    }
}
