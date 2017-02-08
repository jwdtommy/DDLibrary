package com.hyena.framework.datacache.db;

import java.util.List;

import com.hyena.framework.database.BaseTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * 数据缓存表
 * @author yangzc
 */
public class DataCacheTable extends BaseTable<DataCacheItem> {

	public static final String TABLE_NAME = "cache";
	
	public static final String KEY = "key";
	public static final String DATA = "data";
	public static final String ENTER_TIME = "enter_time";
	public static final String LAST_USED_TIME = "last_used_time";
	public static final String VALID_TIME = "valid_time";
	
	public DataCacheTable(SQLiteOpenHelper sqlHelper) {
		super(TABLE_NAME, sqlHelper);
	}

	@Override
	public DataCacheItem getItemFromCursor(Cursor cursor) {
		DataCacheItem cacheEntry = new DataCacheItem();
		cacheEntry.setId(getValue(cursor, "id", Long.class));
		cacheEntry.setKey(getValue(cursor, KEY, String.class));
		cacheEntry.setData(getValue(cursor, DATA, String.class));
		cacheEntry.setEnterTime(getValue(cursor, ENTER_TIME, Long.class));
		cacheEntry.setLastUsedTime(getValue(cursor, LAST_USED_TIME, Long.class));
		cacheEntry.setValidTime(getValue(cursor, VALID_TIME, Long.class));
		return cacheEntry;
	}

	@Override
	public ContentValues getContentValues(DataCacheItem item) {
		ContentValues values = new ContentValues();
		values.put(KEY, item.getKey());
		values.put(DATA, item.getData());
		values.put(ENTER_TIME, System.currentTimeMillis());
		values.put(LAST_USED_TIME, System.currentTimeMillis());
		values.put(VALID_TIME, item.getValidTime());
		return values;
	}

	@Override
	public String getCreateSql() {
		String sql = "CREATE TABLE IF NOT EXISTS "
				+ DataCacheTable.TABLE_NAME + " (" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DataCacheTable.KEY + " TEXT UNIQUE,"
				+ DataCacheTable.DATA + " TEXT NOT NULL,"
				+ DataCacheTable.ENTER_TIME + " INTEGER NOT NULL,"
				+ DataCacheTable.LAST_USED_TIME + " INTEGER NOT NULL,"
				+ DataCacheTable.VALID_TIME + " INTEGER NOT NULL); ";
		return sql;
	}

	@Override
	public long insert(DataCacheItem item) {
		if (TextUtils.isEmpty(item.getKey()) || TextUtils.isEmpty(item.getData()))
			return -1;
		
		return super.insert(item);
	}
	
	public void deleteByKey(String key){
		String where = KEY + "=?";
		String[] args = new String[1];
		args[0] = key;
		
		deleteByCase(where, args);
	}
	
	public void updateBykey(DataCacheItem item){
		if (item == null) {
			return;
		}
		String whereClause = KEY + "=?";
		String[] whereArgs = new String[1];
		whereArgs[0] = item.getKey();
		updateByCase(item, whereClause, whereArgs);
	}
	
	public List<DataCacheItem> queryByKey(String key) {
		String whereClause = KEY + "=?";
		String[] whereArgs = new String[1];
		whereArgs[0] = key;
		return queryByCase(whereClause, whereArgs, null);
	}

	public void triggerDelete(int maxRowCnt) {
		String whereClause = " _id IN (SELECT _id FROM "
				+ TABLE_NAME + " ORDER BY "
				+ LAST_USED_TIME
				+ " LIMIT 0, max((SELECT COUNT(*) FROM CACHE)-" + maxRowCnt
				+ ", 0))";
		deleteByCase(whereClause, null);
	}
}
