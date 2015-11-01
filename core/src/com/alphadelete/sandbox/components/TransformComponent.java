package com.alphadelete.sandbox.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TransformComponent implements Component {
	public final Vector3 pos = new Vector3();
	public final Vector2 scale = new Vector2(1.0f, 1.0f);
	public float rotation = 0.0f;
	
	
	public void setPosition(float x, float y){
		setPosition(new Vector2(x, y));
	}
	
	public void setPosition(Vector2 pos){
		this.pos.x = pos.x;
		this.pos.y = pos.y;
	}
}