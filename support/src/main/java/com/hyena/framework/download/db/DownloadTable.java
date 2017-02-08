/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.download.db;

import java.sql.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.database.BaseTable;
import com.hyena.framework.download.Task;

/**
 * @author yangzc on 15/12/10.
 */
public class DownloadTable extends BaseTable<DownloadItem> {

    public static final String TABLE_NAME = "cache";

    public static final String TASKID = "taskid";
    public static final String SRC_PATH = "srcpath";
    public static final String DEST_PATH = "destpath";
    public static final String DOWNLOADED = "downloaded";
    public static final String TOTAL_LEN = "totallen";
    public static final String STATUS = "status";
    public static final String SOURCE_TYPE = "type";
    public static final String ADD_TIME = "addtime";
    public static final String EXT = "ext";

    public DownloadTable(SQLiteOpenHelper sqlHelper) {
        super("download", sqlHelper);
    }

    @Override
    public DownloadItem getItemFromCursor(Cursor cursor) {
        DownloadItem item = new DownloadItem();
        item.mTaskId = getValue(cursor, TASKID, String.class);
        item.mSrcPath = getValue(cursor, SRC_PATH, String.class);
        item.mDestPath = getValue(cursor, DEST_PATH, String.class);
        item.mDownloaded = getValue(cursor, DOWNLOADED, Long.class);
        item.mTotalLen = getValue(cursor, TOTAL_LEN, Long.class);
        item.mStatus = getValue(cursor, STATUS, Integer.class);
        item.mSourceType = getValue(cursor, SOURCE_TYPE, String.class);
        item.mExt = getValue(cursor, EXT, String.class);
        item.mAddDate = getValue(cursor, ADD_TIME, Date.class);
        return item;
    }

    @Override
    public ContentValues getContentValues(DownloadItem item) {
        ContentValues values = new ContentValues();
        values.put(TASKID, item.mTaskId);
        values.put(SRC_PATH, item.mSrcPath);
        values.put(DEST_PATH, item.mDestPath);
        values.put(DOWNLOADED, item.mDownloaded);
        values.put(TOTAL_LEN, item.mTotalLen);
        values.put(STATUS, item.mStatus);
        values.put(SOURCE_TYPE, item.mSourceType);
        values.put(EXT, item.mExt);
        return values;
    }

    @Override
    public String getCreateSql() {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + getTableName() + " (" + BaseColumns._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TASKID + " TEXT,"
                + SRC_PATH + " TEXT,"
                + DEST_PATH + " TEXT,"
                + DOWNLOADED + " INTEGER,"
                + TOTAL_LEN + " INTEGER,"
                + STATUS + " INTEGER,"
                + SOURCE_TYPE + " TEXT,"
                + ADD_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + EXT + " TEXT"
                + "); ";
        return sql;
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	super.onUpgrade(db, oldVersion, newVersion);
    	
    	dropTable();
    	execSQL(getCreateSql());
    }

    /**
     * 更新进度
     * @param taskId
     * @param progress
     * @param totalLen
     * @return
     */
    public int updateProgress(String taskId, long progress, long totalLen){
        int count = -1;
        try {
            SQLiteDatabase db = getDatabase();
            if(db == null)
                return -1;

            ContentValues values = new ContentValues();
            values.put(DOWNLOADED, progress);
            values.put(TOTAL_LEN, totalLen);
            count = db.update(getTableName(), values, TASKID + "=?", new String[]{taskId});
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        }
        return count;
    }

    /**
     * 更新状态
     * @param taskId
     * @param status
     * @return
     */
    public int updateStatus(String taskId, int status){
        int count = -1;
        try {
            SQLiteDatabase db = getDatabase();
            if(db == null)
                return -1;

            ContentValues values = new ContentValues();
            values.put(STATUS, status);
            count = db.update(getTableName(), values, TASKID + "=?", new String[]{taskId});
        } catch (Exception e) {
            LogUtil.e(getTableName(), e);
        }
        return count;
    }

    /**
     * 通过原始路径查询下载任务
     * @param srcPath
     * @return
     */
    public DownloadItem queryDownladBySrcPath(String srcPath){
        return querySingleByCase(SRC_PATH + "=?", new String[]{srcPath}, ADD_TIME + " asc");
    }

    /**
     * 通过任务ID查询下载任务
     * @param taskId
     * @return
     */
    public DownloadItem queryDownladByTaskId(String taskId){
        return querySingleByCase(TASKID + "=?", new String[]{taskId}, ADD_TIME + " asc");
    }
    
    /**
     * 获得所有未完成的任务
     * @return
     */
    public List<DownloadItem> queryUnFinishedDownload(){
    	return queryByCase(STATUS + "<" + Task.STATUS_COMPLETED, null, ADD_TIME + " asc");
    }
    
    /**
     * 获得所有完成的任务
     * @return
     */
    public List<DownloadItem> queryFinishedDownload(){
    	return queryByCase(STATUS + "=" + Task.STATUS_COMPLETED, null, ADD_TIME + " asc");
    }
    
    /**
     * 删除任务
     * @param taskId
     */
    public void removeDownload(String taskId){
    	deleteByCase(TASKID + "=?", new String[]{taskId});
    }
}
