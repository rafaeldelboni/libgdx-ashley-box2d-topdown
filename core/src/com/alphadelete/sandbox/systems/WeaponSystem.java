package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.components.TransformComponent;
import com.alphadelete.sandbox.components.WeaponComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class WeaponSystem extends IteratingSystem {
	
	@SuppressWarnings("unchecked")
	private static final Family family = Family.all(
			WeaponComponent.class, 
			TransformComponent.class).get();
	
	private ComponentMapper<WeaponComponent> wm;
	private ComponentMapper<TransformComponent> tm;
	
	public WeaponSystem() {
		super(family);
		
		wm = ComponentMapper.getFor(WeaponComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		WeaponComponent weapon = wm.get(entity);
		TransformComponent t = tm.get(entity);
		
		weapon.position.pos.set(t.pos.x, t.pos.y, -4f);
	}
}