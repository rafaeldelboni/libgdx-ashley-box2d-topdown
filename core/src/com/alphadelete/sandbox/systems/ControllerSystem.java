package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.components.PlayerComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class ControllerSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family = Family.all(PlayerComponent.class).get();
	private ComponentMapper<PlayerComponent> pm;
	
	private Vector2 accel = new Vector2 (0,0);
	private Vector2 attackPos = new Vector2 (0,0);
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
		player.isAttacking = this.isAttacking;
		player.attackPos.set(this.attackPos);
		
	}

	public void setAttack(boolean isAttacking, float attkX, float attkY) {
		this.attackPos = new Vector2(attkX, attkY);
		this.isAttacking = isAttacking;
		
	}

	public void setAccel(float accelX, float accelY) {
		this.accel = new Vector2(accelX, accelY);
	}
	
		
}