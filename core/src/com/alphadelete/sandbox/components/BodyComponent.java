package com.alphadelete.sandbox.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class BodyComponent implements Component {
    // Box2d CATEGORIES
	public final static short CATEGORY_PLAYER = 0x0001;
    public final static short CATEGORY_MONSTER = 0x0002;
    public final static short CATEGORY_SCENERY = 0x0004;
    public final static short CATEGORY_ATTACK = 0x0008;
    public final static short CATEGORY_PLAYER_ATTACK = 0x0016;
    public final static short CATEGORY_MONSTER_ATTACK = 0x0032;
    public final static short CATEGORY_EXIT = 0x0064;
    // Box2d MASKS
    public final static short MASK_PLAYER = CATEGORY_MONSTER | CATEGORY_SCENERY; // or ~CATEGORY_PLAYER
    public final static short MASK_MONSTER = CATEGORY_PLAYER | CATEGORY_MONSTER | CATEGORY_SCENERY; // or ~CATEGORY_MONSTER
    public final static short MASK_ATTACK = CATEGORY_PLAYER | CATEGORY_MONSTER | CATEGORY_SCENERY;
    public final static short MASK_PLAYER_ATTACK = CATEGORY_MONSTER | CATEGORY_SCENERY;
    public final static short MASK_MONSTER_ATTACK = CATEGORY_PLAYER | CATEGORY_SCENERY;
    public final static short MASK_SCENERY = -1;
    public final static short MASK_EXIT = CATEGORY_PLAYER;
    
	public Body body;
	
	public BodyComponent(Entity entity, World world, BodyDef.BodyType type, Shape shape, Vector3 pos, float density, float friction, float restitution, Boolean isSensor, short category, short mask) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(pos.x, pos.y);

		body = world.createBody(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.friction = friction;
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;
		fixtureDef.isSensor = isSensor;
		fixtureDef.filter.categoryBits = category; 
		fixtureDef.filter.maskBits = mask;
		
		body.setLinearDamping(1f);
		body.setFixedRotation(true);
		body.setUserData(entity);
		
		body.createFixture(fixtureDef);
	}
	
	public BodyComponent(Entity entity, World world, BodyDef.BodyType type, Shape shape, Vector3 pos, short category, short mask) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(pos.x, pos.y);

		body = world.createBody(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.friction = 0f;
		fixtureDef.shape = shape;
		fixtureDef.density = 0f;
		fixtureDef.restitution = 0f;
		fixtureDef.isSensor = true; 
		fixtureDef.filter.categoryBits = category; 
		fixtureDef.filter.maskBits = mask;
		
		body.setLinearDamping(1f);
		body.setFixedRotation(true);
		body.setUserData(entity);
		
		body.createFixture(fixtureDef);
	}
	
	public void setAsSensor(Boolean flag) {
		body.getFixtureList().get(0).setSensor(flag);
		
		Filter filter = new Filter();
		filter.groupIndex = flag ? (short)-1 : (short)1;
		body.getFixtureList().get(0).setFilterData(filter);
	}

}