package edu.teamWat.rhythmKnights.technicalPrototype.models;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import edu.teamWat.rhythmKnights.technicalPrototype.utils.FilmStrip;
import edu.teamWat.rhythmKnights.technicalPrototype.views.GameCanvas;

public class Board {
    /* Width of current board */
    public int width;
    /* Height of current board */
    public int height;

    private final int TILE_SIZE = 80;

    /* Variables for tile sprite */
    public static final String TILE_FILE = "images/tileFull.png";
    public static Texture tileTexture;

    /* Holds the array of tiles that make up this board */
    private Tile[][] tiles;

    /** Initialize a board with dimensions w x h with empty tiles*/
    public Board(int w, int h){
        width = w;
        height = h;
        tiles = new Tile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i][j] = new Tile();
            }
        }
    }

    /** Clears the board by calling clear on each tile, but maintains dimensions. */
    public void clear() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i][j].clear();
            }
        }
    }

    /* Destroy the board by making it null (you need to initialize it again after this)*/
    public void destroy(){
        tiles = null;
    }

    public void update(float delta) {
        // TODO: Update the board; E.g state of each tile
        // Might not need this method.
    }

    /*Set a certain tile to have all listed variables*/
    public void setTile(int x, int y, boolean goal, boolean start, boolean knight, boolean enemy, boolean obstacle){
        this.tiles[x][y].isGoal = goal;
        this.tiles[x][y].isStart = start;
        //this.tiles[x][y].isKnight = knight;
        //this.tiles[x][y].isEnemy = enemy;
        this.tiles[x][y].isObstacle = obstacle;
    }

    public void draw(GameCanvas canvas){
        for (int i=0; i<this.width; i++){
            for (int j=0; j<this.height; j++){
                Vector2 loc = canvas.boardToScreen(i,j);
                Color c = tiles[i][j].getColor();
                //texture, color, sprite origin x/y, x/y offset, angle, scale x/y
                canvas.draw(tileTexture, c, 0, 0, loc.x, loc.y, 0, 1, 1);
            }
        }
    }

    /* Returns if the tile at (x,y) is a goal tile */
    public boolean isGoalTile(int x, int y){
        return this.tiles[x][y].isGoal;
    }

    /* Returns if the tile at (x,y) is the start tile */
    public boolean isStartTile(int x, int y){
        return this.tiles[x][y].isStart;
    }

    /* Returns if the tile at (x,y) is the knight's location */
    public boolean isKnightTile(int x, int y){
        return this.tiles[x][y].isKnight;
    }

    /* Returns if the tile at (x,y) contains an enemy */
    public boolean isEnemyTile(int x, int y){
        return this.tiles[x][y].isEnemy;
    }

    /* Returns if the tile at (x,y) is an obstacle (and therefore impassable) */
    public boolean isObstacleTile(int x, int y){
        return this.tiles[x][y].isObstacle;
    }

    /**
     * Preloads the assets for the Tile.
     *
     * The asset manager for LibGDX is asynchronous.  That means that
     * you tell it what to load and then wait while it
     * loads them.  This is the first step: telling it what to load.
     *
     * @param manager Reference to global asset manager.
     */
    public static void PreLoadContent(AssetManager manager) {
        manager.load(TILE_FILE, Texture.class);
    }

    /**
     * Loads the assets for the Tile.
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
        if (manager.isLoaded(TILE_FILE)) {
            tileTexture = manager.get(TILE_FILE,Texture.class);
            tileTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } else {
            tileTexture = null;  // Failed to load
        }
    }

    /**
     * Unloads the assets for the Tile
     *
     * This method erases the static variables.  It also deletes the associated textures from the assert manager.
     *
     * @param manager Reference to global asset manager.
     */
    public static void UnloadContent(AssetManager manager) {
        if (tileTexture != null) {
            tileTexture = null;
            manager.unload(TILE_FILE);
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float boardToScreen(int i) {
        // TODO Auto-generated method stub
        return 0;
    }



    /**
     * Each tile on the board has a set of attributes associated with it.
     */
    public static class Tile {
        /** Is this a goal tile?*/
        public boolean isGoal;
        /** Is this a start tile?*/
        public boolean isStart;
        /** Is the knight on this tile?*/
        public boolean isKnight;
        /** Is an enemy on this tile?*/
        public boolean isEnemy;
        /** Is there an obstacle on this tile?*/
        public boolean isObstacle;

        public Tile() {
            isGoal = false;
            isStart = false;
            isKnight = false;
            isEnemy = false;
            isObstacle = false;
        }

        public void clear() {
            isGoal = false;
            isStart = false;
            isKnight = false;
            isEnemy = false;
            isObstacle = false;
        }

        public Color getColor(){
            if (this.isStart){
                return Color.CYAN;
            }
            if (this.isObstacle){
                return Color.LIGHT_GRAY;
            }
            if (this.isGoal){
                return Color.GREEN;
            }
            return Color.MAGENTA;
        }
    }


}
