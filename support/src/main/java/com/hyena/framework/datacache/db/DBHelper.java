package com.hyena.framework.datacache.db;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.cache.CacheEntry;
import com.hyena.framework.network.NetworkProvider;

/**
 * <h2>缓存数据库帮助类</h2>
 */
public class DBHelper {

	private static final String TAG = "CacheDBHelper";
	private static DBHelper instance = null;
	/** 最多保存200条数据 */
	private static int MAX_ROW_COUNT = 200;

	public static DBHelper getInstance(Context context) {
		if (instance != null)
			return instance;
		synchronized (DBHelper.class) {
			if (instance == null)
				instance = new DBHelper(context.getApplicationContext());
		}
		return instance;
	}

	private DBHelper(Context context) {
	}

	/**
	 * 将缓存条目插入数据库
	 * 
	 * @param cacheEntry
	 *            缓存条目
	 */
	public void insert(CacheEntry cacheEntry) {
		DataCacheTable table = DataBaseManager.getDataBaseManager().getTable(DataCacheTable.class);
		table.getDatabase().beginTransaction();
		try {
			table.deleteByKey(cacheEntry.getKey());
			table.insert(cacheEntry.toDataCacheItem());
			table.getDatabase().setTransactionSuccessful();
		} catch (Exception e) {
			LogUtil.e("", e);
		} finally {
			table.getDatabase().endTransaction();
		}
//		insert(cacheEntry.getKey(), cacheEntry.getData(),
//				cacheEntry.getValidTime());
        //无网络状态不进行删除
        if (!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()) {
            triggerDelete();
        }
	}

	/**
	 * 从数据库读取缓存条目
	 * 
	 * @param key
	 *            缓存key
	 * @param cacheEntry
	 *            空缓存条目
	 * @return 填充后的缓存条目
	 */
	public CacheEntry get(String key, CacheEntry cacheEntry) {
		LogUtil.d(TAG, "get key : " + key);
		try{
			if (TextUtils.isEmpty(key) || cacheEntry == null) {
				LogUtil.e(TAG, "get error , url is invalidate");
				return null;
			}
			DataCacheTable table = DataBaseManager.getDataBaseManager().getTable(DataCacheTable.class);
			List<DataCacheItem> items = table.queryByKey(key);
			if(items == null || items.size() == 0)
				return null;
			DataCacheItem entry = items.get(0);
			cacheEntry.setKey(entry.getKey());
			cacheEntry.setData(entry.getData());
			cacheEntry.setEnterTime(entry.getEnterTime());
			cacheEntry.setLastUsedTime(entry.getLastUsedTime());
			cacheEntry.setValidTime(entry.getValidTime());
		}catch(Exception e){
			e.printStackTrace();
		}
		return cacheEntry;
	}

	/**
	 * 从数据库删除key对应缓存条目
	 * 
	 * @param key
	 */
	public void delete(final String key) {
		DataCacheTable table = DataBaseManager.getDataBaseManager().getTable(DataCacheTable.class);
		table.deleteByKey(key);
	}

	/**
	 * 从数据库删除key对应缓存条目<br/>
	 * 
	 * 检查数据条数，优先清理最近最少使用的数据
	 */
	public void triggerDelete() {
		DataCacheTable table = DataBaseManager.getDataBaseManager().getTable(DataCacheTable.class);
		table.triggerDelete(MAX_ROW_COUNT);
	}

	/**
	 * 更新某一条缓存条目
	 * 
	 * @param cacheEntry
	 *            缓存条目
	 */
	public void update(CacheEntry cacheEntry) {
		if (cacheEntry == null) {
			LogUtil.e(TAG, "update error cacheEntry is null");
			return;
		}
		DataCacheTable table = DataBaseManager.getDataBaseManager().getTable(DataCacheTable.class);
		List<DataCacheItem> items = table.queryByKey(cacheEntry.getKey());
		if(items != null && items.size() > 0){
			DataCacheItem value = items.get(0);
			value.setLastUsedTime(cacheEntry.getLastUsedTime());
			value.setValidTime(cacheEntry.getValidTime());
			table.updateBykey(value);
		}
	}
	
	public void setMaxRowCount(int count) {
		MAX_ROW_COUNT = count;
	}
}
