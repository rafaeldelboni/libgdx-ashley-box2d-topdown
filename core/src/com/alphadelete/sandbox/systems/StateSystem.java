package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.components.StateComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class StateSystem extends IteratingSystem {	
	private ComponentMapper<StateComponent> sm;
	
	@SuppressWarnings("unchecked")
	public StateSystem() {
		super(Family.all(StateComponent.class).get());
		
		sm = ComponentMapper.getFor(StateComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		sm.get(entity).time += deltaTime;
	}
}