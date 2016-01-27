package com.alphadelete.sandbox.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class BodyComponent implements Component {
    // Box2d CATEGORIES
	public final static short CATEGORY_PLAYER = 0x0001;  // 0000000000000001 in binary
    public final static short CATEGORY_MONSTER = 0x0002; // 0000000000000010 in binary
    public final static short CATEGORY_SCENERY = 0x0004; // 0000000000000100 in binary
    // Box2d MASKS
    public final static short MASK_PLAYER = CATEGORY_MONSTER | CATEGORY_SCENERY; // or ~CATEGORY_PLAYER
    public final static short MASK_MONSTER = CATEGORY_PLAYER | CATEGORY_SCENERY; // or ~CATEGORY_MONSTER
    public final static short MASK_SCENERY = -1;
    
	public Body body;
	
	public BodyComponent(World world, BodyDef.BodyType type, Shape shape, Vector3 pos, float density, float friction, float restitution, Boolean isSensor, short category, short mask) {
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
		
		body.createFixture(fixtureDef);
	}
	
	public BodyComponent(World world, BodyDef.BodyType type, Shape shape, Vector3 pos) {
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
		
		body.createFixture(fixtureDef);
	}
}