package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.components.PlayerComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ControllerSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family = Family.all(PlayerComponent.class).get();
	private ComponentMapper<PlayerComponent> pm;
	
	private Vector2 accel = new Vector2 (0,0);
	private Vector2 target = new Vector2 (0,0);
	private Boolean isAttacking = false;
	
	public ControllerSystem() {
		super(family);
				
		pm = ComponentMapper.getFor(PlayerComponent.class);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		PlayerComponent player = pm.get(entity);
		
		player.accel.set(this.accel);
		player.target.set(this.target);
		player.isAttacking = this.isAttacking;
	}
	
	public void setControls(Input inputCtrl, boolean isAttacking, Vector3 targetPos) {
		this.isAttacking = isAttacking;
		this.target = new Vector2(targetPos.x, targetPos.y);
		
		float accelX = 0.0f, accelY = 0.0f;
		// Left
		if (inputCtrl.isKeyPressed(Keys.A)) {
			accelX = 5f;
		}
		// Right
		if (inputCtrl.isKeyPressed(Keys.D)) {
			accelX = -5f;
		}
		// Down
		if (inputCtrl.isKeyPressed(Keys.S)) {
			accelY = 5f;
		}
		// Up
		if (inputCtrl.isKeyPressed(Keys.W)) {
			accelY = -5f;
		}

		this.accel = new Vector2(accelX, accelY);
	}

}