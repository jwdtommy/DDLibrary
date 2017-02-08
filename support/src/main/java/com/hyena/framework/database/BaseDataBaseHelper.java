/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.database;

import com.hyena.framework.datacache.db.DataCacheTable;
import com.hyena.framework.download.db.DownloadTable;

import android.content.Context;

/**
 * 数据库帮助类基类
 * @author yangzc on 15/8/21.
 */
public abstract class BaseDataBaseHelper extends DataBaseHelper {

    public BaseDataBaseHelper(Context context, String name, int version) {
        super(context, name, version);
    }

    @Override
    public void initTables(DataBaseHelper db) {
        addTable(DataCacheTable.class, new DataCacheTable(db));// 注册缓存表
        addTable(DownloadTable.class, new DownloadTable(db));//下载表
        initTablesImpl(db);
    }

    /**
     * 初始化所有业务表
     * @param db
     */
    public abstract void initTablesImpl(DataBaseHelper db);
}
