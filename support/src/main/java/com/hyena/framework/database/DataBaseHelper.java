/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * 数据库帮助类
 * @author yangzc on 15/8/21.
 */
public abstract class DataBaseHelper extends SQLiteOpenHelper {

    //数据库维护的所有表
    private Hashtable<Class<? extends BaseTable<?>>, BaseTable<?>> mDbTables;

    public DataBaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
        mDbTables = new Hashtable<Class<? extends BaseTable<?>>, BaseTable<?>>();
        //初始化所有表
        initTables(this);
    }


    /**
     * 初始化所有表
     * 随应用启动时创建的表
     */
    public abstract void initTables(DataBaseHelper db);

    @Override
    public final void onCreate(SQLiteDatabase db) {
        //创建表
        createTables(db);
    }

    @Override
    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgradeImpl(db, oldVersion, newVersion);
    }

    /**
     * 添加表
     * @param clazz
     * @param table
     */
    public void addTable(Class<? extends BaseTable<?>> clazz, BaseTable<?> table) {
        mDbTables.put(clazz, table);
    }

    /**
     * 获得所有表
     * @return
     */
    public Hashtable<Class<? extends BaseTable<?>>, BaseTable<?>> getTables() {
        return mDbTables;
    }

    /**
     * 通过表类型获得表实例
     * @param table
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
	public <T extends BaseTable<?>> T getTable(Class<T> table) {
        return (T) getTables().get(table);
    }

    /**
     * 创建所有表
     * @param db
     */
    private void createTables(SQLiteDatabase db){
        if(mDbTables != null){
            Iterator<Class<? extends BaseTable<?>>> iterator = mDbTables.keySet().iterator();
            while (iterator.hasNext()) {
                Class<? extends BaseTable<?>> clazz = iterator.next();
                BaseTable<?> table = mDbTables.get(clazz);
                String sql = table.getCreateSql();
                if(!TextUtils.isEmpty(sql)){
                    db.execSQL(sql);
                }
            }
        }
    }

    /**
     * 数据库升级
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgradeImpl(SQLiteDatabase db, int oldVersion, int newVersion){
        if(mDbTables != null){
            Iterator<Class<? extends BaseTable<?>>> iterator = mDbTables.keySet().iterator();
            while (iterator.hasNext()) {
                Class<? extends BaseTable<?>> clazz = iterator.next();
                BaseTable<?> table = mDbTables.get(clazz);
                table.onUpgrade(db, oldVersion, newVersion);
            }
        }
    }

    /**
     * 清空默认数据库
     */
    public void clearDataBase() {
        if (mDbTables != null) {
            Iterator<Class<? extends BaseTable<?>>> iterator = mDbTables
                    .keySet().iterator();
            while (iterator.hasNext()) {
                Class<? extends BaseTable<?>> key = iterator.next();
                BaseTable<?> table = mDbTables.get(key);
                table.deleteByCase(null, null);
            }
        }
    }

}
