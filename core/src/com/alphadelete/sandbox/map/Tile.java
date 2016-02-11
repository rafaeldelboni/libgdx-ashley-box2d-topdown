package com.alphadelete.sandbox.map;

import com.alphadelete.sandbox.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {
	// Enum for tile types
	public enum TileType {
		CeilingDown (6, "ceiling-down-"),
		CeilingLeft (6, "ceiling-left-"),
		CeilingLeftDown (1, "ceiling-left-down-"),
		CeilingLeftRight (2, "ceiling-left-right-"),
		CeilingLeftUp (1, "ceiling-left-up-"),
		CeilingRight (6, "ceiling-right-"),
		CeilingRightDown (1, "ceiling-right-down-"),
		CeilingRightUp (1, "ceiling-right-up-"),
		CeilingSquare (1, "ceiling-square-"),
		CeilingUDown (1, "ceiling-u-down-"),
		CeilingULeft (1, "ceiling-u-left-"),
		CeilingURight (1, "ceiling-u-right-"),
		CeilingUUp (1, "ceiling-u-up-"),
		CeilingUp (6, "ceiling-up-"),
		CeilingUpDown (2, "ceiling-up-down-"),
		Floor (5, "floor-"),
		Corridor (5, "floor-"),
		//Corridor (3, "floor-stone-"),
		None (1, "none-"),
		WallBase (4, "wall-base-"),
		WallBaseUp (4, "wall-base-up-"),
		WallCornerDouble (1, "wall-corner-double-"),
		WallCornerDoubleUp (1, "wall-corner-double-up-"),
		WallCornerLeft (1, "wall-corner-left-"),
		WallCornerLeftUp (1, "wall-corner-left-up-"),
		WallCornerRight (1, "wall-corner-right-"),
		WallCornerRightUp (1, "wall-corner-right-up-"),
		WallEnter (1, "wall-enter-"),
		WallEnterUp (1, "wall-enter-up-"),
		WallExit (1, "wall-exit-"),
		WallExitUp (1, "wall-exit-up-"),
		Null (1, "");
		
	    private final int variants;
	    private final String atlasName;
	    
	    TileType(int variants, String atlasName) {
	        this.variants = variants;
	        this.atlasName = atlasName;
	    }
	    
	    public int variants() { return variants; }
	    public String atlasName() { return atlasName; }
	}

	public TileType type;
	public TextureRegion texture;

	// Constructor for creating new tiles
	public Tile(TileType type) {
		this.type = type;
		this.texture = getTileTexture(type);
	}
	
	public Tile(TileType type, int variation) {
		this.type = type;
		this.texture = getTileTexture(type, variation);
	}
	
	private TextureRegion getTileTexture (Tile.TileType type) {
		return getTileTexture(type, 1);
	}
	
	private TextureRegion getTileTexture (Tile.TileType type, int variation) {
		return Assets.loadAtlasTextureRegion(type.atlasName() + variation, Assets.dungeonAtlas);
	}

}
