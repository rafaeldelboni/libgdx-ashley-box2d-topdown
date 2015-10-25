package com.alphadelete.sandbox;

import java.util.Random;

import com.alphadelete.sandbox.components.AnimationComponent;
import com.alphadelete.sandbox.components.BackgroundComponent;
import com.alphadelete.sandbox.components.BodyComponent;
import com.alphadelete.sandbox.components.BoundsComponent;
import com.alphadelete.sandbox.components.CameraComponent;
import com.alphadelete.sandbox.components.MovementComponent;
import com.alphadelete.sandbox.components.PlayerComponent;
import com.alphadelete.sandbox.components.StateComponent;
import com.alphadelete.sandbox.components.TextureComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.alphadelete.sandbox.components.WallComponent;
import com.alphadelete.sandbox.systems.RenderingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class GameWorld {
	public static final float WORLD_WIDTH = Constants.APP_WIDTH;
	public static final float WORLD_HEIGHT = Constants.APP_HEIGHT;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_NEXT_LEVEL = 1;
	public static final int WORLD_STATE_GAME_OVER = 2;
	public static final Vector2 gravity = new Vector2(0, -12);

	public final Random rand;

	public float heightSoFar;
	public int score;
	public int state;

	private Vector3 startPosition = new Vector3(5.0f, 1.0f, 0.0f);

	private PooledEngine engine;
	private World world;

	public GameWorld(PooledEngine engine, World world) {
		this.engine = engine;
		this.world = world;
		this.rand = new Random();
	}

	public void create() {
		Entity player = createPlayer();
		createCamera(player);
		createBackground();

		createWall(6.0f, 1.0f);
		createWall(7.0f, 1.0f);
		
		this.heightSoFar = 0;
		this.score = 0;
		this.state = WORLD_STATE_RUNNING;
	}

	private Entity createPlayer() {
		Entity entity = engine.createEntity();

		AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		PlayerComponent player = engine.createComponent(PlayerComponent.class);
		BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
		MovementComponent movement = engine.createComponent(MovementComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		StateComponent state = engine.createComponent(StateComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(PlayerComponent.WIDTH / 2, PlayerComponent.HEIGHT / 2);
		BodyComponent body = new BodyComponent(world, BodyType.DynamicBody, shape, startPosition, 9f, 0.5f, 0.5f);
		body.body.setTransform(5f, 0.5f, 0f);
		body.body.setFixedRotation(true);
		body.body.setUserData(entity);
		shape.dispose();

		animation.animations.put(PlayerComponent.STATE_WALK, Assets.warriorWalkAnimation);
		animation.animations.put(PlayerComponent.STATE_HIT, Assets.warriorIdleAnimation);
		animation.animations.put(PlayerComponent.STATE_IDLE, Assets.warriorIdleAnimation);

		bounds.bounds.width = PlayerComponent.WIDTH;
		bounds.bounds.height = PlayerComponent.HEIGHT;

		position.pos.set(startPosition);

		state.set(PlayerComponent.STATE_IDLE);
		
		entity.add(body);
		entity.add(animation);
		entity.add(player);
		entity.add(bounds);
		entity.add(movement);
		entity.add(position);
		entity.add(state);
		entity.add(texture);

		engine.addEntity(entity);

		return entity;
	}

	private void createCamera(Entity target) {
		Entity entity = engine.createEntity();

		CameraComponent camera = new CameraComponent();
		camera.camera = engine.getSystem(RenderingSystem.class).getCamera();
		camera.camera.position.set(startPosition);
		camera.target = target;

		entity.add(camera);

		engine.addEntity(entity);
	}

	private void createBackground() {
		Entity entity = engine.createEntity();

		BackgroundComponent background = engine.createComponent(BackgroundComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);

		texture.region = Assets.backgroundRegion;

		entity.add(background);
		entity.add(position);
		entity.add(texture);

		engine.addEntity(entity);
	}

	private void createWall(float x, float y) {
		Entity entity = engine.createEntity();

		WallComponent wall = engine.createComponent(WallComponent.class);
		BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(WallComponent.WIDTH, WallComponent.HEIGHT);
		BodyComponent body = new BodyComponent(world, BodyType.StaticBody, shape, startPosition, 9f, 0.5f, 0.5f);
		body.body.setTransform(x, y, 0f);
		body.body.setFixedRotation(true);
		body.body.setUserData(entity);
		shape.dispose();
		
		bounds.bounds.width = WallComponent.WIDTH;
		bounds.bounds.height = WallComponent.HEIGHT;

		position.pos.set(x, y, 2.0f);

		texture.region = Assets.dungeonWall1;
		
		entity.add(body);
		entity.add(wall);
		entity.add(bounds);
		entity.add(position);
		entity.add(texture);

		engine.addEntity(entity);
	}

}
