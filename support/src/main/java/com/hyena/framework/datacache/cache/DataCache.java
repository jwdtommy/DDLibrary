package com.hyena.framework.datacache.cache;

import android.content.Context;
import android.text.TextUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.config.FrameworkConfig;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.db.DBHelper;
import com.hyena.framework.datacache.objects.LruCache2;
import com.hyena.framework.network.NetworkProvider;

/**
 * <h2>数据缓存</h2><br/>
 * <p>
 * 用来在内存和数据库中缓存数据；<br/>
 * 默认使用LRU算法清理过期缓存对象<br/>
 */
public class DataCache {
    protected static final String TAG = "DataCache";
    private static DataCache instance;

    private Context mContext;
    /**
     * 最大缓存空间默认值：1M
     */
    private long mMaxSpace = 1 * 1024 * 1024;
    /**
     * 内存缓存当前占用的内存空间大小
     */
    private long mCacheSpace = 0;

    protected int hitCount = 0;

    protected int missCount = 0;

    /**
     * 缓存体
     */
    protected LruCache2<String, CacheEntry> mCache;
    /**
     * 待更新集合
     */
    private ConcurrentHashMap<Integer, CacheEntry> mUpdateSet = new ConcurrentHashMap<Integer, CacheEntry>();
    /**
     * 更新阀，当更新集合元素数大于该阀时更新数据库
     */
    private final int UPDATE_THRESHOLD = 10;

    private DataCache() {
    }

    public static DataCache getInstance(Context context) {
        if (instance != null)
            return instance;
        synchronized (DataCache.class) {
            if (instance == null)
                instance = new DataCache(context.getApplicationContext());
        }
        return instance;
    }

    private DataCache(Context context) {
        mContext = context;
        mCache = new LruCache2<String, CacheEntry>(2, 0) {

            @Override
            public void recycleEldestEntry(CacheEntry eldestRef) {
                mCacheSpace -= eldestRef.caculateMemSize();
                validate();
            }
        };
    }

    public boolean hasContain(String key) {
        if (mCache.get(key) == null) {

            CacheEntry entry = new CacheEntry();
            entry.setObject(new BaseObject());
            entry = DBHelper.getInstance(mContext).get(key, entry);
            return entry != null;
        }
        return true;
    }

    /**
     * 从缓存模块读取缓存对象
     *
     * @param key   缓存对象的key
     * @param entry 为填充的缓存对象
     * @return 从缓存模块获取数据后填充完成的缓存对象
     * @throws CacheExpiredException
     */
    public Cacheable get(String key, CacheEntry entry) throws CacheExpiredException, CacheUncachedException {
        if (TextUtils.isEmpty(key) || entry == null)
            return null;
        CacheEntry cacheEntry = mCache.get(key);

        debug("readFromMemCache entry : " + cacheEntry);
        if (cacheEntry == null)
            cacheEntry = readFromDatabase(key, entry.getObject());

        Cacheable cachedObj = null;
        if (cacheEntry != null) {
            cacheEntry.setLastUsedTime(System.currentTimeMillis());
            mUpdateSet.put(cacheEntry.hashCode(), cacheEntry);
            if (mUpdateSet.size() > UPDATE_THRESHOLD)
                update();
            cachedObj = cacheEntry.getObject();
        }
        if (cachedObj == null)
            missCount++;
        else
            hitCount++;
        entry.setObject(cachedObj);
        if (cachedObj == null)
            throw new CacheUncachedException();
        if (cacheEntry.isExpired() && NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable())
            throw new CacheExpiredException();
        return cachedObj;
    }

    /**
     * 将缓存对象加入缓存
     *
     * @param key       缓存对象的key
     * @param obj       缓存对象
     * @param validTime 缓存时间(以毫秒计，超过该时间后将无法从缓存模块获得该缓存对象)
     * @return 缓存成功后的缓存对象
     */
    public Cacheable put(String key, Cacheable obj, long validTime) {
        if (TextUtils.isEmpty(key) || obj == null || !obj.isCacheable() || validTime <= 0)
            return null;
        long cacheTime = System.currentTimeMillis();
        CacheEntry entry = new CacheEntry(key, obj, cacheTime, validTime);
        putIntoMemCache(key, entry);
        validate();// 验证缓存空间
        saveToDatabase(entry);
        return obj;
    }

    /**
     * 将缓存条目保存到数据库
     *
     * @param entry 缓存条目
     */
    private void saveToDatabase(CacheEntry entry) {
        DBHelper.getInstance(mContext).insert(entry);
    }

    /**
     * 从数据库读取缓存条目
     *
     * @param key 缓存条目key
     * @param obj 缓存空数据对象
     * @return 缓存条目
     */
    private CacheEntry readFromDatabase(String key, Cacheable obj) {
        CacheEntry entry = new CacheEntry();
        entry.setObject(obj);
        entry = DBHelper.getInstance(mContext).get(key, entry);
        putIntoMemCache(key, entry);
        return entry;
    }

    /**
     * 将缓存条目加入内存缓存
     *
     * @param key   缓存键
     * @param entry 缓存条目
     */
    private void putIntoMemCache(String key, CacheEntry entry) {
        if (TextUtils.isEmpty(key) || entry == null)
            return;
        mCache.boom();// 缓存容量+1
        mCache.put(key, entry);// 加入缓存
        debug("putIntoMemCache entry : " + entry);
        mCacheSpace += entry.caculateMemSize();// 计算缓存所占空间
    }

    /**
     * 检查缓存空间大小
     */
    private void validate() {
        debug("validate() mCacheSpace : " + mCacheSpace);
        if (mCacheSpace > mMaxSpace)
            mCache.trimToSize(mCache.size() - 1);
    }

    /**
     * 异步更新数据库
     */
    private void update() {
        new Thread() {
            @Override
            public void run() {
                Set<Integer> keys = mUpdateSet.keySet();
                CacheEntry entry = null;
                for (Integer key : keys) {
                    entry = mUpdateSet.remove(key);
                    DBHelper.getInstance(mContext).update(entry);
                }
            }
        }.start();
        ;
    }

    private void debug(String msg) {
        if (FrameworkConfig.getConfig().isDebug()) {
            LogUtil.d(TAG, msg);
        }
    }
}