package com.alphadelete.sandbox.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Point2D;

import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.map.Tile.TileType;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class Level {
	private long seed;
	private Random rdm;
	private List<Room> Rooms;
	private List<Room> Corridors;
	private ArrayMap<Point2D, Tile> tileMap;
	
    public Level() {
    	// Random without seeds
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
	
	public ArrayMap<Point2D, Tile> generate() {
		placeRooms();
		placeCorridors();
		placeWallRooms();
		placeWallCorridors();
		
		placeWallFix();
		placeCeilingFix();
		
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
					this.tileMap.put(coord, createTile(TileType.Floor));
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
					this.tileMap.put(coord, createTile(TileType.Corridor));
				}
			}
		}
	}
	// endregion
	
	// region map
	private ArrayMap<Point2D, Tile> generateMap() {
		ArrayMap<Point2D, Tile> newMap = new ArrayMap<Point2D, Tile> ();
		// Create base map without Tiles
		for(int x = 0; x < Constants.MAP_WIDTH; x++) {
			for(int y = 0; y < Constants.MAP_HEIGHT; y++) {
				Point2D coord = new Point2D(x, y);
				newMap.put(coord, createTile(TileType.None));
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
		for(Entry<Point2D, Tile> map : this.tileMap.entries()) {

			if (map.value.type == TileType.Floor ) {
				
				Point2D leftWall = map.key.add(-1,0);
				if (getTileValue(leftWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(leftWall), createTile(TileType.CeilingLeft));
				}
			
				Point2D rightWall = map.key.add(+1,0);
				if (getTileValue(rightWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(rightWall), createTile(TileType.CeilingRight));
				}

				Point2D downWall = map.key.add(0,-1);
				if (getTileValue(downWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingDown));
				}

				Point2D upWallCheck = map.key.add(0,+1);
				if (getTileValue(upWallCheck) == TileType.None && 
						getTileValue(map.key) != TileType.CeilingLeft &&
							getTileValue(map.key) != TileType.CeilingRight ) {
					int variation = getTileVariation(TileType.WallBase);
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), new Tile(TileType.WallBase, variation));
					this.tileMap.setValue(this.tileMap.indexOfKey(upWallCheck), new Tile(TileType.WallBaseUp, variation));
				}
			}
		}
	}
	
	private void placeWallCorridors() {
		for(Entry<Point2D, Tile> map : this.tileMap.entries()) {

			if (map.value.type == TileType.Corridor) {
				
				Point2D leftWall = map.key.add(-1,0);
				if (getTileValue(leftWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(leftWall), createTile(TileType.CeilingLeft));
				}
			
				Point2D rightWall = map.key.add(+1,0);
				if (getTileValue(rightWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(rightWall), createTile(TileType.CeilingRight));
				}

				Point2D downWall = map.key.add(0,-1);
				if (getTileValue(downWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(downWall), createTile(TileType.CeilingDown));
				}

				Point2D upWallCheck = map.key.add(0,+1);
				if (getTileValue(upWallCheck) == TileType.None && 
						getTileValue(map.key) != TileType.CeilingLeft &&
							getTileValue(map.key) != TileType.CeilingRight ) {
					int variation = getTileVariation(TileType.WallBase);
					this.tileMap.setValue(this.tileMap.indexOfKey(upWallCheck), new Tile(TileType.WallBase, variation));
					this.tileMap.setValue(this.tileMap.indexOfKey(upWallCheck.add(0,+1)), new Tile(TileType.WallBaseUp, variation));
				}
				
			}

		}
	}

	private void placeWallFix() {
		for(Entry<Point2D, Tile> map : this.tileMap.entries()) {
			if(getTileValue(map.key) != TileType.Floor && getTileValue(map.key) != TileType.Corridor) {
				Point2D leftTile = map.key.add(-1,0);
				Point2D rightTile = map.key.add(+1,0);
				Point2D downTile = map.key.add(0,-1);
				Point2D upTile = map.key.add(0,+1);
				Point2D up2Tile = map.key.add(0,+2);
				
				// Remove upper walls in the middle of the corridor
				if ( 
						(getTileValue(map.key) == TileType.WallBase ||
							getTileValue(map.key) == TileType.CeilingLeft ||
								getTileValue(map.key) == TileType.CeilingRight ) &&
						(getTileValue(downTile) == TileType.Floor ||
							getTileValue(downTile) == TileType.Corridor) &&
						(getTileValue(up2Tile) == TileType.Corridor)) {
					
						this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.Floor));
						this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.Floor));
				}
				// Put Right Wall Corner
				if ( 
					(getTileValue(leftTile) == TileType.WallBase || 
						getTileValue(leftTile) == TileType.WallBaseUp ||
							getTileValue(leftTile) == TileType.CeilingLeft ||
								getTileValue(leftTile) == TileType.CeilingRight ||
									getTileValue(leftTile) == TileType.WallCornerLeft ||
										getTileValue(leftTile) == TileType.WallCornerLeftUp ) && 
					(getTileValue(rightTile) == TileType.Floor || 
						getTileValue(rightTile) == TileType.Corridor) &&
					(getTileValue(downTile) == TileType.Floor ||
						getTileValue(downTile) == TileType.Corridor) && 
					(getTileValue(upTile) != TileType.Floor &&
						getTileValue(upTile) != TileType.Corridor)) {
					
					int variation = getTileVariation(TileType.WallCornerRight);
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), new Tile(TileType.WallCornerRightUp, variation));
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), new Tile(TileType.WallCornerRight, variation));
				}
				// Put Left Wall Corner
				if ( 
					(getTileValue(rightTile) == TileType.WallBase || 
						getTileValue(rightTile) == TileType.WallBaseUp ||
							getTileValue(rightTile) == TileType.CeilingRight ||
								getTileValue(rightTile) == TileType.CeilingLeft ||
									getTileValue(rightTile) == TileType.WallCornerRight ||
										getTileValue(rightTile) == TileType.WallCornerRightUp) && 
					(getTileValue(leftTile) == TileType.Floor || 
						getTileValue(leftTile) == TileType.Corridor) &&
					(getTileValue(downTile) == TileType.Floor ||
						getTileValue(downTile) == TileType.Corridor) && 
					(getTileValue(upTile) != TileType.Floor &&
						getTileValue(upTile) != TileType.Corridor)) {
					
					int variation = getTileVariation(TileType.WallCornerLeft);
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), new Tile(TileType.WallCornerLeftUp, variation));
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), new Tile(TileType.WallCornerLeft, variation));
				}
				// Put Double Wall Corner
				if ( 
						(getTileValue(rightTile) == TileType.Floor || 
							getTileValue(rightTile) == TileType.Corridor) &&
						(getTileValue(leftTile) == TileType.Floor || 
							getTileValue(leftTile) == TileType.Corridor) &&
						(getTileValue(downTile) == TileType.Floor ||
							getTileValue(downTile) == TileType.Corridor) && 
						(getTileValue(upTile) != TileType.Floor &&
							getTileValue(upTile) != TileType.Corridor)) {
			
					int variation = getTileVariation(TileType.WallCornerDouble);
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), new Tile(TileType.WallCornerDoubleUp, variation));
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), new Tile(TileType.WallCornerDouble, variation));
				}
				// Fix left or right wall in the middle of upper walls
				if ( 
						(getTileValue(rightTile) == TileType.WallBase ||
							getTileValue(rightTile) == TileType.WallBaseUp ||
								getTileValue(rightTile) == TileType.WallCornerLeft ||
									getTileValue(rightTile) == TileType.WallCornerRight ||
										getTileValue(rightTile) == TileType.WallCornerLeftUp ||
											getTileValue(rightTile) == TileType.WallCornerRightUp) &&
						(getTileValue(leftTile) == TileType.WallBase ||
							getTileValue(leftTile) == TileType.WallBaseUp ||
								getTileValue(leftTile) == TileType.WallCornerLeft ||
									getTileValue(leftTile) == TileType.WallCornerRight ||
										getTileValue(leftTile) == TileType.WallCornerLeftUp ||
											getTileValue(leftTile) == TileType.WallCornerRightUp ) &&
						(getTileValue(downTile) == TileType.Floor ||
							getTileValue(downTile) == TileType.Corridor) &&
						(getTileValue(up2Tile) != TileType.Corridor)) {
				
					int variation = getTileVariation(TileType.WallBase);
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), new Tile(TileType.WallBaseUp, variation));
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), new Tile(TileType.WallBase, variation));
				}
			}
		}
	}
	
	private void placeCeilingFix() {
		for(Entry<Point2D, Tile> map : this.tileMap.entries()) {
			if(getTileValue(map.key) != TileType.Floor && getTileValue(map.key) != TileType.Corridor) {
				Point2D upTile = map.key.add(0,+1);
				
				// Top Ceiling Normal Walls
				if(getTileValue(map.key) == TileType.WallBaseUp &&
					(getTileValue(upTile) == TileType.Floor ||
						getTileValue(upTile) == TileType.Corridor ||
							getTileValue(upTile) == TileType.CeilingDown) &&
								getTileValue(upTile) != TileType.Null) {
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingUpDown));
				}
				if(getTileValue(map.key) == TileType.WallBaseUp &&
					(getTileValue(upTile) != TileType.Floor && 
						getTileValue(upTile) != TileType.Corridor &&
							getTileValue(upTile) != TileType.CeilingUpDown &&
								getTileValue(upTile) != TileType.Null)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingUp));
				}
				// Top Ceiling Double Corner Walls
				if(getTileValue(map.key) == TileType.WallCornerDoubleUp &&
					(getTileValue(upTile) == TileType.Floor || 
						getTileValue(upTile) == TileType.Corridor ||
							getTileValue(upTile) == TileType.CeilingDown) &&
								getTileValue(upTile) != TileType.Null) {
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingSquare));
				}
				if(getTileValue(map.key) == TileType.WallCornerDoubleUp &&
					(getTileValue(upTile) != TileType.Floor &&
						getTileValue(upTile) != TileType.Corridor &&
							getTileValue(upTile) != TileType.CeilingSquare &&
								getTileValue(upTile) != TileType.Null)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingUUp));
				}
				// Top Ceiling Right Corner Walls
				if(getTileValue(map.key) == TileType.WallCornerRightUp &&
					(getTileValue(upTile) == TileType.Floor || 
						getTileValue(upTile) == TileType.Corridor ||
							getTileValue(upTile) == TileType.CeilingDown ||
								getTileValue(upTile) == TileType.CeilingUpDown) &&
									getTileValue(upTile) != TileType.Null) {
				this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingURight));
				}
				if(getTileValue(map.key) == TileType.WallCornerRightUp &&
					(getTileValue(upTile) != TileType.Floor &&
						getTileValue(upTile) != TileType.Corridor &&
							getTileValue(upTile) != TileType.CeilingURight &&
								getTileValue(upTile) != TileType.Null)) {
						this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingRightUp));
				}
				// Top Ceiling Left Corner Walls
				if(getTileValue(map.key) == TileType.WallCornerLeftUp &&
					(getTileValue(upTile) == TileType.Floor || 
						getTileValue(upTile) == TileType.Corridor ||
							getTileValue(upTile) == TileType.CeilingDown ||
								getTileValue(upTile) == TileType.CeilingUpDown) &&
									getTileValue(upTile) != TileType.Null) {
						this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingULeft));
				}
				if(getTileValue(map.key) == TileType.WallCornerLeftUp &&
					(getTileValue(upTile) != TileType.Floor &&
						getTileValue(upTile) != TileType.Corridor &&
							getTileValue(upTile) != TileType.CeilingULeft &&
								getTileValue(upTile) != TileType.Null)) {
						this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingLeftUp));
				}
				
				Point2D leftTile = map.key.add(-1,0);
				Point2D rightTile = map.key.add(+1,0);
				
				if((getTileValue(map.key) == TileType.None || 
						getTileValue(map.key) == TileType.CeilingLeft) &&
					(getTileValue(leftTile) == TileType.Floor || 
						getTileValue(leftTile) == TileType.Corridor ||
							getTileValue(leftTile) == TileType.WallBase ||
								getTileValue(leftTile) == TileType.WallBaseUp)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingRight));
				}
				if((getTileValue(map.key) == TileType.None || 
						getTileValue(map.key) == TileType.CeilingRight) &&
					(getTileValue(rightTile) == TileType.Floor || 
						getTileValue(rightTile) == TileType.Corridor ||
							getTileValue(rightTile) == TileType.WallBase ||
								getTileValue(rightTile) == TileType.WallBaseUp)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingLeft));
				}
				if((getTileValue(map.key) == TileType.CeilingLeft || 
						getTileValue(map.key) == TileType.CeilingRight) &&
					(getTileValue(leftTile) == TileType.Floor || 
						getTileValue(leftTile) == TileType.Corridor) &&
					(getTileValue(rightTile) == TileType.Floor ||
						getTileValue(rightTile) == TileType.Corridor)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingLeftRight));
				}
				if((getTileValue(map.key) == TileType.CeilingLeft || 
						getTileValue(map.key) == TileType.CeilingRight ||
							getTileValue(map.key) == TileType.CeilingLeftRight ) &&
					(getTileValue(leftTile) == TileType.Floor || 
						getTileValue(leftTile) == TileType.Corridor) &&
					(getTileValue(rightTile) == TileType.Floor ||
						getTileValue(rightTile) == TileType.Corridor)  &&
					(getTileValue(upTile) == TileType.Floor || 
						getTileValue(upTile) == TileType.Corridor)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingUDown));
				}
			}
		}
	}
	// endregion
	
	// region Utils
	private Tile createTile (TileType type) {
		int variation = getTileVariation(type);
		return new Tile (type, variation);
	}
	
	private TileType getTileValue (Point2D tile) {
		if (this.tileMap.containsKey(tile))
			return this.tileMap.get(tile).type;
		
		return TileType.Null;
	}
	
	private int getTileVariation(Tile.TileType type) {
		return this.rdm.nextInt(type.variants()) + 1;
	}
	

	// endregion

}
