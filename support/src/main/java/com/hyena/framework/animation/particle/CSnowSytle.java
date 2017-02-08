package com.hyena.framework.animation.particle;

import java.util.ArrayList;
import java.util.List;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.texture.CTexture;

import android.graphics.Point;

/**
 * 雪花效果
 * @author yangzc
 */
public class CSnowSytle extends CParticleSystem {

	private List<CTexture> mTextures;
	private static int SNOW_SIZE = 58;
	
	protected CSnowSytle(Director director, CTexture ...textures){
		super(director, SNOW_SIZE);
		mTextures = new ArrayList<CTexture>();
		if(textures != null){
			for(int i=0; i< textures.length; i++){
				mTextures.add(textures[i]);
			}
		}
	}
	
	public static CSnowSytle create(Director director, CTexture ...textures){
		return new CSnowSytle(director, textures);
	}
	
	public void snow(Director director){
		if(mTextures == null || mTextures.isEmpty())
			return;
		CTexture texture = mTextures.get(0);
		
		for(int i=0; i< SNOW_SIZE; i++){
			Point startPoint = new Point(getStartX(), getPosition().y -i);
			CParticle particle = CParticle.create(director, getRecycleParticle(), texture, startPoint, schedulePaths(startPoint, 100), getDuration());
			if(particle != null)
				addParticle(particle);
		}
	}
	
	private int getStartX(){
		return (int) (getWidth()* getRandom());
	}
	
	private int getDuration(){
		return (int) (1000 + getRandom() * 2000);
	}
	
	private List<Point> schedulePaths(Point startPoint, int cnt){
		int height = getHeight();
		Point endPoint = new Point((int)(startPoint.x - getRandom()* 20), height);
		Point ctrlPoint = new Point((endPoint.x - startPoint.x)/2 + endPoint.x + 10, (height >> 1) - 10);
		return calculateBezierPaths(startPoint, ctrlPoint, endPoint, cnt);
	}
}
