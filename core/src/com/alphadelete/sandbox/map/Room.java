package com.alphadelete.sandbox.map;

import com.alphadelete.sandbox.Constants;
import javafx.geometry.Point2D;

public class Room {
	// Grid coordinates for each corner of the room
	public double x1;
	public double x2;
	public double y1;
	public double y2;
	
	// Width and height of room in terms of grid
	public double w;
	public double h;
	
	// Center point of the room
	public Point2D coords;
	
	// Center point of the room
	public Point2D center;

	// Constructor for creating new rooms
	public Room(double x, double y, double w, double h) {
		this.x1 = x;
		this.x2 = x + w;
		this.y1 = y;
		this.y2 = y + h;
		
		this.coords = new Point2D(x * Constants.TILE_WIDTH, y * Constants.TILE_HEIGHT);
		this.w = w;
		this.h = h;
		
		this.center = new Point2D(
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
