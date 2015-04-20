package matachi.mapeditor.grid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import matachi.mapeditor.editor.Constants;
import matachi.mapeditor.editor.GUIInformation;

/**
 * Takes inputs from the GridView and communicated with a Camera.
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 *
 */
public class GridController implements MouseListener, MouseMotionListener, ActionListener, KeyListener {

	/**
	 * The camera of the grid.
	 */
	private Camera camera;
	
	/**
	 * The last tile that the user clicked.
	 */
	private int lastClickedTileX;
	private int lastClickedTileY;
	
	/**
	 * The class that provides with GUI information.
	 */
	private GUIInformation guiInformation;
	
//	/**
//	 * If the drawingMode is on or off.
//	 */
//	private boolean drawingMode;

	/**
	 * The GridController which the GridView needs.
	 * @param camera The camera which the GridController will command.
	 * @param guiInformation The class that the GridController will query for
	 * information.
	 */
	public GridController(Camera camera, GUIInformation guiInformation) {
		this.camera = camera;
		this.guiInformation = guiInformation;
//		this.drawingMode = true;
	}

	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }
	
	/**
	 * If a mouse button is clicked.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		lastClickedTileX = e.getX() / Constants.TILE_WIDTH;
		lastClickedTileY = e.getY() / Constants.TILE_HEIGHT;
		if (ifLeftMouseButtonPressed(e)) {
			updateTile(lastClickedTileX, lastClickedTileY);
		}
		// if the tile clicked was an enemy
		char character = guiInformation.getSelectedTile().getCharacter();

		//System.out.println(character); // '5' for enemy

		if (guiInformation.getSelectedTile().getCharacter() == '5'){
			System.out.println("Need to display the direction inputs");
			// enable sidebar for text input
			guiInformation.enablePathText();
		} else {
			guiInformation.disablePathText();
		}
	}
	

	private boolean ifLeftMouseButtonPressed(MouseEvent e) {
		return (e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK;
	}
	
	private boolean ifRightMouseButtonPressed(MouseEvent e) {
		return (e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK;
	}
	
	private void updateTile(int xCor, int yCor) {
		xCor = Math.max(0, Math.min(xCor, Constants.GRID_WIDTH-1));
		yCor = Math.max(0, Math.min(yCor, Constants.GRID_HEIGHT-1));
		if (guiInformation.getSelectedTile() != null) {
			camera.setTile(xCor, yCor, guiInformation.getSelectedTile().getCharacter());
		}
	}
	
	private void updatePath(int xCor, int yCor) {
		xCor = Math.max(0, Math.min(xCor, Constants.GRID_WIDTH-1));
		yCor = Math.max(0, Math.min(yCor, Constants.GRID_HEIGHT-1));
		System.out.printf("Cursor was last at, %d, %d", xCor, yCor);
		System.out.println("Getting path string: " + guiInformation.getCurrentPathString());
		if (guiInformation.getCurrentPathString() != null) {
			camera.setPath(xCor, yCor, guiInformation.getCurrentPathString());
			System.out.println("path of object updated");
		}
	}
	
	

	private void updateCamera(int newTileX, int newTileY) {
		if (newTileX != lastClickedTileX) {
			if (newTileX - lastClickedTileX > 0) {
				camera.moveCamera(GridCamera.WEST);
			} else {
				camera.moveCamera(GridCamera.EAST);
			}
		}
		if (newTileY != lastClickedTileY) {
			if (newTileY - lastClickedTileY > 0) {
				camera.moveCamera(GridCamera.NORTH);
			} else {
				camera.moveCamera(GridCamera.SOUTH);
			}
		}
		lastClickedTileX = newTileX;
		lastClickedTileY = newTileY;
	}

	@Override
	public void mouseReleased(MouseEvent e) { }

	/**
	 * If the user keeps the mouse button pressed it will keep drawing if it is
	 * in drawing mode, which it is by default.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (ifRightMouseButtonPressed(e)) {
			int newTileX = e.getX() / Constants.TILE_WIDTH;
			int newTileY = e.getY() / Constants.TILE_HEIGHT;
			updateCamera(newTileX, newTileY);
		}
		this.mousePressed(e);
	}

	/**
	 * If the mouse cursor in moved.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) { }

	/**
	 * If a key is pressed down.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			camera.moveCamera(GridCamera.NORTH);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			camera.moveCamera(GridCamera.EAST);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			camera.moveCamera(GridCamera.SOUTH);
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			camera.moveCamera(GridCamera.WEST);
		} else if (e.getKeyCode() == KeyEvent.VK_S){
			updatePath(lastClickedTileX, lastClickedTileY);
			guiInformation.disablePathText();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) { }
	
	@Override
	public void keyTyped(KeyEvent e) { }
}
