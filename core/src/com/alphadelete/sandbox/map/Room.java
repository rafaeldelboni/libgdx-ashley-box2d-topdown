package com.alphadelete.sandbox.map;

import com.alphadelete.sandbox.Constants;
import com.badlogic.gdx.math.Vector2;

public class Room {
	// Grid coordinates for each corner of the room
	public float x1;
	public float x2;
	public float y1;
	public float y2;
	
	// Width and height of room in terms of grid
	public float w;
	public float h;
	
	// Center point of the room
	public Vector2 coords;
	
	// Center point of the room
	public Vector2 center;

	// Constructor for creating new rooms
	public Room(float x, float y, float w, float h) {
		this.x1 = x;
		this.x2 = x + w;
		this.y1 = y;
		this.y2 = y + h;
		
		this.coords = new Vector2(x * Constants.TILE_WIDTH, y * Constants.TILE_HEIGHT);
		this.w = w;
		this.h = h;
		
		this.center = new Vector2(
				// Convert to into to get the relative center related to a tile
				(int)Math.floor(x1 + x2) / 2, 
				(int)Math.floor(y1 + y2) / 2
		);
	}
	// Return true if this room the intersects provided room
	public boolean intersects(Room room) {
		return(x1 <= room.x2 && x2 >= room.x1 && 
				y1 <= room.y2 && y2 >= room.y1);
	}

}
