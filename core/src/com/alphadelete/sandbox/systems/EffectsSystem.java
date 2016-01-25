package com.alphadelete.sandbox.systems;


import com.alphadelete.sandbox.GameWorld;
import com.alphadelete.sandbox.components.BodyComponent;
import com.alphadelete.sandbox.components.EffectsComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class EffectsSystem extends IteratingSystem {
	
	@SuppressWarnings("unchecked")
	private static final Family family = Family.all(EffectsComponent.class, BodyComponent.class).get();
	
	private ComponentMapper<EffectsComponent> em;
	private ComponentMapper<BodyComponent> bm;
	private GameWorld gameWorld;
	
	public EffectsSystem(GameWorld gameWorld) {
		super(family);
		
		this.gameWorld = gameWorld;
		
		em = ComponentMapper.getFor(EffectsComponent.class);
		bm = ComponentMapper.getFor(BodyComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		EffectsComponent effect = em.get(entity);
		BodyComponent body = bm.get(entity);

		if (System.currentTimeMillis() - effect.startTimeMillis > effect.durationTimeMillis )
		{
			if(body != null) {
				gameWorld.destroyBody(body.body); 
			}
            this.getEngine().removeEntity(entity);
		}
	}
}