package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.Assets;
import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.components.PlayerComponent;
import com.alphadelete.sandbox.components.AnimationComponent;
import com.alphadelete.sandbox.components.BoundsComponent;
import com.alphadelete.sandbox.components.EffectsComponent;
import com.alphadelete.sandbox.components.EnemyComponent;
import com.alphadelete.sandbox.components.MovementComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.alphadelete.utils.Vector2DUtils;
import com.alphadelete.sandbox.components.StateComponent;
import com.alphadelete.sandbox.components.TextureComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

public class PlayerSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family = Family.all(PlayerComponent.class,
													   StateComponent.class,
													   TransformComponent.class,
													   MovementComponent.class).get();

	private ComponentMapper<PlayerComponent> bm;
	private ComponentMapper<StateComponent> sm;
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<MovementComponent> mm;
	
	private PooledEngine engine;
	
	public PlayerSystem(PooledEngine engine) {
		super(family);
		
		this.engine = engine;
		
		bm = ComponentMapper.getFor(PlayerComponent.class);
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
		PlayerComponent player = bm.get(entity);
		TransformComponent t = tm.get(entity);
		StateComponent state = sm.get(entity);
		MovementComponent mov = mm.get(entity);
		
		// Copy Vectors
		Vector2 targetPos = player.target.cpy();
		Vector2 playerPos = t.getPosition();
		
		// Target position and angle	
		Vector2 relativeTarget = Vector2DUtils.getPointInBetweenByLen(playerPos, targetPos, 1.5f);
		float angle = Vector2DUtils.getAngleInBetween(playerPos, targetPos);
		
		if (player.isAttacking) {
			
			if(player.attackStance == 0) {
				player.attackStance = 1f;
			} else {
				player.attackStance = 0;
			}

			// Move towards the attack, if stopped
			if(!player.getPlayerIsMoving()) {
				Vector2 att = targetPos.cpy().sub(playerPos);
				if (att.x < 0) {
					player.accel.x = 5f;
				}
				if (att.x > 0) {
					player.accel.x = -5f;
				}
				if (att.y < 0) {
					player.accel.y = 5f;
				}
				if (att.y > 0) {
					player.accel.y = -5f;
				}
			}
			
			// Attack effect
			createEffectAttack(relativeTarget.x, relativeTarget.y, angle);
		}
		
		// Move
		mov.velocity.x = -player.accel.x * PlayerComponent.MOVE_VELOCITY;
		mov.velocity.y = -player.accel.y * PlayerComponent.MOVE_VELOCITY;
	
		// State: Idle or Walk 
		if (state.get() != PlayerComponent.STATE_WALK && (mov.velocity.y != 0 || mov.velocity.x != 0) ) {
			state.set(PlayerComponent.STATE_WALK);
		}
		if (state.get() != PlayerComponent.STATE_IDLE && mov.velocity.y == 0 && mov.velocity.x == 0 ) {
			state.set(PlayerComponent.STATE_IDLE);
		}
		
		// Sprite side (scale)
		Vector2 side = targetPos.cpy().sub(playerPos);
		if (player.accel.x < 0) {
			player.scaleSide = Constants.SCALE_LEFT;
		} else {
			player.scaleSide = Constants.SCALE_RIGHT;
		}
		if (side.x > 0){
			player.scaleSide = Constants.SCALE_LEFT;
		} else {
			player.scaleSide = Constants.SCALE_RIGHT;
		}
	
		t.scale.x = Math.abs(t.scale.x) * player.scaleSide;

	}
	
	private void createEffectAttack(float attackX, float attackY, float angle) {
		
		Entity entity = engine.createEntity();
		
		StateComponent state = engine.createComponent(StateComponent.class);
		BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
		AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		EffectsComponent effect = new EffectsComponent(150);

		position.scale.set(0.35f, 0.35f);
		position.rotation = angle;
		position.pos.set(attackX, attackY, 0.0f);
		animation.animations.put(0, Assets.attackEffect);
		state.set(0);
		
		bounds.bounds.width = 0.35f;
		bounds.bounds.height = 0.35f;
		
		entity.add(state);
		entity.add(bounds);
		entity.add(animation);
		entity.add(position);
		entity.add(texture);
		entity.add(effect);
		
		engine.addEntity(entity);	
	}
		
}