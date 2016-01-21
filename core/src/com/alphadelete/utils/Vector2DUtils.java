package com.alphadelete.utils;

import com.badlogic.gdx.math.Vector2;

public class Vector2DUtils {
	
	// http://stackoverflow.com/questions/27426053/find-specific-point-between-2-points-three-js
	
	public final static Vector2 getPointInBetweenByLen(Vector2 a, Vector2 b, float length) {
		
		Vector2 pointA = a.cpy();
		Vector2 pointB = b.cpy();
		
		Vector2 dir = pointB.sub(pointA).nor().scl(length);
		
		Vector2 result = pointA.add(dir);
		
	    return result;

	}
	
	public final static Vector2 getPointInBetweenByPerc(Vector2 a, Vector2 b, float percentage) {
		
		Vector2 pointA = a.cpy();
		Vector2 pointB = b.cpy();
		
		Vector2 dir = pointB.sub(pointA);
	    float len = dir.len();
	    dir = dir.nor().scl(len*percentage);
	    
	    Vector2 result = pointA.add(dir);
	    
	    return result.add(dir);

	}
	
	public final static float getAngleInBetween(Vector2 a, Vector2 b) {
		
		Vector2 pointA = a.cpy();
		Vector2 pointB = b.cpy();
		
		Vector2 dir =  pointB.sub(pointA);
		return (float)Math.atan2(dir.y, dir.x);
	}

}
