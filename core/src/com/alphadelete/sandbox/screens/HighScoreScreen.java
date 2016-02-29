package com.alphadelete.sandbox.screens;

import com.alphadelete.sandbox.Assets;
import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.Sandbox;
import com.alphadelete.sandbox.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class HighScoreScreen extends ScreenAdapter {
	Sandbox game;
	OrthographicCamera guiCam;
	Rectangle backBounds;
	Vector3 touchPoint;
	String[] highScores;
	float xOffset = 0;
	private GlyphLayout layout = new GlyphLayout();

	public HighScoreScreen (Sandbox game) {
		this.game = game;

		guiCam = new OrthographicCamera(Constants.APP_WIDTH, Constants.APP_HEIGHT);
		guiCam.position.set(Constants.APP_WIDTH / 2, Constants.APP_HEIGHT / 2, 0);
		
		backBounds = new Rectangle(0, 0, 64, 64);
		touchPoint = new Vector3();
		highScores = new String[5];
		for (int i = 0; i < 5; i++) {
			highScores[i] = i + 1 + ". " + Settings.highscores[i];
			layout.setText(Assets.font, highScores[i]);
			xOffset = Math.max(layout.width, xOffset);
		}
		xOffset = (Constants.APP_WIDTH / 2) - xOffset / 2 + Assets.font.getSpaceWidth() / 2;
	}

	public void update () {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (backBounds.contains(touchPoint.x, touchPoint.y)) {
				game.setScreen(new MainMenuScreen(game));
				return;
			}
		}
	}

	public void draw () {
		GL20 gl = Gdx.gl;
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		guiCam.update();

		game.batcher.setProjectionMatrix(guiCam.combined);
		game.batcher.disableBlending();
		game.batcher.begin();
		game.batcher.draw(Assets.backgroundRegion, 0, 0, Constants.APP_WIDTH, Constants.APP_HEIGHT);
		game.batcher.end();

		game.batcher.enableBlending();
		game.batcher.begin();
		game.batcher.draw(Assets.menuHighScore, Constants.APP_WIDTH / 2 - 300 / 2, Constants.APP_HEIGHT - 100, 300, 33);

		float y = Constants.APP_HEIGHT - 250;
		for (int i = 4; i >= 0; i--) {
			Assets.font.draw(game.batcher, highScores[i], xOffset, y);
			y += Assets.font.getLineHeight();
		}

		game.batcher.draw(Assets.buttonBack, 0, 0, 64, 64);
		game.batcher.end();
	}

	@Override
	public void render (float delta) {
		update();
		draw();
	}
}
