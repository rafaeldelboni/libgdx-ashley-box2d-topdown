package com.alphadelete.sandbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Constants {

	public static final int APP_WIDTH = 800;
    public static final int APP_HEIGHT = 480;
    
    public static final int GAME_RUNNING = 1;
    public static final int GAME_PAUSED = 2;
    public static final int GAME_LEVEL_END = 3;
    public static final int GAME_OVER = 4;
    public static final boolean GAME_DEBUG = false;
    
    public static final float CAMERA_SMOOTH = 2f;
    public static final float CAMERA_ZOOM = 0.5f;
    
    public static final float SCALE_RIGHT = -1.0f;
    public static final float SCALE_LEFT = 1.0f;
    
    public static final double TILE_WIDTH = 16;
    public static final double TILE_HEIGHT = 16;
    
    public static final double MAP_WIDTH = 75;
    public static final double MAP_HEIGHT = 55;
    public static final double MAP_CORRIDOR_SIZE = 2;
    public static final double MAP_ROOM_MINSIZE = 6;
    public static final double MAP_ROOM_MAXSIZE = Math.min(MAP_WIDTH, MAP_HEIGHT) / 4;
    public static final double MAP_ROOM_NUMBER = Math.sqrt(MAP_WIDTH * MAP_HEIGHT) / 2;
    
    public static final float PIXELS_TO_METRES = 1f / 16f;
    
    public static final FileHandle LOG_FILE = Gdx.files.local("log.txt");
    
}
