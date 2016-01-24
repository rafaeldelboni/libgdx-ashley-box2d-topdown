package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.components.BoundsComponent;
import com.alphadelete.sandbox.components.EnemyComponent;
import com.alphadelete.sandbox.components.EffectsComponent;
import com.alphadelete.sandbox.components.MovementComponent;
import com.alphadelete.sandbox.components.PlayerComponent;
import com.alphadelete.sandbox.components.StateComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;

public class CollisionSystem extends EntitySystem {
	
	private ComponentMapper<BoundsComponent> bm;
	private ComponentMapper<MovementComponent> mm;
	private ComponentMapper<StateComponent> sm;
	private ComponentMapper<TransformComponent> tm;
	
	private Engine engine;
	private World world;
	
	private ImmutableArray<Entity> player;
	private ImmutableArray<Entity> enemies;
	private ImmutableArray<Entity> effects;
	
	public CollisionSystem(World world) {
		this.world = world;
		
		bm = ComponentMapper.getFor(BoundsComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
		
		player = engine.getEntitiesFor(Family.all(PlayerComponent.class, BoundsComponent.class, TransformComponent.class, StateComponent.class).get());
		enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class, BoundsComponent.class).get());
		effects = engine.getEntitiesFor(Family.all(EffectsComponent.class, BoundsComponent.class).get());

	}
	

	@Override
	public void update(float deltaTime) {
		EnemySystem enemySystem = engine.getSystem(EnemySystem.class);
		
		for (int i = 0; i < enemies.size(); ++i) {
			
			Entity enemy = enemies.get(i);
			
			StateComponent enemyState = sm.get(enemy);
			
			MovementComponent enemyMov = mm.get(enemy);
			BoundsComponent enemyBounds = bm.get(enemy);
				
			for (int j = 0; j < effects.size(); ++j) {
				Entity effect = effects.get(j);
				
				BoundsComponent effectBounds = bm.get(effect);
				
				if (effectBounds.bounds.overlaps(enemyBounds.bounds)) {
					Gdx.app.debug("Hit", "Test");
				}
			}
			
		}
	}
}