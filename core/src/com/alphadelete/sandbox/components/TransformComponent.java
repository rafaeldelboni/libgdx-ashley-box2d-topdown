package com.alphadelete.sandbox.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TransformComponent implements Component {
	public final Vector3 pos = new Vector3();
	public final Vector2 scale = new Vector2(1.0f, 1.0f);
	public Vector2 origin = null;
	public float rotation = 0.0f;
	
	
	public void setPosition(float x, float y){
		setPosition(new Vector2(x, y));
	}
	
	public void setPosition(Vector2 pos){
		this.pos.x = pos.x;
		this.pos.y = pos.y;
	}
	
	public void setPosition(float x, float y, float z){
		setPosition(new Vector3(x, y, z));
	}
	
	public void setPosition(Vector3 pos){
		this.pos.set(pos);
	}
	
	public void setOrigin(float x, float y){
		setOrigin(new Vector2 (x,y));
	}
	
	public void setOrigin(Vector2 orig){
		this.origin = new Vector2 (orig.x, orig.y);
	}
	
	public Vector2 getPosition(){
		return new Vector2(this.pos.x, this.pos.y);
	}
	
	public Boolean isOriginSet(){
		if(origin != null) {
			return true;
		}
		return false;
	}
}