package edu.teamWat.rhythmKnights.alpha.controllers;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;


import com.badlogic.gdx.utils.BooleanArray;
import edu.teamWat.rhythmKnights.alpha.JSONReader;
import edu.teamWat.rhythmKnights.alpha.models.*;
import edu.teamWat.rhythmKnights.alpha.models.gameObjects.*;

public class GameplayController {
	/** Reference to the game board */
	public Board board;
	/** Reference to all the game objects in the game */
	public static GameObjectList gameObjects;
	/** List of all the input (both player and AI) controllers */
	public static InputController[] controls;
	/** Ticker */
	public Ticker ticker;
	
	private Knight knight;

	public static PlayerController playerController;

	public CollisionController collisionController;

	private boolean gameOver = false;

	public boolean gameStateAdvanced;

	private final int DOT_HP = 3;
	private int timeHP = DOT_HP;
	private boolean hasMoved = false;

	public boolean calibrate;

    private int backgroundNum = 0;

	public GameplayController() {
	}


	public void initialize(int levelNum) {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));

		String path = (System.getProperty("user.dir"));
		path = path.substring(path.length() - 6);
		System.out.println(path);

		FileHandle levelhandle = Gdx.files.internal("levels/level" + levelNum + ".json");
		FileHandle audiohandle;
		board = JSONReader.parseFile(levelhandle.readString());
		audiohandle = JSONReader.getAudioHandle();
		
		
//		if (path.equals("assets")) {
//			board = JSONReader.parseFile("levels/level" + levelNum + ".json");
//			audio = JSONReader.getAudio(false);
//		} else {
//			board = JSONReader.parseFile(System.getProperty("user.dir") + "/beta/levels/level" + levelNum + ".json");
//			audio = JSONReader.getAudio(true);
//		}
		JSONReader.getObjects();
		ticker = JSONReader.initializeTicker();



		// Preallocate memory
		ProjectilePool projs = new ProjectilePool();

		collisionController = new CollisionController(board, gameObjects, projs);

		knight = (Knight)gameObjects.getPlayer();
		knight.setInvulnerable(true);
		hasMoved = false;
		gameOver = false;
		try {
			RhythmController.init(audiohandle, ticker);
		} catch (Exception e) {
			e.printStackTrace();
		}

		RhythmController.launch();
	}

