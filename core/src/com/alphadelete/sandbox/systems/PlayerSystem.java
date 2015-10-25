package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.GameWorld;
import com.alphadelete.sandbox.components.PlayerComponent;
import com.alphadelete.sandbox.components.MovementComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.alphadelete.sandbox.components.StateComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;

public class PlayerSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family = Family.all(PlayerComponent.class,
													   StateComponent.class,
													   TransformComponent.class,
													   MovementComponent.class).get();
	
	private float accelX = 0.0f;
	private float accelY = 0.0f;
	private float scaleX = Constants.SCALE_RIGHT;
	
	private ComponentMapper<PlayerComponent> bm;
	private ComponentMapper<StateComponent> sm;
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<MovementComponent> mm;
	
	public PlayerSystem() {
		super(family);
				
		bm = ComponentMapper.getFor(PlayerComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);		
	}
	
	public void setAccelX(float accelX) {
		this.accelX = accelX;
	}
	
	public void setAccelY(float accelY) {
		this.accelY = accelY;
	}
	
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		accelX = 0.0f;
		accelY = 0.0f;
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent t = tm.get(entity);
		StateComponent state = sm.get(entity);
		MovementComponent mov = mm.get(entity);
		
		mov.velocity.x = -accelX * PlayerComponent.MOVE_VELOCITY;
		mov.velocity.y = -accelY * PlayerComponent.MOVE_VELOCITY;
		
		if (state.get() != PlayerComponent.STATE_WALK && (mov.velocity.y != 0 || mov.velocity.x != 0) ) {
			state.set(PlayerComponent.STATE_WALK);
		}
		
		if (state.get() != PlayerComponent.STATE_IDLE && mov.velocity.y == 0 && mov.velocity.x == 0 ) {
			state.set(PlayerComponent.STATE_IDLE);
		}
	
		if (scaleX < 0) {
			t.scale.x = Math.abs(t.scale.x);
		} else {
			t.scale.x = Math.abs(t.scale.x) * -1.0f;
		}

	}

}