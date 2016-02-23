package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.GameWorld;
import com.alphadelete.sandbox.components.PlayerComponent;
import com.alphadelete.sandbox.components.MovementComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.alphadelete.utils.Vector2DUtils;
import com.alphadelete.sandbox.components.StateComponent;
import com.alphadelete.sandbox.components.TextureComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
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
	private ComponentMapper<TextureComponent> txm;
	
	private GameWorld gameWorld;
		
	public PlayerSystem(GameWorld gameWorld) {
		super(family);
		
		this.gameWorld = gameWorld;
		
		bm = ComponentMapper.getFor(PlayerComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
		txm = ComponentMapper.getFor(TextureComponent.class);
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
		Vector2 relativeTarget = Vector2DUtils.getPointInBetweenByLen(playerPos, targetPos, 1f);
		float angle = Vector2DUtils.getAngleInBetween(playerPos, targetPos);
		
		if (player.isAttacking) {
			
			if(player.attackStance == 0.5f) {
				player.attackStance = 1f;
			} else {
				player.attackStance = 0.5f;
			}

			// Move towards the attack, if stopped
			if(!player.getPlayerIsMoving()) {
				Vector2 att = targetPos.cpy().sub(playerPos).nor().scl(-10f);
				player.accel = att;
			}
			
			// Attack effect
			gameWorld.createPlayerAttack(relativeTarget.x, relativeTarget.y, angle);
		}
		
		// Move
		if (player.accel.x != 0 && player.accel.y != 0) {
			// Scale accel for diagonal movement
			player.accel.scl(0.75f);
		}
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
	
	public void takeDamage(Entity entity, float attackX, float attackY, float damage){
		takeDamage(entity, new Vector2(attackX, attackY), damage);
	}
	public void takeDamage(Entity entity, Vector2 attackPos, float damage) {
		if (!family.matches(entity)) return;

		PlayerComponent player = bm.get(entity);
		TransformComponent t = tm.get(entity);
		TextureComponent tex = txm.get(entity);
		
		Vector2 enemyPos = t.getPosition();
		
		// Knock back
		Vector2 att = attackPos.cpy().sub(enemyPos).nor().scl(10f);
		player.accel = att;
				
		player.health -= damage; 
		
		tex.color = Color.RED;
		
		player.knockbackTimeMillis = 250;
	}
			
}