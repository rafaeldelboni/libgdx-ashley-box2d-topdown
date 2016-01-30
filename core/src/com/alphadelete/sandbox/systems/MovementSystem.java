package com.alphadelete.sandbox.systems;

import com.alphadelete.utils.Box2DUtils;
import com.alphadelete.sandbox.components.BodyComponent;
import com.alphadelete.sandbox.components.MovementComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

public class MovementSystem extends IteratingSystem {
	private Vector2 tmp = new Vector2();

	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<MovementComponent> mm;
	private ComponentMapper<BodyComponent> bm;

	@SuppressWarnings("unchecked")
	public MovementSystem() {
		super(Family.all(TransformComponent.class, MovementComponent.class).get());

		tm = ComponentMapper.getFor(TransformComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
		bm = ComponentMapper.getFor(BodyComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent pos = tm.get(entity);
		MovementComponent mov = mm.get(entity);
		BodyComponent body = bm.get(entity);
		
		if (body != null) {
			tmp.set(mov.accel).scl(deltaTime);
			mov.velocity.add(tmp);
			
			tmp.set(mov.velocity).scl(deltaTime);
			body.body.setLinearVelocity(tmp.x, tmp.y);		
			
			pos.setPosition(body.body.getPosition().x, body.body.getPosition().y + Box2DUtils.getHeight(body.body) / 2f, pos.pos.z);

		} else {
			tmp.set(mov.accel).scl(deltaTime);
			mov.velocity.add(tmp);

			tmp.set(mov.velocity).scl(deltaTime);
			pos.pos.add(tmp.x, tmp.y, 0.0f);
		}
		
	}
}