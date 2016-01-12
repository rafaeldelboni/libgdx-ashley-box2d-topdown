package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.Assets;
import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.GameWorld;
import com.alphadelete.sandbox.components.PlayerComponent;
import com.alphadelete.sandbox.components.BackgroundComponent;
import com.alphadelete.sandbox.components.MovementComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.alphadelete.sandbox.components.StateComponent;
import com.alphadelete.sandbox.components.TextureComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
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
	private boolean attack = false;
	private float attackX = 0.0f;
	private float attackY = 0.0f;
	
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
	
	public void setAttack(boolean attack, float x, float y) {
		this.attack = attack;
		this.attackX = x;
		this.attackY = y;
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
				
		if (attack) {
			createEffectAttack(attackX, attackY);
		}
		
		mov.velocity.x = -accelX * PlayerComponent.MOVE_VELOCITY;
		mov.velocity.y = -accelY * PlayerComponent.MOVE_VELOCITY;
		
		if (state.get() != PlayerComponent.STATE_WALK && (mov.velocity.y != 0 || mov.velocity.x != 0) ) {
			state.set(PlayerComponent.STATE_WALK);
		}
		
		if (state.get() != PlayerComponent.STATE_IDLE && mov.velocity.y == 0 && mov.velocity.x == 0 ) {
			state.set(PlayerComponent.STATE_IDLE);
		}
	
		if (accelX < 0) {
			t.scale.x = Math.abs(t.scale.x) * Constants.SCALE_LEFT;
		} else if (accelX > 0) {
			t.scale.x = Math.abs(t.scale.x) * Constants.SCALE_RIGHT;
		}

	}
	
	private void createEffectAttack(float attackX, float attackY) {
		PooledEngine engine = ((PooledEngine)getEngine());
		
		Entity entity = engine.createEntity();

		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		
		position.scale.set(0.1f, 0.1f);
		position.pos.set(attackX, attackY, 0.0f);
		texture.region = Assets.attackEffect;

		entity.add(position);
		entity.add(texture);

		engine.addEntity(entity);	
	}
	

}