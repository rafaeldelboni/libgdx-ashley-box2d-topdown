package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.components.BoundsComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class BoundsSystem extends IteratingSystem {
	
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<BoundsComponent> bm;
	
	@SuppressWarnings("unchecked")
	public BoundsSystem() {
		super(Family.all(BoundsComponent.class, TransformComponent.class).get());
		
		tm = ComponentMapper.getFor(TransformComponent.class);
		bm = ComponentMapper.getFor(BoundsComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent pos = tm.get(entity);
		BoundsComponent bounds = bm.get(entity);
		
		bounds.bounds.x = pos.pos.x - bounds.bounds.width;
		bounds.bounds.y = pos.pos.y - bounds.bounds.height;
	}
}