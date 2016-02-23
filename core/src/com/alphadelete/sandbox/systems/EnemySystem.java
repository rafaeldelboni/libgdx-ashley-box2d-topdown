package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.GameWorld;
import com.alphadelete.sandbox.components.BodyComponent;
import com.alphadelete.sandbox.components.EnemyComponent;
import com.alphadelete.sandbox.components.MovementComponent;
import com.alphadelete.sandbox.components.PlayerComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.alphadelete.sandbox.components.StateComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class EnemySystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	private static final Family family = Family.all(EnemyComponent.class,
													   StateComponent.class,
													   TransformComponent.class,
													   MovementComponent.class).get();

	private ComponentMapper<EnemyComponent> em;
	private ComponentMapper<StateComponent> sm;
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<MovementComponent> mm;
	private ComponentMapper<BodyComponent> bm;
	
	private GameWorld gameWorld;
	
	public EnemySystem(GameWorld gameWorld) {
		super(family);
		
		this.gameWorld = gameWorld;
				
		em = ComponentMapper.getFor(EnemyComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
		bm = ComponentMapper.getFor(BodyComponent.class);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		EnemyComponent enemy = em.get(entity);
		TransformComponent t = tm.get(entity);
		StateComponent state = sm.get(entity);
		MovementComponent mov = mm.get(entity);
		
		if (enemy.knockbackTimeMillis >= 0){
			enemy.knockbackTimeMillis -= deltaTime * 1000;
		} else if (enemy.knockbackTimeMillis < 0){
			// Stop knock back
			enemy.knockbackTimeMillis = 0;
			// Stop acceleration
			enemy.accel.setZero();
		}
		
		if (enemy.health < 1)
		{
			if (state.get() != EnemyComponent.STATE_DIE) {
				state.set(EnemyComponent.STATE_DIE);
			}
			
		} else {
			
			// Search for player
			searchPlayer(entity);
			
			// State: Idle or Walk 
			if (state.get() != EnemyComponent.STATE_WALK && (mov.velocity.y != 0 || mov.velocity.x != 0) ) {
				state.set(EnemyComponent.STATE_WALK);
			}
			if (state.get() != EnemyComponent.STATE_IDLE && state.get() != EnemyComponent.STATE_ATTACK && mov.velocity.y == 0 && mov.velocity.x == 0 ) {
				state.set(EnemyComponent.STATE_IDLE);
			}
		}
		// Copy Vectors
		Vector2 targetPos = enemy.target.cpy();
		Vector2 enemyPos = t.getPosition();
		
		// Move
		if (enemy.accel.x != 0 && enemy.accel.y != 0) {
			// Scale accel for diagonal movement
			enemy.accel.scl(0.75f);
		}
		mov.velocity.x = -enemy.accel.x * EnemyComponent.MOVE_VELOCITY;
		mov.velocity.y = -enemy.accel.y * EnemyComponent.MOVE_VELOCITY;

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
	public void takeDamage(Entity entity, float attackX, float attackY, float damage){
		takeDamage(entity, new Vector2(attackX, attackY), damage);
	}
	public void takeDamage(Entity entity, Vector2 attackPos, float damage) {
		if (!family.matches(entity)) return;

		EnemyComponent enemy = em.get(entity);
		TransformComponent t = tm.get(entity);

		Vector2 enemyPos = t.getPosition();
		
		// Knock back
		Vector2 att = attackPos.cpy().sub(enemyPos).nor().scl(10f);
		enemy.accel = att;
				
		enemy.health -= damage; 
		
		enemy.knockbackTimeMillis = 250;
	}
	
	private Fixture rayCastFixture;
	private void searchPlayer(Entity enemy) {
		StateComponent state = sm.get(enemy);
		
		@SuppressWarnings("unchecked")
		ImmutableArray<Entity> players = gameWorld.getEngine().getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class, StateComponent.class).get());
		
		for (int i = 0; i < players.size(); ++i) {
			Entity player = players.get(i);
			BodyComponent playerPos = bm.get(player);
			BodyComponent enemyPos = bm.get(enemy);
			
			Vector2 playerPos2d = playerPos.body.getPosition().cpy();
			Vector2 enemyPos2d = enemyPos.body.getPosition().cpy();
			
			EnemyComponent enemyComp = em.get(enemy);

			if(enemyPos2d.dst2(playerPos2d) < 30f) {
				if(enemyPos2d.dst2(playerPos2d) > 5f) {
		            // do path finding every 0.1 second
					enemyComp.nextNode = gameWorld.getAStartPathFinding().findNextNode(enemyPos2d, playerPos2d);
					
					if (enemyComp.nextNode != null) {
						
						Vector2 nodePos = new Vector2 (enemyComp.nextNode.x, enemyComp.nextNode.y);
						Vector2 enemyIntPos = new Vector2 (MathUtils.floor(enemyPos2d.x),MathUtils.floor(enemyPos2d.y)); 
						Vector2 att = enemyIntPos.cpy().sub(nodePos).nor().scl(Constants.GAME_ACCEL);
						
						if(enemyComp.knockbackTimeMillis == 0) {
							enemyComp.accel = att;
						}
						enemyComp.target = nodePos;
					}
				} else {
					if (state.get() != EnemyComponent.STATE_ATTACK) {
						state.set(EnemyComponent.STATE_ATTACK);
					}
				}
				/*	
				RayCastCallback callbackFirstBody = new RayCastCallback(){
					
					float _fraction = 1;
					@Override
					public float reportRayFixture (Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
						if(fixture.getFilterData().categoryBits == BodyComponent.CATEGORY_PLAYER_ATTACK || 
							fixture.getFilterData().categoryBits == BodyComponent.CATEGORY_MONSTER)
							  return 1;
						
						if(fraction <= _fraction) {
							_fraction = fraction;
							rayCastFixture = fixture;
						}
						return _fraction;
					}
				};
				
				gameWorld.getWorld().rayCast(callbackFirstBody, enemyPos2d, playerPos2d);
				if(rayCastFixture != null && rayCastFixture.getFilterData().categoryBits == BodyComponent.CATEGORY_PLAYER) {
					
					Vector2 att = enemyPos2d.cpy().sub(playerPos2d).nor().scl(Constants.GAME_ACCEL);
					EnemyComponent enemyComp = em.get(enemy);
					
					if(enemyComp.knockbackTimeMillis == 0) {
						enemyComp.accel = att;
					}

					enemyComp.target = playerPos2d;

				}
				*/
			}
		}
	}

		
}
