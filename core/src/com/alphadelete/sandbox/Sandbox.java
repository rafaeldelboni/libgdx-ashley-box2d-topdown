package com.alphadelete.sandbox;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.alphadelete.sandbox.screens.MainMenuScreen;

public class Sandbox extends Game {
	// used by all screens
	public SpriteBatch batcher;

	@Override
	public void create() {
		batcher = new SpriteBatch();

		Assets.load();
		setScreen(new MainMenuScreen(this));

		if (Constants.GAME_DEBUG) {
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		}
	}

	@Override
	public void render() {
		GL20 gl = Gdx.gl;
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
	}
}