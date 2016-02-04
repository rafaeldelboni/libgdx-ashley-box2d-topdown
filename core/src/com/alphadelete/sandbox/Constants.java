package com.alphadelete.sandbox;

public class Constants {
	
	public static enum TileTyle {
		WallLeft,
		WallRight,
		WallDown,
		WallUp,
		WallUp2,
		Door,
		DoorUp,
		Floor,
		None
	}
	
	public static final int APP_WIDTH = 800;
    public static final int APP_HEIGHT = 480;
    
    public static final int GAME_RUNNING = 1;
    public static final int GAME_PAUSED = 2;
    public static final int GAME_LEVEL_END = 3;
    public static final int GAME_OVER = 4;
    public static final boolean GAME_DEBUG = true;
    
    public static final float CAMERA_SMOOTH = 2f;
    
    public static final float SCALE_RIGHT = -1.0f;
    public static final float SCALE_LEFT = 1.0f;
    
    public static final double TILE_WIDTH = 16;
    public static final double TILE_HEIGHT = 16;
    
    public static final float PIXELS_TO_METRES = 1f / 16f;
    
}
