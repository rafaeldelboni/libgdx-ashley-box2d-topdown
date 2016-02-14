package com.alphadelete.sandbox.systems;

import java.util.Comparator;

import com.alphadelete.sandbox.components.TransformComponent;
import com.alphadelete.sandbox.Constants;
import com.alphadelete.sandbox.components.TextureComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RenderingSystem extends IteratingSystem {
	private SpriteBatch batch;
	private Array<Entity> renderQueue;
	private Comparator<Entity> comparator;
	private OrthographicCamera cam;
	private Viewport viewport;
		
	private ComponentMapper<TextureComponent> textureM;
	private ComponentMapper<TransformComponent> transformM;
	
	private World world;
	private Matrix4 debugMatrix;
	private Box2DDebugRenderer debugRenderer;
	
	@SuppressWarnings("unchecked")
	public RenderingSystem(SpriteBatch batch, World world) {
		super(Family.all(TransformComponent.class, TextureComponent.class).get());
		
		textureM = ComponentMapper.getFor(TextureComponent.class);
		transformM = ComponentMapper.getFor(TransformComponent.class);
		
		renderQueue = new Array<Entity>();
		
		comparator = new Comparator<Entity>() {
			@Override
			public int compare(Entity entityA, Entity entityB) {
				return (int)Math.signum(transformM.get(entityB).pos.z -
										transformM.get(entityA).pos.z);
			}
		};
		
		this.batch = batch;
		
		float meterCamX = (Constants.APP_WIDTH * Constants.PIXELS_TO_METRES);
		float meterCamy = (Constants.APP_HEIGHT * Constants.PIXELS_TO_METRES);
		
		cam = new OrthographicCamera();
		viewport = new FitViewport(meterCamX, meterCamy, cam);
		cam.zoom = Constants.CAMERA_ZOOM;		
		
		this.world = world;
		if(Constants.GAME_DEBUG) {			
			debugMatrix = new Matrix4(cam.combined);
			debugRenderer = new Box2DDebugRenderer();
	        debugRenderer.setDrawVelocities(true);
	        debugRenderer.setDrawBodies(true);
	        debugRenderer.setDrawJoints(true);
	        debugRenderer.setDrawContacts(true);
		}
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderQueue.sort(comparator);
		
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		
		for (Entity entity : renderQueue) {
			TextureComponent tex = textureM.get(entity);
			
			if (tex.region == null) {
				continue;
			}
			
			TransformComponent t = transformM.get(entity);
		
			float width = tex.region.getRegionWidth();
			float height = tex.region.getRegionHeight();
			
			float originX = width * 0.5f;
			float originY = height * 0.5f;
			// if origin is set on transform use it
			if (t.isOriginSet()) {
				batch.draw(tex.region,
							t.pos.x - t.origin.x, t.pos.y - t.origin.y,
							t.origin.x, t.origin.y,
							width, height,
							t.scale.x * Constants.PIXELS_TO_METRES, t.scale.y * Constants.PIXELS_TO_METRES,
							MathUtils.radiansToDegrees * t.rotation);
			// else get the middle point in texture
			} else {
				batch.draw(tex.region,
							t.pos.x - originX, t.pos.y - originY,
							originX, originY,
							width, height,
							t.scale.x * Constants.PIXELS_TO_METRES, t.scale.y * Constants.PIXELS_TO_METRES,
							MathUtils.radiansToDegrees * t.rotation);
			}
		}
		
		batch.end();
		renderQueue.clear();
		
		if (Constants.GAME_DEBUG) {
			debugMatrix.set(cam.combined);
        	debugRenderer.render(world, debugMatrix);
		}
		Gdx.graphics.setTitle(String.valueOf(Gdx.app.getGraphics().getFramesPerSecond()));
		
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		renderQueue.add(entity);
	}
	
	public void resizeCamera(int width, int height) {
		viewport.update(width, height, false);
	}
	
	public OrthographicCamera getCamera() {
		return cam;
	}
}