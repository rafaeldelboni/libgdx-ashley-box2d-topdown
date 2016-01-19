package com.alphadelete.sandbox.systems;


import com.alphadelete.sandbox.components.EffectsComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class EffectsSystem extends IteratingSystem {
	
	@SuppressWarnings("unchecked")
	private static final Family family = Family.all(EffectsComponent.class).get();
	
	private ComponentMapper<EffectsComponent> em;
	
	public EffectsSystem() {
		super(family);
		
		em = ComponentMapper.getFor(EffectsComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		EffectsComponent effect = em.get(entity);

		if (System.currentTimeMillis() - effect.startTimeMillis > effect.durationTimeMillis )
		{
            this.getEngine().removeEntity(entity);
		}
	}
}