package com.hyena.framework.animation.particle;

import java.util.ArrayList;
import java.util.List;

import com.hyena.framework.animation.CLayer;
import com.hyena.framework.animation.Director;

import android.graphics.Canvas;
import android.graphics.Point;

/**
 * 粒子系统
 * @author yangzc
 *
 */
public class CParticleSystem extends CLayer {

	protected ArrayList<CParticle> mRenderParticles;
	protected ArrayList<CParticle> mRecycleParticles;
	private int mMaxParticleCnt = 1000;
	
	protected CParticleSystem(Director director, int maxParticleCnt){
		super(director);
		this.mMaxParticleCnt = maxParticleCnt;
		
		mRenderParticles = new ArrayList<CParticle>(mMaxParticleCnt);
		mRecycleParticles = new ArrayList<CParticle>(mMaxParticleCnt);
	}
	
	protected CParticleSystem(Director director){
		super(director);
		mRenderParticles = new ArrayList<CParticle>(mMaxParticleCnt);
		mRecycleParticles = new ArrayList<CParticle>(mMaxParticleCnt);
	}
	
	public static CParticleSystem create(Director director){
		return new CParticleSystem(director);
	}
	
	@Override
	public synchronized void update(float dt) {
		super.update(dt);
		if(mRenderParticles == null)
			return;
		
		checkParticle();//检查精灵状态
		
		for(int i=0; i< mRenderParticles.size(); i++){
			CParticle sprite = mRenderParticles.get(i);
			if(sprite != null)
				sprite.update(dt);
		}
	}
	
	@Override
	public synchronized void render(Canvas canvas) {
		super.render(canvas);
		if (!isValid() || !isVisible()) {
			return;
		}

		if(mRenderParticles == null)
			return;
		
		for(int i=0; i< mRenderParticles.size(); i++){
			CParticle sprite = mRenderParticles.get(i);
			if(sprite != null)
				sprite.render(canvas);
		}
	}
	
	/**
	 * 添加粒子
	 * @param particle
	 */
	protected synchronized void addParticle(CParticle particle){
		if(mRenderParticles == null || mRenderParticles.size() >= mMaxParticleCnt){
			return;
		}
		mRenderParticles.add(particle);
	}
	
	/**
	 * 检查精灵是否存活
	 */
	protected void checkParticle(){
		if(mRenderParticles == null)
			return;
		
		List<CParticle> delSprite = new ArrayList<CParticle>();
		for(int i=0; i< mRenderParticles.size(); i++){
			CParticle sprite = mRenderParticles.get(i);
			if(!sprite.isAlive()){
				if(mRecycleParticles.size() < mMaxParticleCnt){
					mRecycleParticles.add(sprite);
				}
				delSprite.add(sprite);
				sprite.release();
			}
		}
		mRenderParticles.removeAll(delSprite);
	}
	
	/**
	 * 获得回收池中的粒子
	 * @return
	 */
	protected CParticle getRecycleParticle(){
		if(mRecycleParticles.size() > 0){
			return mRecycleParticles.remove(0);
		}
		return null;
	}
	
	/**
	 * 获得随机数
	 * @return
	 */
	protected double getRandom(){
		return Math.random();
	}
	
	/**
	 * 计算贝塞尔曲线路径
	 * @param start
	 * @param ctrl
	 * @param end
	 * @return
	 */
	protected List<Point> calculateBezierPaths(Point start, Point ctrl, Point end, int cnt) {
        List<Point> points = new ArrayList<Point>();
        float tempX, tempY;
        for(float i = 0; i <= 1; i += 1f/cnt) {
            tempX = (1 - i) * (1 - i) * start.x + 2 * i * (1 - i) * ctrl.x + i
                    * i * end.x;
            tempY = (1 - i) * (1 - i) * start.y + 2 * i * (1 - i) * ctrl.y + i
                    * i * end.y;
            Point temp = new Point((int)tempX, (int)tempY);
            points.add(temp);
        }
        return points;
    }
}
