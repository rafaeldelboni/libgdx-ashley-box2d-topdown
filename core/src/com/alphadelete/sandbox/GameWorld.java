package com.alphadelete.sandbox;

import com.alphadelete.sandbox.components.AnimationComponent;
import com.alphadelete.sandbox.components.AttackComponent;
import com.alphadelete.sandbox.components.BackgroundComponent;
import com.alphadelete.sandbox.components.BodyComponent;
import com.alphadelete.sandbox.components.CameraComponent;
import com.alphadelete.sandbox.components.EffectsComponent;
import com.alphadelete.sandbox.components.EnemyComponent;
import com.alphadelete.sandbox.components.MovementComponent;
import com.alphadelete.sandbox.components.PlayerComponent;
import com.alphadelete.sandbox.components.StateComponent;
import com.alphadelete.sandbox.components.TextureComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.alphadelete.sandbox.components.WallComponent;
import com.alphadelete.sandbox.components.WeaponComponent;
import com.alphadelete.sandbox.systems.EnemySystem;
import com.alphadelete.sandbox.systems.RenderingSystem;
import com.alphadelete.utils.astar.AStarMap;
import com.alphadelete.sandbox.map.Level;
import com.alphadelete.sandbox.map.Tile;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class GameWorld {
	public static final float WORLD_WIDTH = Constants.APP_WIDTH;
	public static final float WORLD_HEIGHT = Constants.APP_HEIGHT;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_NEXT_LEVEL = 1;
	public static final int WORLD_STATE_GAME_OVER = 2;
	public static final Vector2 gravity = new Vector2(0, -12);

	public int score;
	public int state;

	private PooledEngine engine;
	private World world;
	private Long seed;
	
	private AStarMap aStarMap;
	
	public GameWorld(PooledEngine engine, World world, long seed) {
		this.engine = engine;
		this.world = world;
		this.seed = seed;
		// create map for A* path finding
		this.aStarMap = new AStarMap((int)Constants.MAP_WIDTH, (int)Constants.MAP_HEIGHT);
	}
	
	public PooledEngine getEngine() {
		return this.engine;
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	public AStarMap getAStarMap() {
		return this.aStarMap;
	}

	public void create() {
		
		 world.setContactListener(new ContactListener() {
	            @Override
	            public void beginContact(Contact contact) {
					Fixture fixA = contact.getFixtureA();
					Fixture fixB = contact.getFixtureB();
					
					if (fixA.getFilterData().categoryBits == BodyComponent.CATEGORY_PLAYER_ATTACK &&
						fixB.getFilterData().categoryBits == BodyComponent.CATEGORY_MONSTER ) {

						Entity attack = (Entity) fixA.getBody().getUserData();		
						Entity attacked = (Entity) fixB.getBody().getUserData();
						
						doAttack(attack, attacked);
					}
					if (fixA.getFilterData().categoryBits == BodyComponent.CATEGORY_MONSTER &&
						fixB.getFilterData().categoryBits == BodyComponent.CATEGORY_PLAYER_ATTACK ) {

						Entity attacked = (Entity) fixA.getBody().getUserData();		
						Entity attack = (Entity) fixB.getBody().getUserData();
						
						doAttack(attack, attacked);
					}
				}

	            @Override
	            public void endContact(Contact contact) {
	            }

	            @Override
	            public void preSolve(Contact contact, Manifold oldManifold) {
	            }

	            @Override
	            public void postSolve(Contact contact, ContactImpulse impulse) {
	            }
	        }
		 );

		createBackground();
		
		Level dungeon = new Level(this.seed);
		this.seed = dungeon.getSeed();
				
		//ArrayMap<Vector2, Tile> tileMap = dungeon.generateDungeon();
		ArrayMap<Vector2, Tile> tileMap = dungeon.generateTestRoom();
		for(Entry<Vector2, Tile> map : tileMap.entries()) {
			
			Vector2 coord = map.key;

			if (map.value.type == Tile.TileType.Floor || 
				map.value.type == Tile.TileType.Corridor ||
				map.value.type == Tile.TileType.WallBase ||
				map.value.type == Tile.TileType.WallCornerLeft ||
				map.value.type == Tile.TileType.WallCornerRight ||
				map.value.type == Tile.TileType.WallCornerDouble ||
				map.value.type == Tile.TileType.WallEnter ||
				map.value.type == Tile.TileType.WallExit ) {
				createFloor(coord.x, coord.y, 5, map.value.texture);
			} else if (map.value.type == Tile.TileType.CeilingDown || 
				map.value.type == Tile.TileType.CeilingLeftDown ||
				map.value.type == Tile.TileType.CeilingRightDown ||
				map.value.type == Tile.TileType.CeilingUpDown ||
				map.value.type == Tile.TileType.CeilingUDown ||
				map.value.type == Tile.TileType.CeilingULeft ||
				map.value.type == Tile.TileType.CeilingURight ) {
				createFloor(coord.x, coord.y, -5, map.value.texture);
			} 
			else if (map.value.type == Tile.TileType.WallBaseUp || 
				map.value.type == Tile.TileType.WallCornerDoubleUp ||
				map.value.type == Tile.TileType.WallCornerLeftUp ||
				map.value.type == Tile.TileType.WallCornerRightUp ||
				map.value.type == Tile.TileType.WallEnterUp ||
				map.value.type == Tile.TileType.WallExitUp ) {
				createWall(coord.x, coord.y, 5, map.value.texture);
				this.aStarMap.getNodeAt((int)coord.x, (int)coord.y).isWall = true;
				} 
			else {
				createWall(coord.x, coord.y, -5, map.value.texture);
				this.aStarMap.getNodeAt((int)coord.x, (int)coord.y).isWall = true;
			}
		}
		
		for(Entry<Vector2, Tile> map : tileMap.entries()) {
			if (map.value.type == Tile.TileType.WallEnter ) {
				Entity player = createPlayer(map.key.x, map.key.y-1);
				createCamera(player, map.key.x, map.key.y-1);
				break;
			}
		}
		
		//Entity player = createPlayer(0, 0);
		//createCamera(player, 0, 0);
		createEnemy(12, 2);
		createEnemy(14, 2);
		
		this.score = 0;
		this.state = WORLD_STATE_RUNNING;
	}

	private Entity createPlayer(float x, float y) {
		Entity entity = engine.createEntity();

		AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		PlayerComponent player = engine.createComponent(PlayerComponent.class);
		MovementComponent movement = engine.createComponent(MovementComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		StateComponent state = engine.createComponent(StateComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		
		WeaponComponent weapon = new WeaponComponent(
			engine, 
			new Vector3(x, y, 0f), 
			Assets.warriorWeapon1, 
			WeaponComponent.TYPE_PLAYER
		);
		
		
		CircleShape shape = new CircleShape();
		shape.setRadius(PlayerComponent.WIDTH / 2);
		BodyComponent body = new BodyComponent(
			entity,
			world, 
			BodyType.DynamicBody, 
			shape, 
			new Vector3 (x, y , 1), 
			9f, 0.5f, 0.5f, 
			false,
			BodyComponent.CATEGORY_PLAYER,
			BodyComponent.MASK_PLAYER
		);
		shape.dispose();

		animation.animations.put(PlayerComponent.STATE_WALK, Assets.warriorWalkAnimation);
		animation.animations.put(PlayerComponent.STATE_HIT, Assets.warriorIdleAnimation);
		animation.animations.put(PlayerComponent.STATE_IDLE, Assets.warriorIdleAnimation);

		position.setPosition(body.body.getPosition().x, body.body.getPosition().y, 1f);

		state.set(PlayerComponent.STATE_IDLE);
		state.frameRate = 0.1f;
		
		entity.add(body);
		entity.add(animation);
		entity.add(player);
		entity.add(movement);
		entity.add(position);
		entity.add(state);
		entity.add(texture);
		entity.add(weapon);

		engine.addEntity(entity);

		return entity;
	}

	private void createCamera(Entity target, float x, float y) {
		Entity entity = engine.createEntity();

		CameraComponent camera = new CameraComponent();
		camera.camera = engine.getSystem(RenderingSystem.class).getCamera();
		camera.camera.position.set(new Vector3 (x, y , 0));
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
	
	private void createFloor(float x, float y, float z, TextureRegion assetTexture) {
		Entity entity = engine.createEntity();

		WallComponent wall = engine.createComponent(WallComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);

		position.pos.set(x, y, z);
		texture.region = assetTexture;
		
		entity.add(wall);
		entity.add(position);
		entity.add(texture);

		engine.addEntity(entity);
	}

	private void createWall(float x, float y, float z, TextureRegion assetTexture) {
		Entity entity = engine.createEntity();

		WallComponent wall = engine.createComponent(WallComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(WallComponent.WIDTH / 2, WallComponent.HEIGHT / 2);
		BodyComponent body = new BodyComponent(
			entity,
			world, 
			BodyType.StaticBody, 
			shape, 
			new Vector3(x, y, z), 
			9f, 0.5f, 0.5f, 
			false,
			BodyComponent.CATEGORY_SCENERY,
			BodyComponent.MASK_SCENERY
		);
		body.body.setTransform(x, y, 0f);
		body.body.setFixedRotation(true);
		body.body.setUserData(entity);
		shape.dispose();
		
		position.pos.set(x, y, z);

		texture.region = assetTexture;
		
		entity.add(body);
		entity.add(wall);
		entity.add(position);
		entity.add(texture);

		engine.addEntity(entity);
	}
	
	private Entity createEnemy(float x, float y) {
		Entity entity = engine.createEntity();

		AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		EnemyComponent enemy = engine.createComponent(EnemyComponent.class);
		MovementComponent movement = engine.createComponent(MovementComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		StateComponent state = engine.createComponent(StateComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(EnemyComponent.WIDTH / 2, EnemyComponent.HEIGHT / 2);
		BodyComponent body = new BodyComponent(
			entity,
			world, 
			BodyType.DynamicBody, 
			shape, 
			new Vector3(x,y,2), 
			9f, 0.5f, 0.5f, 
			false,
			BodyComponent.CATEGORY_MONSTER,
			BodyComponent.MASK_MONSTER
		);
		body.body.setLinearDamping(1f);
		body.body.setFixedRotation(true);
		body.body.setUserData(entity);
		shape.dispose();
		
		animation.animations.put(EnemyComponent.STATE_IDLE, Assets.goblinIdleAnimation);
		animation.animations.put(EnemyComponent.STATE_WALK, Assets.goblinWalkAnimation);
		animation.animations.put(EnemyComponent.STATE_DIE, Assets.goblinDieAnimation);

		enemy.health = 3;
		
		position.setPosition(body.body.getPosition().x, body.body.getPosition().y, 2);

		state.set(EnemyComponent.STATE_IDLE);
		state.frameRate = 0.1f;
		
		entity.add(body);
		entity.add(animation);
		entity.add(enemy);
		entity.add(movement);
		entity.add(position);
		entity.add(state);
		entity.add(texture);

		engine.addEntity(entity);

		return entity;
	}
	
	public void createPlayerAttack(float attackX, float attackY, float angle) {
		
		Entity entity = engine.createEntity();
		
		StateComponent state = engine.createComponent(StateComponent.class);
		AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		EffectsComponent effect = new EffectsComponent(150);
		AttackComponent attack = new AttackComponent(1);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(2f * 0.65f, 2f * 0.65f);
		BodyComponent body = new BodyComponent(
			entity,
			world, 
			BodyType.DynamicBody, 
			shape, 
			new Vector3(attackX, attackY, 0.9f),
			BodyComponent.CATEGORY_PLAYER_ATTACK,
			BodyComponent.MASK_PLAYER_ATTACK
		);
		body.body.setLinearDamping(1f);
		body.body.setFixedRotation(true);
		body.body.setUserData(entity);
		shape.dispose();

		position.scale.set(0.65f, 0.65f);
		position.rotation = angle;
		position.pos.set(attackX, attackY, 0.9f);
		animation.animations.put(0, Assets.attackEffect);
		state.set(0);
		
		entity.add(body);
		entity.add(state);
		entity.add(animation);
		entity.add(position);
		entity.add(texture);
		entity.add(effect);
		entity.add(attack);
		
		engine.addEntity(entity);	
	}
	
	public void destroyBody(Body body) {
		if(!world.isLocked()) {
		     world.destroyBody(body);
		}
	}
	
	private void doAttack (Entity attack, Entity attacked) {
		ComponentMapper<TransformComponent> ap = ComponentMapper.getFor(TransformComponent.class);
		ComponentMapper<AttackComponent> aa = ComponentMapper.getFor(AttackComponent.class);
		TransformComponent attackPos = ap.get(attack);
		AttackComponent attackCom = aa.get(attack);
		
		EnemySystem enemySystem = engine.getSystem(EnemySystem.class);
		enemySystem.takeDamage(attacked, attackPos.pos.x, attackPos.pos.y, attackCom.damage);
	}
}
