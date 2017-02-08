package com.hyena.framework.database;

/**
 * 数据库管理器
 * @author yangzc
 */
public class DataBaseManager {

	// 应用主数据库
	private DataBaseHelper mDefaultDbHelper;

	public static DataBaseManager _instance = null;

	private DataBaseManager() {
	}

	public static DataBaseManager getDataBaseManager() {
		if (_instance == null) {
			_instance = new DataBaseManager();
		}
		return _instance;
	}

	/**
	 * 获得表实例
	 * 
	 * @param table
	 * @return
	 */
	public <T extends BaseTable<?>> T getTable(Class<T> table) {
		return mDefaultDbHelper.getTable(table);
	}

	/**
	 * 初始化数据库
	 * @param database
	 */
	public void registDataBase(DataBaseHelper database) {
		if (database != null)
            database.close();
		mDefaultDbHelper = database;
	}

	/**
	 * 获得默认数据库
	 * 
	 * @return
	 */
	public DataBaseHelper getDefaultDB() {
		return mDefaultDbHelper;
	}

	/**
	 * 清空默认数据库
	 */
	public void clearDataBase() {
		if(mDefaultDbHelper != null){
            mDefaultDbHelper.clearDataBase();
        }
	}

    /**
     * 释放数据库
     */
    public void releaseDB() {
        if (mDefaultDbHelper != null) {
            try {
                mDefaultDbHelper.close();
            } catch (Exception e) {
            }
            mDefaultDbHelper = null;
        }
    }

}
