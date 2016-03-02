package com.alphadelete.sandbox.screens;

import java.util.Random;

import com.alphadelete.sandbox.Assets;
import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.Sandbox;
import com.alphadelete.sandbox.Settings;
import com.alphadelete.sandbox.components.AttackComponent;
import com.alphadelete.sandbox.components.BodyComponent;
import com.alphadelete.sandbox.components.PlayerComponent;
import com.alphadelete.sandbox.components.StateComponent;
import com.alphadelete.sandbox.components.TransformComponent;
import com.alphadelete.sandbox.GameWorld;
import com.alphadelete.sandbox.systems.AnimationSystem;
import com.alphadelete.sandbox.systems.BackgroundSystem;
import com.alphadelete.sandbox.systems.CameraSystem;
import com.alphadelete.sandbox.systems.ControllerSystem;
import com.alphadelete.sandbox.systems.EffectsSystem;
import com.alphadelete.sandbox.systems.EnemySystem;
import com.alphadelete.sandbox.systems.MovementSystem;
import com.alphadelete.sandbox.systems.PlayerSystem;
import com.alphadelete.sandbox.systems.RenderingSystem;
import com.alphadelete.sandbox.systems.StateSystem;
import com.alphadelete.sandbox.systems.WeaponSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class GameScreen extends ScreenAdapter {
	Sandbox game;

	OrthographicCamera guiCam;
	Vector3 touchPoint;
	GameWorld gameWorld;
	Rectangle pauseBounds;
	Rectangle resumeBounds;
	Rectangle quitBounds;
	String scoreString;
	String p1LifeString;
	long p1Life;
	long lastScore = 0;
	
	private GlyphLayout layout = new GlyphLayout();

	private int state;
	private long seed;

	public GameScreen(Sandbox game) {
		startGame(game);
	}
	public void startGame (Sandbox game){
		try {
 			this.seed = new Random().nextLong();
			startGame(game, this.seed);
		} catch (Exception ex){
			Constants.LOG_FILE.writeString("Error: " + ex.toString(), true);
		}
	}
	public void startGame (final Sandbox game, long seed){
		this.game = game;

		state = Constants.GAME_RUNNING;
		guiCam = new OrthographicCamera(Constants.APP_WIDTH, Constants.APP_HEIGHT);
		guiCam.position.set(Constants.APP_WIDTH / 2, Constants.APP_HEIGHT / 2, 0);
		touchPoint = new Vector3();

		gameWorld = new GameWorld(new PooledEngine(), new World(Vector2.Zero,true), seed, lastScore);

		gameWorld.getEngine().addSystem(new PlayerSystem(gameWorld));
		gameWorld.getEngine().addSystem(new EnemySystem(gameWorld));
		gameWorld.getEngine().addSystem(new CameraSystem());
		gameWorld.getEngine().addSystem(new BackgroundSystem());
		gameWorld.getEngine().addSystem(new MovementSystem());
		gameWorld.getEngine().addSystem(new StateSystem());
		gameWorld.getEngine().addSystem(new AnimationSystem());
		gameWorld.getEngine().addSystem(new EffectsSystem(gameWorld));
		gameWorld.getEngine().addSystem(new WeaponSystem());
		gameWorld.getEngine().addSystem(new ControllerSystem());
		gameWorld.getEngine().addSystem(new RenderingSystem(game.batcher, gameWorld));

		gameWorld.getEngine().getSystem(BackgroundSystem.class).setCamera(gameWorld.getEngine().getSystem(RenderingSystem.class).getCamera());
		gameWorld.getEngine().getSystem(RenderingSystem.class).resizeCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		gameWorld.create();
		
		gameWorld.getWorld().setContactListener(new ContactListener() {
	            @Override
	            public void beginContact(Contact contact) {
					Fixture fixA = contact.getFixtureA();
					Fixture fixB = contact.getFixtureB();
					// Player attack
					if (fixA.getFilterData().categoryBits == BodyComponent.CATEGORY_PLAYER_ATTACK &&
						fixB.getFilterData().categoryBits == BodyComponent.CATEGORY_MONSTER ) {

						Entity attack = (Entity) fixA.getBody().getUserData();		
						Entity attacked = (Entity) fixB.getBody().getUserData();
						
						doAttack(attack, attacked, Constants.TYPE_PLAYER);
					}
					if (fixA.getFilterData().categoryBits == BodyComponent.CATEGORY_MONSTER &&
						fixB.getFilterData().categoryBits == BodyComponent.CATEGORY_PLAYER_ATTACK ) {

						Entity attacked = (Entity) fixA.getBody().getUserData();		
						Entity attack = (Entity) fixB.getBody().getUserData();
						
						doAttack(attack, attacked, Constants.TYPE_PLAYER);
					}
					// Enemy Attack
					if (fixA.getFilterData().categoryBits == BodyComponent.CATEGORY_MONSTER_ATTACK &&
						fixB.getFilterData().categoryBits == BodyComponent.CATEGORY_PLAYER ) {

						Entity attack = (Entity) fixA.getBody().getUserData();		
						Entity attacked = (Entity) fixB.getBody().getUserData();
						
						doAttack(attack, attacked, Constants.TYPE_ENEMY);
					}
					if (fixA.getFilterData().categoryBits == BodyComponent.CATEGORY_PLAYER &&
						fixB.getFilterData().categoryBits == BodyComponent.CATEGORY_MONSTER_ATTACK ) {

						Entity attacked = (Entity) fixA.getBody().getUserData();		
						Entity attack = (Entity) fixB.getBody().getUserData();
						
						doAttack(attack, attacked, Constants.TYPE_ENEMY);
					}
					// Player on exit
					if (fixA.getFilterData().categoryBits == BodyComponent.CATEGORY_EXIT &&
						fixB.getFilterData().categoryBits == BodyComponent.CATEGORY_PLAYER ) {
						
						if (gameWorld.enemies < 1) {
							long oldP1Life = p1Life;
							startGame(game);
							setFirstPlayerLife(oldP1Life);
						}
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

		pauseBounds = new Rectangle(Constants.APP_WIDTH - 64, Constants.APP_HEIGHT - 64, 64, 64);
		resumeBounds = new Rectangle(Constants.APP_WIDTH / 2 - 192 / 2, Constants.APP_HEIGHT / 2, 192, 36);
		quitBounds = new Rectangle(Constants.APP_WIDTH / 2 - 192 / 2, Constants.APP_HEIGHT / 2 - 36, 192, 36);
		scoreString = "SCORE: ";
		
		p1Life = getFirstPlayerLife();
		p1LifeString = "Life: ";
		
		pauseSystems();
	}

	public void update(float deltaTime) {
		if (deltaTime > 0.1f)
			deltaTime = 0.1f;

		gameWorld.getEngine().update(deltaTime);
		
		switch (state) {
		case Constants.GAME_RUNNING:
			resumeSystems();
			updateRunning(deltaTime);
			break;
		case Constants.GAME_PAUSED:
			updatePaused();
			break;
		case Constants.GAME_LEVEL_END:
			updateLevelEnd();
			break;
		case Constants.GAME_OVER:
			updateGameOver();
			break;
		}
	}

	private void updateRunning(float deltaTime) {
		gameWorld.getWorld().step(deltaTime,6,2);
		gameWorld.getWorld().clearForces();
		
		p1Life = getFirstPlayerLife();
				
		boolean isAttacking = false;
		Vector3 targetPos = new Vector3();
		
		OrthographicCamera camera = gameWorld.getEngine().getSystem(RenderingSystem.class).getCamera();
		camera.unproject(targetPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (pauseBounds.contains(touchPoint.x, touchPoint.y)) {
				state = Constants.GAME_PAUSED;
				pauseSystems();
				return;
			} else {
				isAttacking = true;
			}
		}

		gameWorld.getEngine().getSystem(ControllerSystem.class).setControls(Gdx.input, isAttacking, targetPos);
		
		// Refresh World for debug
		if (Gdx.input.isKeyPressed(Keys.R)) {
			startGame(this.game, gameWorld.getSeed());
		}
		if (Gdx.input.isKeyPressed(Keys.T)) {
			startGame(this.game);
		}
		
		if (gameWorld.score != lastScore) {
			lastScore = gameWorld.score;
		}
		if (gameWorld.state == GameWorld.WORLD_STATE_GAME_OVER) {
			state = Constants.GAME_OVER;
			if (lastScore >= Settings.highscores[4])
				scoreString = "NEW HIGHSCORE: ";
			else
				scoreString = "SCORE: ";
			pauseSystems();
			Settings.addScore(lastScore);
			Settings.save();
		}
	}

	private void updatePaused() {

		gameWorld.getWorld().clearForces();
		
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (resumeBounds.contains(touchPoint.x, touchPoint.y)) {
				state = Constants.GAME_RUNNING;
				resumeSystems();
				return;
			}

			if (quitBounds.contains(touchPoint.x, touchPoint.y)) {
				game.setScreen(new MainMenuScreen(game));
				return;
			}
		}
	}

	private void updateLevelEnd() {
		if (Gdx.input.justTouched()) {
			gameWorld.getEngine().removeAllEntities();
			this.seed = new Random().nextLong();
			gameWorld = new GameWorld(gameWorld.getEngine(), gameWorld.getWorld(), this.seed, this.lastScore);
			state = Constants.GAME_RUNNING;
		}
	}
	
	private void updateGameOver () {
		if (Gdx.input.justTouched()) {
			game.setScreen(new MainMenuScreen(game));
		}
	}

	public void drawUI() {
		guiCam.update();
		game.batcher.setProjectionMatrix(guiCam.combined);
		game.batcher.begin();
		switch (state) {
		case Constants.GAME_RUNNING:
			presentRunning();
			break;
		case Constants.GAME_PAUSED:
			presentPaused();
			break;
		case Constants.GAME_LEVEL_END:
			presentLevelEnd();
			break;
		case Constants.GAME_OVER:
			presentGameOver();
			break;
		}
		game.batcher.end();
	}

	private void presentRunning() {
		game.batcher.draw(Assets.buttonPause, Constants.APP_WIDTH - 64, Constants.APP_HEIGHT - 64, 64, 64);
		Assets.font.draw(game.batcher, scoreString + lastScore, 16, Constants.APP_HEIGHT - 20);
		Assets.font.draw(game.batcher, p1LifeString + p1Life, 16, Constants.APP_HEIGHT - 40);
	}

	private void presentPaused() {
		game.batcher.draw(Assets.menuPause, Constants.APP_WIDTH / 2 - 192 / 2, Constants.APP_HEIGHT / 2 - 96 / 2, 192,96);
		Assets.font.draw(game.batcher, scoreString + lastScore, 16, Constants.APP_HEIGHT - 20);
		Assets.font.draw(game.batcher, p1LifeString + p1Life, 16, Constants.APP_HEIGHT - 40);
	}

	private void presentLevelEnd() {
		String topText = "the princess is ...";
		String bottomText = "in another castle!";

		layout.setText(Assets.font, topText);
		float topWidth = layout.width;

		layout.setText(Assets.font, bottomText);
		float bottomWidth = layout.width;
		Assets.font.draw(game.batcher, topText, Constants.APP_WIDTH / 2 - topWidth / 2, Constants.APP_HEIGHT - 40);
		Assets.font.draw(game.batcher, bottomText, Constants.APP_WIDTH / 2 - bottomWidth / 2, 40);
	}

	private void presentGameOver() {
		game.batcher.draw(Assets.menuGameOver, Constants.APP_WIDTH / 2 - 160 / 2, Constants.APP_HEIGHT / 2 - 96 / 2, 160, 96);

		layout.setText(Assets.font, scoreString + lastScore);
		float scoreWidth = layout.width;
		Assets.font.draw(game.batcher, scoreString + lastScore, Constants.APP_WIDTH / 2 - scoreWidth / 2, Constants.APP_HEIGHT - 20);
	}

	private void pauseSystems() {
		gameWorld.getEngine().getSystem(PlayerSystem.class).setProcessing(false);
		gameWorld.getEngine().getSystem(EnemySystem.class).setProcessing(false);
		gameWorld.getEngine().getSystem(MovementSystem.class).setProcessing(false);
		gameWorld.getEngine().getSystem(StateSystem.class).setProcessing(false);
		gameWorld.getEngine().getSystem(EffectsSystem.class).setProcessing(false);
		gameWorld.getEngine().getSystem(WeaponSystem.class).setProcessing(false);
		gameWorld.getEngine().getSystem(AnimationSystem.class).setProcessing(false);
		gameWorld.getEngine().getSystem(ControllerSystem.class).setProcessing(false);
	}

	private void resumeSystems() {
		gameWorld.getEngine().getSystem(PlayerSystem.class).setProcessing(true);
		gameWorld.getEngine().getSystem(EnemySystem.class).setProcessing(true);
		gameWorld.getEngine().getSystem(MovementSystem.class).setProcessing(true);
		gameWorld.getEngine().getSystem(StateSystem.class).setProcessing(true);
		gameWorld.getEngine().getSystem(EffectsSystem.class).setProcessing(true);
		gameWorld.getEngine().getSystem(WeaponSystem.class).setProcessing(true);
		gameWorld.getEngine().getSystem(AnimationSystem.class).setProcessing(true);
		gameWorld.getEngine().getSystem(ControllerSystem.class).setProcessing(true);
	}
	
	private long getFirstPlayerLife() {
		long life = 0;
		@SuppressWarnings("unchecked")
		ImmutableArray<Entity> players = gameWorld.getEngine().getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class, StateComponent.class).get());
		// First in the array is the first player?
		if (players.size() > 0) {
			Entity player = players.get(0);
			ComponentMapper<PlayerComponent> pm = ComponentMapper.getFor(PlayerComponent.class);
			PlayerComponent playerComp = pm.get(player);
			life = playerComp.health; 
		}
		return life;
	}
	
	private void setFirstPlayerLife(long life) {
		@SuppressWarnings("unchecked")
		ImmutableArray<Entity> players = gameWorld.getEngine().getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class, StateComponent.class).get());
		// First in the array is the first player?
		Entity player = players.get(0);
		ComponentMapper<PlayerComponent> pm = ComponentMapper.getFor(PlayerComponent.class);
		PlayerComponent playerComp = pm.get(player);
		
		playerComp.health = life;
	}
	
	private void doAttack (Entity attack, Entity attacked, int type) {
		
		ComponentMapper<TransformComponent> ap = ComponentMapper.getFor(TransformComponent.class);
		ComponentMapper<AttackComponent> aa = ComponentMapper.getFor(AttackComponent.class);
		TransformComponent attackPos = ap.get(attack);
		AttackComponent attackCom = aa.get(attack);
		if (type == Constants.TYPE_PLAYER) {
			EnemySystem enemySystem = gameWorld.getEngine().getSystem(EnemySystem.class);
			enemySystem.takeDamage(attacked, attackPos.pos.x, attackPos.pos.y, attackCom.damage);
		} else if (type == Constants.TYPE_ENEMY) {
			PlayerSystem playerSystem = gameWorld.getEngine().getSystem(PlayerSystem.class);
			playerSystem.takeDamage(attacked, attackPos.pos.x, attackPos.pos.y, attackCom.damage);
		}
	}

	@Override
	public void render(float delta) {
		update(delta);
		drawUI();
	}

	@Override
	public void pause() {
		if (state == Constants.GAME_RUNNING) {
			state = Constants.GAME_PAUSED;
			pauseSystems();
		}
	}
	
	@Override
	public void resize(int width, int height) {
		gameWorld.getEngine().getSystem(RenderingSystem.class).resizeCamera(width, height);
	}
	
	@Override
	public void dispose() {
		gameWorld.dispose();
		game.dispose();
	}
}
