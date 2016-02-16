package com.alphadelete.sandbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Assets {
	public static Texture background;
	public static TextureRegion backgroundRegion;
	
	public static TextureAtlas menuAtlas = new TextureAtlas(Gdx.files.internal("images/menu.atlas"));
	public static TextureAtlas dungeonAtlas = new TextureAtlas(Gdx.files.internal("images/dungeon2.atlas"));
	public static TextureAtlas warriorAtlas = new TextureAtlas(Gdx.files.internal("images/warrior2.atlas"));
	public static TextureAtlas goblinAtlas = new TextureAtlas(Gdx.files.internal("images/goblin2.atlas"));
	public static TextureAtlas attackAtlas = new TextureAtlas(Gdx.files.internal("images/atk_slash.atlas"));
	public static TextureRegion menuMain;
	public static TextureRegion menuPause;
	public static TextureRegion menuReady;
	public static TextureRegion menuGameOver;
	public static TextureRegion buttonPause;
	public static BitmapFont font;
	public static Animation warriorIdleAnimation;
	public static Animation warriorWalkAnimation;
	public static Animation goblinIdleAnimation;
	public static Animation goblinWalkAnimation;
	public static Animation goblinDieAnimation;
	public static Animation attackEffect;
	public static TextureRegion warriorWeapon1;
	
	public static Texture items;
	public static Animation bobJump;
	
	public static void load () {
		loadMenu();
		loadWarrior();
		loadGoblin();
		loadEffects();
	}	

	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}
		
	public static Animation loadAtlasAnimations(String animationName, TextureAtlas atlas, PlayMode playmode, Vector2 size ) {
		TextureRegion r = atlas.findRegion(animationName);
		TextureRegion[][] grid = r.split((int)size.x, (int)size.y);
		Animation a = new Animation(0.08f, grid[0]);
		a.setPlayMode(playmode);
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
		warriorIdleAnimation = Assets.loadAtlasAnimations("warrior-idle-1", Assets.warriorAtlas, PlayMode.LOOP, new Vector2(64,64));
		warriorWalkAnimation = Assets.loadAtlasAnimations("warrior-walk", Assets.warriorAtlas, PlayMode.LOOP, new Vector2(64,64));
		warriorWeapon1 = Assets.loadAtlasTextureRegion("warrior-arm-1", Assets.warriorAtlas);
	}
	
	private static void loadGoblin () {
		goblinIdleAnimation = Assets.loadAtlasAnimations("goblin-idle-1", Assets.goblinAtlas, PlayMode.LOOP, new Vector2(64,64));
		goblinWalkAnimation = Assets.loadAtlasAnimations("goblin-walk", Assets.goblinAtlas, PlayMode.LOOP, new Vector2(64,64));
		goblinDieAnimation = Assets.loadAtlasAnimations("goblin-dying", Assets.goblinAtlas, PlayMode.NORMAL, new Vector2(64,64));
	}
	
	private static void loadEffects ()	{
		attackEffect = Assets.loadAtlasAnimations("attack_slash", Assets.attackAtlas, PlayMode.REVERSED, new Vector2(70,90));
	}

}
