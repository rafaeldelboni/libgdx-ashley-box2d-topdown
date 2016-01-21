package com.alphadelete.sandbox.systems;

import com.alphadelete.sandbox.components.PlayerComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.alphadelete.sandbox.components.WeaponComponent;
import com.alphadelete.utils.Vector2DUtils;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;

public class WeaponSystem extends IteratingSystem {
	
	@SuppressWarnings("unchecked")
	private static final Family family = Family.all(
			WeaponComponent.class, 
			TransformComponent.class).get();
	
	private ComponentMapper<WeaponComponent> wm;
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<PlayerComponent> pm;
	
	public WeaponSystem() {
		super(family);
		
		wm = ComponentMapper.getFor(WeaponComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
		pm = ComponentMapper.getFor(PlayerComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		WeaponComponent weapon = wm.get(entity);
		TransformComponent t = tm.get(entity);
		
		if(weapon.userType == WeaponComponent.TYPE_PLAYER) {
			PlayerComponent player = pm.get(entity);
			float angle = Vector2DUtils.getAngleInBetween(t.getPosition(), player.target);
			
			weapon.position.scale.y = Math.abs(weapon.position.scale.y) * player.scaleSide;
			weapon.position.rotation = angle;
			Gdx.app.debug("WeaponAngle", String.valueOf(angle));
			Gdx.app.debug("PlayerSide", String.valueOf(player.scaleSide));
		}
		
		weapon.position.pos.set(t.pos.x, t.pos.y, -4f);
	}
}