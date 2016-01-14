package com.alphadelete.sandbox.screens;

import com.alphadelete.sandbox.Assets;
import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.Sandbox;
import com.alphadelete.sandbox.GameWorld;
import com.alphadelete.sandbox.systems.AnimationSystem;
import com.alphadelete.sandbox.systems.BackgroundSystem;
import com.alphadelete.sandbox.systems.BoundsSystem;
import com.alphadelete.sandbox.systems.CameraSystem;
import com.alphadelete.sandbox.systems.EffectsSystem;
import com.alphadelete.sandbox.systems.MovementSystem;
import com.alphadelete.sandbox.systems.PlayerSystem;
import com.alphadelete.sandbox.systems.RenderingSystem;
import com.alphadelete.sandbox.systems.StateSystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

public class GameScreen extends ScreenAdapter {
	final Sandbox game;

	OrthographicCamera guiCam;
	Vector3 touchPoint;
	GameWorld gameWorld;
	Rectangle pauseBounds;
	Rectangle resumeBounds;
	Rectangle quitBounds;
	String scoreString;

	PooledEngine engine;
	World world;
	
	private GlyphLayout layout = new GlyphLayout();

	private int state;

	Animation walkAnimation;
	TextureRegion currentFrame;
	SpriteBatch spriteBatch;
	float stateTime;

	public GameScreen(Sandbox game) {
		this.game = game;

		state = Constants.GAME_READY;
		guiCam = new OrthographicCamera(Constants.APP_WIDTH, Constants.APP_HEIGHT);
		guiCam.position.set(Constants.APP_WIDTH / 2, Constants.APP_HEIGHT / 2, 0);
		touchPoint = new Vector3();

		engine = new PooledEngine();
		world = new World(Vector2.Zero,true);
				
		gameWorld = new GameWorld(engine, world);

		engine.addSystem(new PlayerSystem());
		engine.addSystem(new CameraSystem());
		engine.addSystem(new BackgroundSystem());
		engine.addSystem(new MovementSystem());
		engine.addSystem(new BoundsSystem());
		engine.addSystem(new StateSystem());
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new EffectsSystem());
		engine.addSystem(new RenderingSystem(game.batcher, world));

		engine.getSystem(BackgroundSystem.class).setCamera(engine.getSystem(RenderingSystem.class).getCamera());

		gameWorld.create();

		pauseBounds = new Rectangle(Constants.APP_WIDTH - 64, Constants.APP_HEIGHT - 64, 64, 64);
		resumeBounds = new Rectangle(Constants.APP_WIDTH / 2 - 192 / 2, Constants.APP_HEIGHT / 2, 192, 36);
		quitBounds = new Rectangle(Constants.APP_WIDTH / 2 - 192 / 2, Constants.APP_HEIGHT / 2 - 36, 192, 36);
		scoreString = "SCORE: 0";

		pauseSystems();
	}

	public void update(float deltaTime) {
		if (deltaTime > 0.1f)
			deltaTime = 0.1f;

		engine.update(deltaTime);
		world.step(deltaTime,6,2);
		world.clearForces();
		
		switch (state) {
		case Constants.GAME_READY:
			updateReady();
			break;
		case Constants.GAME_RUNNING:
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

	private void updateReady() {
		if (Gdx.input.justTouched()) {
			state = Constants.GAME_RUNNING;
			resumeSystems();
		}
	}

	private void updateRunning(float deltaTime) {
		boolean attack = false;
		Vector3 attackPos = new Vector3();;
		
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (pauseBounds.contains(touchPoint.x, touchPoint.y)) {
				state = Constants.GAME_PAUSED;
				pauseSystems();
				return;
			} else {
				attack = true;
				OrthographicCamera camera = engine.getSystem(RenderingSystem.class).getCamera();
				camera.unproject(attackPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			}
		}

		float accelX = 0.0f;
		float accelY = 0.0f;

		if (Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) {
			accelX = 5f;
		}
		if (Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) {
			accelX = -5f;
		}
		if (Gdx.input.isKeyPressed(Keys.DPAD_DOWN)) {
			accelY = 5f;
		}
		if (Gdx.input.isKeyPressed(Keys.DPAD_UP)) {
			accelY = -5f;
		}

		engine.getSystem(PlayerSystem.class).setAccelX(accelX);
		engine.getSystem(PlayerSystem.class).setAccelY(accelY);
		engine.getSystem(PlayerSystem.class).setAttack(attack, attackPos.x, attackPos.y);
		
	}

	private void updatePaused() {
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
			engine.removeAllEntities();
			gameWorld = new GameWorld(engine, world);
			state = Constants.GAME_READY;
		}
	}

	private void updateGameOver() {
		if (Gdx.input.justTouched()) {
			game.setScreen(new MainMenuScreen(game));
		}
	}

	public void drawUI() {
		guiCam.update();
		game.batcher.setProjectionMatrix(guiCam.combined);
		game.batcher.begin();
		switch (state) {
		case Constants.GAME_READY:
			presentReady();
			break;
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

	private void presentReady() {
		game.batcher.draw(Assets.menuReady, Constants.APP_WIDTH / 2 - 192 / 2, Constants.APP_HEIGHT / 2 - 32 / 2, 192, 32);
	}

	private void presentRunning() {
		game.batcher.draw(Assets.buttonPause, Constants.APP_WIDTH - 64, Constants.APP_HEIGHT - 64, 64, 64);
		Assets.font.draw(game.batcher, scoreString, 16, Constants.APP_HEIGHT - 20);

	}

	private void presentPaused() {
		game.batcher.draw(Assets.menuPause, Constants.APP_WIDTH / 2 - 192 / 2, Constants.APP_HEIGHT / 2 - 96 / 2, 192,96);
		Assets.font.draw(game.batcher, scoreString, 16, Constants.APP_HEIGHT - 20);
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

		layout.setText(Assets.font, scoreString);
		float scoreWidth = layout.width;
		Assets.font.draw(game.batcher, scoreString, Constants.APP_WIDTH / 2 - scoreWidth / 2, Constants.APP_HEIGHT - 20);
	}

	private void pauseSystems() {
		engine.getSystem(PlayerSystem.class).setProcessing(false);
		engine.getSystem(MovementSystem.class).setProcessing(false);
		engine.getSystem(BoundsSystem.class).setProcessing(false);
		engine.getSystem(StateSystem.class).setProcessing(false);
		engine.getSystem(EffectsSystem.class).setProcessing(false);
		engine.getSystem(AnimationSystem.class).setProcessing(false);
	}

	private void resumeSystems() {
		engine.getSystem(PlayerSystem.class).setProcessing(true);
		engine.getSystem(MovementSystem.class).setProcessing(true);
		engine.getSystem(BoundsSystem.class).setProcessing(true);
		engine.getSystem(StateSystem.class).setProcessing(true);
		engine.getSystem(EffectsSystem.class).setProcessing(true);
		engine.getSystem(AnimationSystem.class).setProcessing(true);
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
}
