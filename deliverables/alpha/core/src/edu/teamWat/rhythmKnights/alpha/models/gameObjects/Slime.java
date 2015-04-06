package edu.teamWat.rhythmKnights.alpha.models.gameObjects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import edu.teamWat.rhythmKnights.alpha.utils.FilmStrip;
import edu.teamWat.rhythmKnights.alpha.views.GameCanvas;

public class Slime extends GameObject{
    
    public static final String SLIME_FILE = "images/slime.png";
    public static Texture slimeTexture;
    private FilmStrip sprite;

    // The number of frames before a sprite refreshes
    private int animDelay = 5;
    private int curTime = 5;
    private int curFrame = 0;

    // Constants for reference to the spritesheet
    private int IDLE_START = 0;
    private int IDLE_END = 5;
    private int HURT_START = 6;
    private int HURT_END = 11;
    private int SPRITE_ROWS = 2;
    private int SPRITE_COLS = 6;
    private int SPRITE_TOT = 12;

    public Slime(int id, float x, float y){
        this.id = id;
        this.position = new Vector2(x,y);
	    this.animatedPosition.set(position);
	    this.oldPosition.set(position);
        isAlive = true;
        isActive = true;
    }

    public void update() {
        // If we are dead do nothing.
        if (!isAlive) {
            return;
        }
	    if (moved) {
		    animAge++;
		    if (animAge == animFrames) {
			    animAge = 0;
			    animatedPosition.set(position);
			    oldPosition.set(position);
			    moved = false;
		    } else {
			    animatedPosition.set(position).sub(oldPosition).scl((float)animAge / animFrames).add(oldPosition);
		    }
	    }
        //TODO: implement this

    }

    public void draw(GameCanvas canvas) {
        //        curTime --;
//        if (curTime == 0) {
//            curFrame ++;
//            if (curFrame >= IDLE_END) {
//                curFrame = IDLE_START;
//            }
//            curTime = animDelay;
//        } else {
//            sprite.setFrame(curFrame);
//        }

        sprite = new FilmStrip(slimeTexture, 1, 1);
	    Vector2 loc = canvas.boardToScreen(animatedPosition.x, animatedPosition.y);
        canvas.draw(sprite, loc.x, loc.y, canvas.tileSize, canvas.tileSize);
    }


    //	/**
//	 * Moves the knight based on the input code*/
//	public void move(int code){
//
//	}
    /**
     * Preloads the assets for the Knight.
     *
     * The asset manager for LibGDX is asynchronous.  That means that
     * you tell it what to load and then wait while it
     * loads them.  This is the first step: telling it what to load.
     *
     * @param manager Reference to global asset manager.
     */
    public static void PreLoadContent(AssetManager manager) {
        manager.load(SLIME_FILE, Texture.class);
    }

    /**
     * Loads the assets for the Knight.
     *
     * All shell objects use one of two textures, so this is a static method.
     * This keeps us from loading the same images
     * multiple times for more than one Shell object.
     *
     * The asset manager for LibGDX is asynchronous.  That means that you tell it what to load and then wait while it
     * loads them.  This is the second step: extracting assets from the manager after it has finished loading them.
     *
     * @param manager Reference to global asset manager.
     */
    public static void LoadContent(AssetManager manager) {
        if (manager.isLoaded(SLIME_FILE)) {
            slimeTexture = manager.get(SLIME_FILE,Texture.class);
            slimeTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } else {
            slimeTexture = null;  // Failed to load
        }
    }

    /**
     * Unloads the assets for the Knight
     *
     * This method erases the static variables.  It also deletes the associated textures from the assert manager.
     *
     * @param manager Reference to global asset manager.
     */
    public static void UnloadContent(AssetManager manager) {
        if (slimeTexture != null) {
            slimeTexture = null;
            manager.unload(SLIME_FILE);
        }
    }


}