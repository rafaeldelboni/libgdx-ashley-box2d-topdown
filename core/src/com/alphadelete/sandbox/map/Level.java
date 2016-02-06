package com.alphadelete.sandbox.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Point2D;


import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.Constants.TileType;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class Level {
	private long seed;
	private Random rdm;
	private List<Room> Rooms;
	private List<Room> Corridors;
	private ArrayMap<Point2D, TileType> tileMap;
	
    public Level() {
    	// Random without seed
    	this(new Random().nextLong());
    }

	public Level(long rdmSeed) {
		// Random with seed
		this.seed = rdmSeed;
		this.rdm = new Random(rdmSeed);

		// Generate Map
		this.tileMap = generateMap();
		// Generate Rooms
		this.Rooms = generateRooms();
		// Generate Corridors
		this.Corridors = generateCorridors();
	}
	
	public ArrayMap<Point2D, TileType> generate() {
		placeRooms();
		placeCorridors();
		placeWallRooms();
		placeWallCorridors();
		
		placeWallCorners();
		
		return this.tileMap;
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	// region placers
	private void placeRooms() {
		// Carve the rooms in base map
		for(Room room : this.Rooms) {
			for (double x = room.x1; x < room.x2; x++) {
				for (double y = room.y1; y < room.y2; y++) {
					Point2D coord = new Point2D(x, y);
					this.tileMap.put(coord, TileType.Floor);
				}
			}
		}
	}
	
	private void placeCorridors() {
		// Carve the corridor in base map
		for(Room corridor : this.Corridors) {
			for (double x = corridor.x1; x < corridor.x2; x++) {
				for (double y = corridor.y1; y < corridor.y2; y++) {
					Point2D coord = new Point2D(x, y);
					this.tileMap.put(coord, TileType.Corridor);
				}
			}
		}
	}
	// endregion
	
	// region map
	private ArrayMap<Point2D, TileType> generateMap() {
		ArrayMap<Point2D, TileType> newMap = new ArrayMap<Point2D, TileType> ();
		// Create base map without Tiles
		for(int x = 0; x < Constants.MAP_WIDTH; x++) {
			for(int y = 0; y < Constants.MAP_HEIGHT; y++) {
				Point2D coord = new Point2D(x, y);
				newMap.put(coord, TileType.None);
			}			
		}
		
		return newMap;
	}
	// endregion
	
	// region room
	private List<Room> generateRooms() {
		// Create array for room storage for easy access
		List<Room> newRooms = new ArrayList<Room>();
		int count = 0;
		
		// Randomize values for each room
		for (int r = 0; r < Constants.MAP_ROOM_NUMBER; r++) {
			double w = Constants.MAP_ROOM_MAXSIZE + this.rdm.nextInt((int) (Constants.MAP_ROOM_MAXSIZE - Constants.MAP_ROOM_MINSIZE + 1));
			double h = Constants.MAP_ROOM_MINSIZE + this.rdm.nextInt((int) (Constants.MAP_ROOM_MAXSIZE - Constants.MAP_ROOM_MINSIZE + 1));
			double x = this.rdm.nextInt((int) (Constants.MAP_WIDTH - w - 1)) + 1;
			double y = this.rdm.nextInt((int) (Constants.MAP_HEIGHT - h - 1)) + 1;
			
			// Create room with randomized values
			Room newRoom = new Room(x, y, w, h);
			boolean failed = false;
			
			for(Room otherRoom : newRooms) {
				if(newRoom.intersects(otherRoom)) {
					failed = true;
					break;
				}
			}
			if (!failed)
				newRooms.add(newRoom);

			count++;
			// Stop if many tries
			if (count > 1000)
				break;
		}
		return newRooms;
	}
	// endregion
	
	// region room
	private List<Room> generateCorridors() {
		// Create array for corridors storage for easy access
		List<Room> newCorridors = new ArrayList<Room>();
		
		// Loop each room
		for (int i = 0; i < this.Rooms.size(); i++) {
			Point2D thisCenter = this.Rooms.get(i).center;
			
			// Check if it is the last room
			if (i + 1 != Rooms.size()) {
				Point2D nextCenter = this.Rooms.get(i + 1).center;
				
				// Carve out corridors between rooms based on centers
				// Randomly start with horizontal or vertical corridors
				if (rdm.nextInt(2) == 1) {
					newCorridors.add(hCorridor(thisCenter.getX(), nextCenter.getX(), thisCenter.getY()));
					newCorridors.add(vCorridor(thisCenter.getY(), nextCenter.getY(), nextCenter.getX()));
				} else {
					newCorridors.add(vCorridor(thisCenter.getY(), nextCenter.getY(), thisCenter.getX()));
					newCorridors.add(hCorridor(thisCenter.getX(), nextCenter.getX(), nextCenter.getY()));			
				}
			}
		}
		
		return newCorridors;
	}
	
	private Room hCorridor(double x1, double x2, double y1) {
		double h = Constants.MAP_CORRIDOR_SIZE;
		double w = Math.max(x1, x2) - Math.min(x1, x2) + Constants.MAP_CORRIDOR_SIZE;
		double x = Math.min(x1, x2);
		double y = y1;
		Room corridor = new Room(x, y, w, h);
		return corridor;
	}
	
	private Room vCorridor(double y1, double y2, double x1) {
		double h = Math.max(y1, y2) - Math.min(y1, y2);
		double w = Constants.MAP_CORRIDOR_SIZE;
		double x = x1;
		double y = Math.min(y1, y2);
		Room corridor = new Room(x, y, w, h);
		return corridor;
	}
	// endregion
	
	// region wall
	private void placeWallRooms() {
		for(Entry<Point2D, TileType> map : this.tileMap.entries()) {

			if (map.value == TileType.Floor ) {
				
				Point2D leftWall = map.key.add(-1,0);
				if (getTileValue(leftWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(leftWall), TileType.WallLeft);
				}
			
				Point2D rightWall = map.key.add(+1,0);
				if (getTileValue(rightWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(rightWall), TileType.WallRight);
				}

				Point2D downWall = map.key.add(0,-1);
				if (getTileValue(downWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), TileType.WallDown);
				}

				Point2D upWallCheck = map.key.add(0,+1);
				if (getTileValue(upWallCheck) == TileType.None && 
						getTileValue(map.key) != TileType.WallLeft &&
							getTileValue(map.key) != TileType.WallRight ) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), TileType.WallUp);
					this.tileMap.setValue(this.tileMap.indexOfKey(upWallCheck), TileType.WallUp2);
				}
			}
		}
	}
	
	private void placeWallCorridors() {
		for(Entry<Point2D, TileType> map : this.tileMap.entries()) {

			if (map.value == TileType.Corridor) {
				
				Point2D leftWall = map.key.add(-1,0);
				if (getTileValue(leftWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(leftWall), TileType.WallLeft);
				}
			
				Point2D rightWall = map.key.add(+1,0);
				if (getTileValue(rightWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(rightWall), TileType.WallRight);
				}

				Point2D downWall = map.key.add(0,-1);
				if (getTileValue(downWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(downWall), TileType.WallDown);
				}

				Point2D upWallCheck = map.key.add(0,+1);
				if (getTileValue(upWallCheck) == TileType.None && 
						getTileValue(map.key) != TileType.WallLeft &&
							getTileValue(map.key) != TileType.WallRight ) {
					this.tileMap.setValue(this.tileMap.indexOfKey(upWallCheck), TileType.WallUp);
					this.tileMap.setValue(this.tileMap.indexOfKey(upWallCheck.add(0,+1)), TileType.WallUp2);
				}
				
			}

		}
	}

	private void placeWallCorners() {
		for(Entry<Point2D, TileType> map : this.tileMap.entries()) {

			Point2D leftTile = map.key.add(-1,0);
			Point2D rightTile = map.key.add(+1,0);
			Point2D downTile = map.key.add(0,-1);
			Point2D upTile = map.key.add(0,+1);
			if(getTileValue(map.key) != TileType.Floor && getTileValue(map.key) != TileType.Corridor) {
				// Wall Left Corner
				if ( 
					(getTileValue(leftTile) == TileType.WallUp || 
						getTileValue(leftTile) == TileType.WallUp2 ||
							getTileValue(leftTile) == TileType.WallLeft ||
								getTileValue(leftTile) == TileType.WallCornerRight ||
									getTileValue(leftTile) == TileType.WallCornerRightUp || 
										getTileValue(leftTile) == TileType.WallCornerLeftUp ) && 
					(getTileValue(rightTile) == TileType.Floor || 
						getTileValue(rightTile) == TileType.Corridor) &&
					(getTileValue(downTile) == TileType.Floor ||
						getTileValue(downTile) == TileType.Corridor) && 
					(getTileValue(upTile) != TileType.Floor &&
						getTileValue(upTile) != TileType.Corridor)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), TileType.WallCornerLeftUp);
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), TileType.WallCornerLeft);
				}
				// Wall Right Corner
				if ( 
					(getTileValue(rightTile) == TileType.WallUp || 
						getTileValue(rightTile) == TileType.WallUp2 ||
							getTileValue(rightTile) == TileType.WallRight ||
								getTileValue(rightTile) == TileType.WallCornerLeft ||
									getTileValue(rightTile) == TileType.WallCornerLeftUp ||
										getTileValue(rightTile) == TileType.WallCornerRightUp) && 
					(getTileValue(leftTile) == TileType.Floor || 
						getTileValue(leftTile) == TileType.Corridor) &&
					(getTileValue(downTile) == TileType.Floor ||
						getTileValue(downTile) == TileType.Corridor) && 
					(getTileValue(upTile) != TileType.Floor &&
						getTileValue(upTile) != TileType.Corridor)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), TileType.WallCornerRightUp);
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), TileType.WallCornerRight);
				}
			}
		}
	}
	// endregion
	
	private TileType getTileValue (Point2D tile) {
		if (this.tileMap.containsKey(tile))
			return this.tileMap.get(tile);
		
		return TileType.Null;
	}

}
