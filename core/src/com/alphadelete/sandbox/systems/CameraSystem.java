package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.components.CameraComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class CameraSystem extends IteratingSystem {
	
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<CameraComponent> cm;
	
	@SuppressWarnings("unchecked")
	public CameraSystem() {
		super(Family.all(CameraComponent.class).get());
		
		tm = ComponentMapper.getFor(TransformComponent.class);
		cm = ComponentMapper.getFor(CameraComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CameraComponent cam = cm.get(entity);
		
		if (cam.target == null) {
			return;
		}
		
		TransformComponent target = tm.get(cam.target);
		
		if (target == null) {
			return;
		}
		cam.camera.position.lerp(target.pos, Constants.CAMERA_SMOOTH * deltaTime);
		//cam.camera.position.set(target.pos);

	}
}