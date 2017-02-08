/**
 * Copyright (C) 2014 The plugin_music Project
 */
package com.hyena.framework.servcie.audio;

import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.servcie.BaseService;
import com.hyena.framework.servcie.bus.IBusServiceStatusListener;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月13日 上午11:24:38
 * 总线服务
 */
public interface PlayerBusService extends IBusServiceStatusListener, BaseService {

	public static final String BUS_SERVICE_NAME = "player_bus";
	
	/**
	 * 播放
	 * @throws Exception
	 */
	public void play(Song song) throws Exception;
	
	/**
	 * 暂停
	 * @throws Exception
	 */
	public void pause() throws Exception;

	/**
	 * 重新播放
	 * @throws Exception
     */
	public void resume() throws Exception;

	/**
	 * seekTo
	 * @throws Exception
     */
	public void seekTo(long position) throws Exception;

	/**
	 * get position
	 * @throws Exception
     */
	public void getPosition() throws Exception;
	
	/**
	 * 获得播放Bus观察者
	 * @return
	 */
	public PlayerBusServiceObserver getPlayerBusServiceObserver();
}