//	int moved = 0;

	public void update() {
		if (hasMoved) {
			timeHP--;
			if (timeHP == 0) {
				knight.decrementHP();
				timeHP = DOT_HP;
			}
		}
		gameStateAdvanced = false;

		board.updateColors();

		if (playerController.didReset) {
			playerController.clear();
			gameOver = true;
			return;
		}

		for (GameObject gameObject : gameObjects) {
			gameObject.update();
		}

		long currentTick = RhythmController.getSequencePosition();
		int prevActionIndex = RhythmController.getClosestEarlierActionIndex(currentTick);
		int nextActionIndex = (prevActionIndex + 1) % RhythmController.numActions;
		ticker.setBeat(RhythmController.convertToTickerBeatNumber(prevActionIndex, ticker));
		if (RhythmController.getTickerAction(prevActionIndex) == Ticker.TickerAction.FIREBALL2 || RhythmController.getTickerAction(prevActionIndex) == Ticker.TickerAction.DASH2) {
			prevActionIndex--;
		} else if (RhythmController.getTickerAction(nextActionIndex) == Ticker.TickerAction.FIREBALL2 || RhythmController.getTickerAction(nextActionIndex) == Ticker.TickerAction.DASH2) {
			nextActionIndex++;
		}
		ticker.indicatorOffsetRatio = ((float)currentTick - (float)RhythmController.getTick(prevActionIndex) - RhythmController.totalOffset / 2) / ((float)RhythmController.getTick(nextActionIndex) - (float)RhythmController.getTick(prevActionIndex));
		board.setDistanceToBeat(Math.abs(ticker.indicatorOffsetRatio - 0.5f));

		int currentActionIndex;
		// Keep clearing the action ahead of us! Simple! :D
		RhythmController.clearNextAction(nextActionIndex);

		if (knight.isAlive()) {
			for (PlayerController.KeyEvent keyEvent : playerController.keyEvents) {
				prevActionIndex = RhythmController.getClosestEarlierActionIndex(keyEvent.time);
				nextActionIndex = (prevActionIndex + 1) % RhythmController.numActions;
				if (nextActionIndex < prevActionIndex) {
					if (RhythmController.getTrackLength() - keyEvent.time > keyEvent.time - RhythmController.getTick(prevActionIndex)) {
						currentActionIndex = prevActionIndex;
					} else {
						currentActionIndex = nextActionIndex;
					}
				} else {
					if (RhythmController.getTick(nextActionIndex) - keyEvent.time > keyEvent.time - RhythmController.getTick(prevActionIndex)) {
						currentActionIndex = prevActionIndex;
					} else {
						currentActionIndex = nextActionIndex;
					}
				}

				if ((keyEvent.code & InputController.CONTROL_RELEASE) == 0 && RhythmController.getCompleted(currentActionIndex)) {
					if (knight.isAlive()) damagePlayer();
				} else {
					switch (RhythmController.getTickerAction(currentActionIndex)) {
						case MOVE:
							if ((keyEvent.code & PlayerController.CONTROL_RELEASE) != 0) break;

							if (knight.getPosition().y == 4) {
								double a = 0;
							}
							RhythmController.setCompleted(currentActionIndex, true);
							RhythmController.setPlayerAction(currentActionIndex, keyEvent.code);
							ticker.glowBeat(RhythmController.convertToTickerBeatNumber(currentActionIndex, ticker), 15);
							Vector2 vel = new Vector2(0, 0);
							switch (keyEvent.code) {
								case PlayerController.CONTROL_MOVE_RIGHT:
									vel.x = 1;
									knight.setDirection(Knight.KnightDirection.RIGHT);
									break;
								case PlayerController.CONTROL_MOVE_UP:
									vel.y = 1;
									knight.setDirection(Knight.KnightDirection.BACK);
									break;
								case PlayerController.CONTROL_MOVE_LEFT:
									vel.x = -1;
									knight.setDirection(Knight.KnightDirection.LEFT);
									break;
								case PlayerController.CONTROL_MOVE_DOWN:
									vel.y = -1;
									knight.setDirection(Knight.KnightDirection.FRONT);
									break;
							}
							hasMoved = true;
							knight.setVelocity(vel);
							advanceGameState();
							RhythmController.playSuccess();

							// Display visual feedback to show success
							if (knight.isAlive()) knight.showSuccess();
							// Set current tile type to SUCCESS
							board.setSuccess((int)knight.getPosition().x, (int)knight.getPosition().y);
							RhythmController.playSuccess();

							break;
						case DASH:
							if ((keyEvent.code & PlayerController.CONTROL_RELEASE) != 0) break;
							RhythmController.setCompleted(currentActionIndex, true);
							RhythmController.setPlayerAction(currentActionIndex, keyEvent.code);
							switch (keyEvent.code) {
								case PlayerController.CONTROL_MOVE_RIGHT:
									knight.setDirection(Knight.KnightDirection.RIGHT);
									break;
								case PlayerController.CONTROL_MOVE_UP:
									knight.setDirection(Knight.KnightDirection.BACK);
									break;
								case PlayerController.CONTROL_MOVE_LEFT:
									knight.setDirection(Knight.KnightDirection.LEFT);
									break;
								case PlayerController.CONTROL_MOVE_DOWN:
									knight.setDirection(Knight.KnightDirection.FRONT);
									break;
							}
							break;
						case DASH2:
							if ((keyEvent.code & PlayerController.CONTROL_RELEASE) != 0) break;
							RhythmController.setCompleted(currentActionIndex, true);
							RhythmController.setPlayerAction(currentActionIndex, keyEvent.code);
							ticker.glowBeat(RhythmController.convertToTickerBeatNumber(currentActionIndex, ticker), 15);
							if (RhythmController.getPlayerAction(currentActionIndex) != RhythmController.getPlayerAction(currentActionIndex - 1)) {
								if (knight.isAlive()) damagePlayer();
							} else {
								vel = new Vector2(0, 0);
								switch (keyEvent.code) {
									case PlayerController.CONTROL_MOVE_RIGHT:
										vel.x = 2;
										break;
									case PlayerController.CONTROL_MOVE_UP:
										vel.y = 2;
										break;
									case PlayerController.CONTROL_MOVE_LEFT:
										vel.x = -2;
										break;
									case PlayerController.CONTROL_MOVE_DOWN:
										vel.y = -2;
										break;
								}
								knight.setVelocity(vel);
								advanceGameState();
								RhythmController.playSuccess();

								// Display visual feedback to show success
								if (knight.isAlive()) knight.showSuccess();
								hasMoved = true;
//							knight.setDashing();
								// Set current tile type to SUCCESS
								board.setSuccess((int)knight.getPosition().x, (int)knight.getPosition().y);
								RhythmController.playSuccess();
							}
							break;
						case FIREBALL:
							if ((keyEvent.code & PlayerController.CONTROL_RELEASE) != 0) break;
							break;
						case FIREBALL2:
							if ((keyEvent.code & PlayerController.CONTROL_RELEASE) == 0) break;
							break;
					}
				}
			}
		}

		playerController.clear();

		prevActionIndex = RhythmController.getClosestEarlierActionIndex(currentTick);
		nextActionIndex = (prevActionIndex + 1) % RhythmController.numActions;

		if (nextActionIndex < prevActionIndex) {
			if (RhythmController.getTrackLength() - currentTick > currentTick - RhythmController.getTick(prevActionIndex)) {
				currentActionIndex = prevActionIndex;
			} else {
				currentActionIndex = nextActionIndex;
			}
		} else {
			if (RhythmController.getTick(nextActionIndex) - currentTick > currentTick - RhythmController.getTick(prevActionIndex)) {
				currentActionIndex = prevActionIndex;
			} else {
				currentActionIndex = nextActionIndex;
			}
		}

		if ((currentActionIndex == nextActionIndex) && !RhythmController.getCompleted(prevActionIndex)) {
			RhythmController.setCompleted(prevActionIndex, true);
			if (knight.isAlive()) damagePlayer();
			if (RhythmController.getTickerAction(prevActionIndex) != Ticker.TickerAction.DASH2 && RhythmController.getTickerAction(prevActionIndex) != Ticker.TickerAction.FIREBALL2) {
				advanceGameState();
			}
		}
	}

	private void advanceGameState() {
		if (gameStateAdvanced) return;
		gameStateAdvanced = true;
		moveEnemies();
		collisionController.update();
		if (collisionController.hasPlayerMoved) knight.setInvulnerable(false);
	}

	public void damagePlayer() {
		if (!knight.isInvulnerable()) {
//			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
//				System.out.println(ste);
//			}
			knight.takeDamage();
			knight.setInvulnerable(true);
		}
	}

	public void moveEnemies() {
		Vector2 vel = new Vector2();
		for (int i = 1; i < controls.length; i++) {
			vel.set(0, 0);
			switch (controls[i].getAction()) {
				case InputController.CONTROL_MOVE_RIGHT:
					vel.x = 1;
					break;
				case InputController.CONTROL_MOVE_UP:
					vel.y = 1;
					break;
				case InputController.CONTROL_MOVE_LEFT:
					vel.x = -1;
					break;
				case InputController.CONTROL_MOVE_DOWN:
					vel.y = -1;
					break;
			}
			((EnemyController)controls[i]).nextAction();
			gameObjects.get(i).setVelocity(vel);
		}
	}

	public boolean isGameOver() {
		return gameOver;
	}
}