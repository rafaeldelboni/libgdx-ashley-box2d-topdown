package com.alphadelete.sandbox.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;


public class WeaponComponent implements Component {
	public static final int TYPE_PLAYER = 0;
	public static final int TYPE_ENEMY = 1;
	
	public TransformComponent position; 
	public TextureComponent texture;
	public int userType;
	
	public WeaponComponent(PooledEngine engine, Vector3 positon, TextureRegion texture, int userType) {
		Entity entity = engine.createEntity();
		
		this.texture = engine.createComponent(TextureComponent.class);
		this.position = engine.createComponent(TransformComponent.class);
		
		this.position.scale.set(1.2f, 1.2f);
		this.position.pos.set(positon);
		this.texture.region = texture;
		
		this.userType = userType;
		
		entity.add(this.position);
		entity.add(this.texture);
		
		engine.addEntity(entity);
	}

}