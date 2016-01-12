package com.alphadelete.sandbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	public static Texture background;
	public static TextureRegion backgroundRegion;
	
	public static TextureAtlas menuAtlas = new TextureAtlas(Gdx.files.internal("images/menu.atlas"));
	public static TextureAtlas dungeonAtlas = new TextureAtlas(Gdx.files.internal("images/dungeon.atlas"));
	public static TextureAtlas warriorAtlas = new TextureAtlas(Gdx.files.internal("images/warrior.atlas"));
	public static TextureRegion attackEffect;
	public static TextureRegion menuMain;
	public static TextureRegion menuPause;
	public static TextureRegion menuReady;
	public static TextureRegion menuGameOver;
	public static TextureRegion buttonPause;
	public static BitmapFont font;
	public static Animation warriorIdleAnimation;
	public static Animation warriorWalkAnimation;
	public static TextureRegion dungeonWall1;
	
	public static Texture items;
	public static Animation bobJump;
	
	public static void load () {
		loadMenu();
		loadWarrior();
		loadDungeon();
		loadEffects();
	}	

	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}
		
	public static Animation loadAtlasAnimations(String animationName, TextureAtlas atlas) {
		TextureRegion r = atlas.findRegion(animationName);
		TextureRegion[][] grid = r.split(32, 32);
		Animation a = new Animation(0.08f, grid[0]);
		a.setPlayMode(PlayMode.LOOP);
		return a;
	}
	
	public static TextureRegion loadAtlasTextureRegion(String animationName, TextureAtlas atlas) {
		TextureRegion r = atlas.findRegion(animationName);
		return r;
	}
	
	private static void loadMenu () {
		menuMain = loadAtlasTextureRegion("main", menuAtlas);
		menuPause = loadAtlasTextureRegion("pause", menuAtlas);
		menuReady = loadAtlasTextureRegion("ready", menuAtlas);
		menuGameOver = loadAtlasTextureRegion("gameover", menuAtlas);
		buttonPause = loadAtlasTextureRegion("pause-btn", menuAtlas);
		font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"), Gdx.files.internal("fonts/font.png"), false);
		
		background = loadTexture("images/background.png");
		backgroundRegion = new TextureRegion(background, 0, 0, 800, 480);
	}
	
	private static void loadWarrior () {
		warriorIdleAnimation = Assets.loadAtlasAnimations("warrior-idle-1", Assets.warriorAtlas);
		warriorWalkAnimation = Assets.loadAtlasAnimations("warrior-walk", Assets.warriorAtlas);
	}
	
	private static void loadDungeon () {
		dungeonWall1 = Assets.loadAtlasTextureRegion("wall-1", Assets.dungeonAtlas);
	}
	
	private static void loadEffects ()	{
		attackEffect = new TextureRegion(loadTexture("images/circular.png"), 0, 0, 366, 366);
	}

}
