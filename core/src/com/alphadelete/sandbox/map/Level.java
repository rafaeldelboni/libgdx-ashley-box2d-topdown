package com.alphadelete.sandbox.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.map.Tile.TileType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class Level {
	private long seed;
	private Random rdm;
	private List<Room> Rooms;
	private List<Room> Corridors;
	private ArrayMap<Vector2, Tile> tileMap;
	
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
	
	public ArrayMap<Vector2, Tile> generate() {
		placeRooms();
		placeCorridors();
		placeWallRooms();
		placeWallCorridors();
		
		placeWallFix();
		placeCeilingFix();
		
		placeFirstDoor();
		placeLastDoor();
		
		return this.tileMap;
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	// region placers
	private void placeRooms() {
		// Carve the rooms in base map
		for(Room room : this.Rooms) {
			for (float x = room.x1; x < room.x2; x++) {
				for (float y = room.y1; y < room.y2; y++) {
					Vector2 coord = new Vector2(x, y);
					this.tileMap.put(coord, createTile(TileType.Floor));
				}
			}
		}
	}
	
	private void placeCorridors() {
		// Carve the corridor in base map
		for(Room corridor : this.Corridors) {
			for (float x = corridor.x1; x < corridor.x2; x++) {
				for (float y = corridor.y1; y < corridor.y2; y++) {
					Vector2 coord = new Vector2(x, y);
					this.tileMap.put(coord, createTile(TileType.Corridor));
				}
			}
		}
	}
	// endregion
	
	// region map
	private ArrayMap<Vector2, Tile> generateMap() {
		ArrayMap<Vector2, Tile> newMap = new ArrayMap<Vector2, Tile> ();
		// Create base map without Tiles
		for(int x = 0; x < Constants.MAP_WIDTH; x++) {
			for(int y = 0; y < Constants.MAP_HEIGHT; y++) {
				Vector2 coord = new Vector2(x, y);
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
			float w = (int)Constants.MAP_ROOM_MAXSIZE + this.rdm.nextInt((int) (Constants.MAP_ROOM_MAXSIZE - Constants.MAP_ROOM_MINSIZE + 1));
			float h = (int)Constants.MAP_ROOM_MINSIZE + this.rdm.nextInt((int) (Constants.MAP_ROOM_MAXSIZE - Constants.MAP_ROOM_MINSIZE + 1));
			float x = this.rdm.nextInt((int) (Constants.MAP_WIDTH - w - 1)) + 1;
			float y = this.rdm.nextInt((int) (Constants.MAP_HEIGHT - h - 1)) + 1;
			
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
			Vector2 thisCenter = this.Rooms.get(i).center;
			
			// Check if it is the last room
			if (i + 1 != Rooms.size()) {
				Vector2 nextCenter = this.Rooms.get(i + 1).center;
				
				// Carve out corridors between rooms based on centers
				// Randomly start with horizontal or vertical corridors
				if (rdm.nextInt(2) == 1) {
					newCorridors.add(hCorridor(thisCenter.x, nextCenter.x, thisCenter.y));
					newCorridors.add(vCorridor(thisCenter.y, nextCenter.y, nextCenter.x));
				} else {
					newCorridors.add(vCorridor(thisCenter.y, nextCenter.y, thisCenter.x));
					newCorridors.add(hCorridor(thisCenter.x, nextCenter.x, nextCenter.y));			
				}
			}
		}
		
		return newCorridors;
	}
	
	private Room hCorridor(float x1, float x2, float y1) {
		float h = Constants.MAP_CORRIDOR_SIZE;
		float w = Math.max(x1, x2) - Math.min(x1, x2) + Constants.MAP_CORRIDOR_SIZE;
		float x = Math.min(x1, x2);
		float y = y1;
		Room corridor = new Room(x, y, w, h);
		return corridor;
	}
	
	private Room vCorridor(float y1, float y2, float x1) {
		float h = Math.max(y1, y2) - Math.min(y1, y2);
		float w = Constants.MAP_CORRIDOR_SIZE;
		float x = x1;
		float y = Math.min(y1, y2);
		Room corridor = new Room(x, y, w, h);
		return corridor;
	}
	// endregion
	
	// region wall
	private void placeWallRooms() {
		for(Entry<Vector2, Tile> map : this.tileMap.entries()) {

			if (map.value.type == TileType.Floor ) {
				
				Vector2 leftWall = map.key.cpy().add(-1,0);
				if (getTileValue(leftWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(leftWall), createTile(TileType.CeilingLeft));
				}
			
				Vector2 rightWall = map.key.cpy().add(+1,0);
				if (getTileValue(rightWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(rightWall), createTile(TileType.CeilingRight));
				}

				Vector2 downWall = map.key.cpy().add(0,-1);
				if (getTileValue(downWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingDown));
				}

				Vector2 upWallCheck = map.key.cpy().add(0,+1);
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
		for(Entry<Vector2, Tile> map : this.tileMap.entries()) {

			if (map.value.type == TileType.Corridor) {
				
				Vector2 leftWall = map.key.cpy().add(-1,0);
				if (getTileValue(leftWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(leftWall), createTile(TileType.CeilingLeft));
				}
			
				Vector2 rightWall = map.key.cpy().add(+1,0);
				if (getTileValue(rightWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(rightWall), createTile(TileType.CeilingRight));
				}

				Vector2 downWall = map.key.cpy().add(0,-1);
				if (getTileValue(downWall) == TileType.None) {
					this.tileMap.setValue(this.tileMap.indexOfKey(downWall), createTile(TileType.CeilingDown));
				}

				Vector2 upWallCheck = map.key.cpy().add(0,+1);
				if (getTileValue(upWallCheck) == TileType.None && 
						getTileValue(map.key) != TileType.CeilingLeft &&
							getTileValue(map.key) != TileType.CeilingRight ) {
					int variation = getTileVariation(TileType.WallBase);
					this.tileMap.setValue(this.tileMap.indexOfKey(upWallCheck), new Tile(TileType.WallBase, variation));
					this.tileMap.setValue(this.tileMap.indexOfKey(upWallCheck.cpy().add(0,+1)), new Tile(TileType.WallBaseUp, variation));
				}
				
			}

		}
	}

	private void placeWallFix() {
		
		for(Entry<Vector2, Tile> map : this.tileMap.entries()) {
			if(getTileValue(map.key) != TileType.Floor && getTileValue(map.key) != TileType.Corridor) {
				Vector2 downTile = map.key.cpy().add(0,-1);
				Vector2 upTile = map.key.cpy().add(0,+1);
				Vector2 up2Tile = map.key.cpy().add(0,+2);
				
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
			}
		}
		
		for(Entry<Vector2, Tile> map : this.tileMap.entries()) {
			if(getTileValue(map.key) != TileType.Floor && getTileValue(map.key) != TileType.Corridor) {
				Vector2 leftTile = map.key.cpy().add(-1,0);
				Vector2 rightTile = map.key.cpy().add(+1,0);
				Vector2 downTile = map.key.cpy().add(0,-1);
				Vector2 upTile = map.key.cpy().add(0,+1);
				Vector2 up2Tile = map.key.cpy().add(0,+2);

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
		for(Entry<Vector2, Tile> map : this.tileMap.entries()) {
			if(getTileValue(map.key) != TileType.Floor && getTileValue(map.key) != TileType.Corridor) {
				Vector2 upTile = map.key.cpy().add(0,1);
				
				// Fix horizontal double side ceilings
				if(getTileValue(map.key) == TileType.WallBaseUp &&
					(getTileValue(upTile) == TileType.Floor ||
						getTileValue(upTile) == TileType.Corridor ||
							getTileValue(upTile) == TileType.CeilingDown) &&
								getTileValue(upTile) != TileType.Null) {
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingUpDown));
				}
				// Put up ceiling
				if(getTileValue(map.key) == TileType.WallBaseUp &&
					(getTileValue(upTile) != TileType.Floor && 
						getTileValue(upTile) != TileType.Corridor &&
							getTileValue(upTile) != TileType.CeilingUpDown &&
								getTileValue(upTile) != TileType.Null)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingUp));
				}
				// Put down ceiling
				if((getTileValue(map.key) == TileType.CeilingLeft || 
						getTileValue(map.key) == TileType.CeilingRight ) &&
					(getTileValue(upTile) == TileType.Floor ||
						getTileValue(upTile) == TileType.Corridor)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingDown));
				}
				// Square Ceiling on Double Corner Walls
				if(getTileValue(map.key) == TileType.WallCornerDoubleUp &&
					(getTileValue(upTile) == TileType.Floor || 
						getTileValue(upTile) == TileType.Corridor ) &&
					getTileValue(upTile) != TileType.Null) {
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingSquare));
				}
				// Put U up ceiling
				if(getTileValue(map.key) == TileType.WallCornerDoubleUp &&
					(getTileValue(upTile) != TileType.Floor &&
						getTileValue(upTile) != TileType.Corridor &&
							getTileValue(upTile) != TileType.CeilingSquare &&
								getTileValue(upTile) != TileType.Null)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingUUp));
				}
				// Put Right U ceiling on Right corner walls
				if(getTileValue(map.key) == TileType.WallCornerRightUp &&
					(getTileValue(upTile) == TileType.Floor || 
						getTileValue(upTile) == TileType.Corridor ||
							getTileValue(upTile) == TileType.CeilingDown ||
								getTileValue(upTile) == TileType.CeilingUpDown || 
									getTileValue(upTile) == TileType.CeilingRight || 
										getTileValue(upTile) == TileType.CeilingLeft ) &&
					(getTileValue(upTile.cpy().add(0,1)) == TileType.Floor || 
						getTileValue(upTile.cpy().add(0,1)) == TileType.Corridor) &&
					getTileValue(upTile) != TileType.Null) {
				this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingURight));
				}
				// Put Ceiling Right Corner Walls
				if(getTileValue(map.key) == TileType.WallCornerRightUp &&
					(getTileValue(upTile) != TileType.Floor &&
						getTileValue(upTile) != TileType.Corridor &&
							getTileValue(upTile) != TileType.CeilingURight &&
								getTileValue(upTile) != TileType.Null)) {
						this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingRightUp));
				}
				// Put Left U ceiling on left corner walls
				if(getTileValue(map.key) == TileType.WallCornerLeftUp &&
					(getTileValue(upTile) == TileType.Floor || 
						getTileValue(upTile) == TileType.Corridor ||
							getTileValue(upTile) == TileType.CeilingDown ||
								getTileValue(upTile) == TileType.CeilingUp ||
									getTileValue(upTile) == TileType.CeilingUpDown ||
										getTileValue(upTile) == TileType.CeilingRight || 
											getTileValue(upTile) == TileType.CeilingLeft) &&
					(getTileValue(upTile.cpy().add(0,1)) == TileType.Floor || 
						getTileValue(upTile.cpy().add(0,1)) == TileType.Corridor) &&
					getTileValue(upTile) != TileType.Null) {
						this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingULeft));
				}
				
				// Put Ceiling Left Corner Walls
				if(getTileValue(map.key) == TileType.WallCornerLeftUp &&
					(getTileValue(upTile) != TileType.Floor &&
						getTileValue(upTile) != TileType.Corridor &&
							getTileValue(upTile) != TileType.CeilingULeft &&
								getTileValue(upTile) != TileType.Null)) {
						this.tileMap.setValue(this.tileMap.indexOfKey(upTile), createTile(TileType.CeilingLeftUp));
				}
			}
		}
		for(Entry<Vector2, Tile> map : this.tileMap.entries()) {
			if(getTileValue(map.key) != TileType.Floor && getTileValue(map.key) != TileType.Corridor) {
				Vector2 upTile = map.key.cpy().add(0,+1);
				Vector2 downTile = map.key.cpy().add(0,-1);
				Vector2 leftTile = map.key.cpy().add(-1,0);
				Vector2 rightTile = map.key.cpy().add(+1,0);
				
		
				// Put right down corner ceilings
				if((getTileValue(map.key) == TileType.CeilingLeft || 
						getTileValue(map.key) == TileType.CeilingRight || 
							getTileValue(map.key) == TileType.CeilingUp || 
								getTileValue(map.key) == TileType.CeilingDown ) &&
					(getTileValue(rightTile) == TileType.Floor ||
						getTileValue(rightTile) == TileType.Corridor) &&
					(getTileValue(upTile) == TileType.Floor ||
						getTileValue(upTile) == TileType.Corridor)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingLeftDown));
				}
				// Put left down corner ceilings
				if((getTileValue(map.key) == TileType.CeilingLeft || 
						getTileValue(map.key) == TileType.CeilingRight|| 
							getTileValue(map.key) == TileType.CeilingUp || 
								getTileValue(map.key) == TileType.CeilingDown  ) &&
					(getTileValue(leftTile) == TileType.Floor || 
						getTileValue(leftTile) == TileType.Corridor) &&
					(getTileValue(upTile) == TileType.Floor ||
						getTileValue(upTile) == TileType.Corridor)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingRightDown));
				}
				// Fix right ceilings
				if((getTileValue(map.key) == TileType.None || 
						getTileValue(map.key) == TileType.CeilingLeft) &&
					(getTileValue(leftTile) == TileType.Floor || 
						getTileValue(leftTile) == TileType.Corridor ||
							getTileValue(leftTile) == TileType.WallBase ||
								getTileValue(leftTile) == TileType.WallBaseUp ||
									getTileValue(leftTile) == TileType.WallCornerRight ||
										getTileValue(leftTile) == TileType.WallCornerRightUp ||
											getTileValue(leftTile) == TileType.WallCornerLeft ||
												getTileValue(leftTile) == TileType.WallCornerLeftUp )) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingRight));
				}
				// Fix left ceilings
				if((getTileValue(map.key) == TileType.None || 
						getTileValue(map.key) == TileType.CeilingRight) &&
					(getTileValue(rightTile) == TileType.Floor || 
						getTileValue(rightTile) == TileType.Corridor ||
							getTileValue(rightTile) == TileType.WallBase ||
								getTileValue(rightTile) == TileType.WallBaseUp ||
									getTileValue(rightTile) == TileType.WallCornerLeft  ||
										getTileValue(rightTile) == TileType.WallCornerLeftUp || 
											getTileValue(rightTile) == TileType.WallCornerRight ||
												getTileValue(rightTile) == TileType.WallCornerRightUp)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingLeft));
				}
				// Fix vertical double side ceilings
				if((getTileValue(map.key) == TileType.CeilingLeft || 
						getTileValue(map.key) == TileType.CeilingRight) &&
					(getTileValue(leftTile) == TileType.Floor || 
						getTileValue(leftTile) == TileType.Corridor || 
							getTileValue(leftTile) == TileType.WallBase ||
								getTileValue(leftTile) == TileType.WallBaseUp ||
									getTileValue(leftTile) == TileType.WallCornerRight ||
										getTileValue(leftTile) == TileType.WallCornerRightUp ||
											getTileValue(leftTile) == TileType.WallCornerLeft ||
												getTileValue(leftTile) == TileType.WallCornerLeftUp ) &&
					(getTileValue(rightTile) == TileType.Floor ||
						getTileValue(rightTile) == TileType.Corridor ||
							getTileValue(rightTile) == TileType.WallBase ||
								getTileValue(rightTile) == TileType.WallBaseUp ||
									getTileValue(rightTile) == TileType.WallCornerLeft  ||
										getTileValue(rightTile) == TileType.WallCornerLeftUp || 
											getTileValue(rightTile) == TileType.WallCornerRight ||
												getTileValue(rightTile) == TileType.WallCornerRightUp)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingLeftRight));
				}
				// Put U down ceiling
				if((getTileValue(map.key) == TileType.CeilingLeft || 
						getTileValue(map.key) == TileType.CeilingRight ||
							getTileValue(map.key) == TileType.CeilingLeftRight ||
								getTileValue(map.key) == TileType.CeilingLeftDown ||
									getTileValue(map.key) == TileType.CeilingRightDown ) &&
					(getTileValue(leftTile) == TileType.Floor || 
						getTileValue(leftTile) == TileType.Corridor) &&
					(getTileValue(rightTile) == TileType.Floor ||
						getTileValue(rightTile) == TileType.Corridor)  &&
					(getTileValue(upTile) == TileType.Floor || 
						getTileValue(upTile) == TileType.Corridor)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.CeilingUDown));
				}
				// Remove unnecessary ceilings
				if((getTileValue(map.key) == TileType.CeilingLeft || 
						getTileValue(map.key) == TileType.CeilingRight ) &&
					(getTileValue(leftTile) != TileType.Floor && 
						getTileValue(leftTile) != TileType.Corridor &&
							getTileValue(leftTile) != TileType.WallBase &&
								getTileValue(leftTile) != TileType.WallBaseUp &&
									getTileValue(leftTile) != TileType.WallCornerRight &&
										getTileValue(leftTile) != TileType.WallCornerRightUp &&
											getTileValue(leftTile) != TileType.WallCornerLeft &&
												getTileValue(leftTile) != TileType.WallCornerLeftUp ) &&
					(getTileValue(rightTile) != TileType.Floor && 
						getTileValue(rightTile) != TileType.Corridor &&
							getTileValue(rightTile) != TileType.WallBase &&
								getTileValue(rightTile) != TileType.WallBaseUp &&
									getTileValue(rightTile) != TileType.WallCornerRight &&
										getTileValue(rightTile) != TileType.WallCornerRightUp &&
											getTileValue(rightTile) != TileType.WallCornerLeft &&
												getTileValue(rightTile) != TileType.WallCornerLeftUp) &&
					(getTileValue(upTile) != TileType.Floor &&
						getTileValue(upTile) != TileType.Corridor) &&
					(getTileValue(downTile) != TileType.Floor &&
						getTileValue(downTile) != TileType.Corridor)) {
					this.tileMap.setValue(this.tileMap.indexOfKey(map.key), createTile(TileType.None));
				}

			}
		}
	}
	private void placeFirstDoor() {
		int firstDoor = 0,
			firstDoorAttempts = 0,
			firstRoomAttempts = 0;
		
		while (firstDoor == 0) {
			if(firstRoomAttempts < this.Rooms.size()) {
				// Try to put a door in every upper wall block
				Room firstRoom = this.Rooms.get(firstRoomAttempts);
				float xFirst = randFloatRange(firstRoom.x1,firstRoom.x2);
				float yFirst = firstRoom.y2;
				Vector2 coord = new Vector2 (xFirst, yFirst); 
				// If the number of attempts is different from the room size
				if (firstRoom.w >= firstDoorAttempts) {
					// Check if block is a upper wall
					if( this.tileMap.get(coord).type == TileType.WallBase ) {
						int variation = getTileVariation(TileType.WallEnter);
						this.tileMap.setValue(this.tileMap.indexOfKey(coord.cpy().add(0,1)), new Tile(TileType.WallEnterUp, variation));
						this.tileMap.setValue(this.tileMap.indexOfKey(coord), new Tile(TileType.WallEnter, variation));
						firstDoor++;
					} else if (this.tileMap.get(coord).type == TileType.WallBaseUp){
						int variation = getTileVariation(TileType.WallEnter);
						this.tileMap.setValue(this.tileMap.indexOfKey(coord), new Tile(TileType.WallEnterUp, variation));
						this.tileMap.setValue(this.tileMap.indexOfKey(coord.cpy().add(0,-1)), new Tile(TileType.WallEnter, variation));
						firstDoor++;					
					}
					firstDoorAttempts++;
				}
				// else there is no block available to put a door try another room
				else {
					firstRoomAttempts++;
				}
			// This should never happen
			} else {
				// Find for the very first wall and set it as door  
				for(Entry<Vector2, Tile> map : tileMap.entries()) {
					if (map.value.type == Tile.TileType.WallBase ) {
						int variation = getTileVariation(TileType.WallEnter);
						this.tileMap.setValue(this.tileMap.indexOfKey(map.key.cpy().add(0,1)), new Tile(TileType.WallEnterUp, variation));
						this.tileMap.setValue(this.tileMap.indexOfKey(map.key), new Tile(TileType.WallEnter, variation));
						firstDoor++;
						break;
					}
				}
			}
		}
	}
	
	private void placeLastDoor() {
		int lastDoor = 0,
			lastDoorAttempts = 0,
			lastRoomAttempts = 0;
		
		while (lastDoor == 0) {
			if (lastRoomAttempts < this.Rooms.size()) {
				// Try to put a door in every upper wall block
				Room lastRoom = this.Rooms.get(this.Rooms.size() - (1 + lastRoomAttempts));
				float xFirst = randFloatRange(lastRoom.x1,lastRoom.x2);
				float yFirst = lastRoom.y2;
				Vector2 coord = new Vector2 (xFirst, yFirst); 
				// If the number of attempts is different from the room size
				if (lastRoom.w >= lastDoorAttempts) {
					// Check if block is a upper wall
					if( this.tileMap.get(coord).type == TileType.WallBase ) {
						int variation = getTileVariation(TileType.WallEnter);
						this.tileMap.setValue(this.tileMap.indexOfKey(coord.cpy().add(0,1)), new Tile(TileType.WallExitUp, variation));
						this.tileMap.setValue(this.tileMap.indexOfKey(coord), new Tile(TileType.WallExit, variation));
						lastDoor++;
					} else if (this.tileMap.get(coord).type == TileType.WallBaseUp){
						int variation = getTileVariation(TileType.WallEnter);
						this.tileMap.setValue(this.tileMap.indexOfKey(coord), new Tile(TileType.WallExitUp, variation));
						this.tileMap.setValue(this.tileMap.indexOfKey(coord.cpy().add(0,-1)), new Tile(TileType.WallExit, variation));
						lastDoor++;					
					}
					lastDoorAttempts++;
				}
				// else there is no block available to put a door try another room
				else {
					lastRoomAttempts++;
				}
			// This should never happen
			} else {
				// Find for the very first wall and set it as door  
				for(Entry<Vector2, Tile> map : tileMap.entries()) {
					if (map.value.type == Tile.TileType.WallBase ) {
						int variation = getTileVariation(TileType.WallEnter);
						this.tileMap.setValue(this.tileMap.indexOfKey(map.key.cpy().add(0,1)), new Tile(TileType.WallEnterUp, variation));
						this.tileMap.setValue(this.tileMap.indexOfKey(map.key), new Tile(TileType.WallEnter, variation));
						lastDoor++;
						break;
					}
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
	
	private TileType getTileValue (Vector2 tile) {
		if (this.tileMap.containsKey(tile))
			return this.tileMap.get(tile).type;
		
		return TileType.Null;
	}
	
	private int getTileVariation(Tile.TileType type) {
		return this.rdm.nextInt(type.variants()) + 1;
	}
	
	private float randFloatRange(float min, float max) {

		return (float)this.rdm.nextInt((int)(max - min) + 1) + (int)min;
	}
	// endregion

}
