package com.alphadelete.utils;

import com.badlogic.gdx.math.Vector2;

public abstract class Vector2DUtils {
	
	// http://stackoverflow.com/questions/27426053/find-specific-point-between-2-points-three-js
	
	public static Vector2 getPointInBetweenByLen(Vector2 pointA, Vector2 pointB, float length) {

		Vector2 dir = pointB.sub(pointA).nor().scl(length);
	    return pointA.add(dir);

	}
	
	public static Vector2 getPointInBetweenByPerc(Vector2 pointA, Vector2 pointB, float percentage) {

		Vector2 dir = pointB.sub(pointA);
	    float len = dir.len();
	    dir = dir.nor().scl(len*percentage);
	    return pointA.add(dir);

	}
	
	public static float getAngleInBetween(Vector2 pointA, Vector2 pointB) {

		double dx = pointB.x - pointA.x; 
		double dy = pointB.y - pointA.y;
		return (float)Math.atan2(dy, dx);

	}

}
