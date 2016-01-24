package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.components.EnemyComponent;
import com.alphadelete.sandbox.components.MovementComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.alphadelete.sandbox.components.StateComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

public class EnemySystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family = Family.all(EnemyComponent.class,
													   StateComponent.class,
													   TransformComponent.class,
													   MovementComponent.class).get();

	private ComponentMapper<EnemyComponent> bm;
	private ComponentMapper<StateComponent> sm;
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<MovementComponent> mm;
	
	public EnemySystem() {
		super(family);
				
		bm = ComponentMapper.getFor(EnemyComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);		
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		EnemyComponent enemy = bm.get(entity);
		TransformComponent t = tm.get(entity);
		StateComponent state = sm.get(entity);
		MovementComponent mov = mm.get(entity);
		
		// Copy Vectors
		Vector2 targetPos = enemy.target.cpy();
		Vector2 enemyPos = t.getPosition();
		
		
		// Move
		mov.velocity.x = -enemy.accel.x * EnemyComponent.MOVE_VELOCITY;
		mov.velocity.y = -enemy.accel.y * EnemyComponent.MOVE_VELOCITY;
	
		// State: Idle or Walk 
		if (state.get() != EnemyComponent.STATE_WALK && (mov.velocity.y != 0 || mov.velocity.x != 0) ) {
			state.set(EnemyComponent.STATE_WALK);
		}
		if (state.get() != EnemyComponent.STATE_IDLE && mov.velocity.y == 0 && mov.velocity.x == 0 ) {
			state.set(EnemyComponent.STATE_IDLE);
		}
		
		// Sprite side (scale)
		Vector2 side = targetPos.cpy().sub(enemyPos);
		if (enemy.accel.x < 0) {
			enemy.scaleSide = Constants.SCALE_LEFT;
		} else {
			enemy.scaleSide = Constants.SCALE_RIGHT;
		}
		if (side.x > 0){
			enemy.scaleSide = Constants.SCALE_LEFT;
		} else {
			enemy.scaleSide = Constants.SCALE_RIGHT;
		}
	
		t.scale.x = Math.abs(t.scale.x) * enemy.scaleSide;

	}

		
}