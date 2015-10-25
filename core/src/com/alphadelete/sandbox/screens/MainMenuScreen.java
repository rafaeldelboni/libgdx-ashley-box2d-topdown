package com.alphadelete.sandbox.screens;

import com.alphadelete.sandbox.Assets;
import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.Sandbox;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MainMenuScreen extends ScreenAdapter {
	Sandbox game;
	OrthographicCamera guiCam;
	Rectangle playBounds;
	Vector3 touchPoint;
	
	public MainMenuScreen(Sandbox game) {
		this.game = game;
		
		guiCam = new OrthographicCamera();
		guiCam.setToOrtho(false, Constants.APP_WIDTH, Constants.APP_HEIGHT);
		playBounds = new Rectangle(Constants.APP_WIDTH / 2 - 300 / 2, Constants.APP_HEIGHT / 2 - 110 / 2 + 36 * 2, 300, 36);
		touchPoint = new Vector3();
	}
	
	public void update(){
		// check user input
		if (Gdx.input.isTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			
			if (playBounds.contains(touchPoint.x, touchPoint.y)) {
				game.setScreen(new GameScreen(game));
				dispose();
			}
		}
	}

	public void draw() {
		GL20 gl = Gdx.gl;
		gl.glClearColor(0, 0, 0.2f, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		guiCam.update();
		game.batcher.setProjectionMatrix(guiCam.combined);
		
		game.batcher.enableBlending();
		game.batcher.begin();
		game.batcher.draw(Assets.menuMain, Constants.APP_WIDTH / 2 - 300 / 2, Constants.APP_HEIGHT / 2 - 110 / 2, 300, 110);
		game.batcher.end();	
	}

	@Override
	public void render (float delta) {
		update();
		draw();
	}

	@Override
	public void pause () {
		
	}

}
