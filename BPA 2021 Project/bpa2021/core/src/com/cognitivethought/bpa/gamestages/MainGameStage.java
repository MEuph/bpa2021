package com.cognitivethought.bpa.gamestages;

import java.util.HashMap;

import com.backendless.Backendless;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.game.Player;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.multiplayer.TurnPacket;
import com.cognitivethought.bpa.tidiness.Strings;

public class MainGameStage extends GameStage {

	public static final TooltipManager MANAGER = new TooltipManager();
	public static boolean warInitiated = false;
	
	public HashMap<String, Player> players = new HashMap<String, Player>();
	
	public Player currentPlayer;
	public Player clientPlayer;
	public Label fps;
	
	public ImageButton nextTurn;
	
	public TurnPacket turn;
	
	public float frameTime = 0f;

	public MainGameStage(Viewport vp) {
		super(vp);
		MANAGER.instant();
		
	}

	@Override
	public void populate() {
		super.populate();
		
		players.put(Backendless.UserService.CurrentUser().getUserId(), new Player());
		currentPlayer = players.get(Backendless.UserService.CurrentUser().getUserId());
		
		currentPlayer.populate(this);
		clientPlayer = currentPlayer;
		
		nextTurn = new ImageButton(new Image(new Texture(Strings.URL_NEXT_TURN)).getDrawable());
		nextTurn.setPosition(clientPlayer.placemat.getLeft().getX() - (currentPlayer.placemat.getLeft().getWidth() / 2), clientPlayer.placemat.getLeft().getY() - clientPlayer.placemat.getLeft().getHeight() - 40);

		nextTurn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				currentPlayer.placemat.advance();
			}
		});
		
		fps = new Label("", labelStyle);
		fps.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getWidth() / 2);

		addActor(clientPlayer);
		addActor(fps);
		addActor(nextTurn);
	}

	@Override
	public void clearFields() {

	}

	@Override
	public void draw() {
		if (Launcher.currentStage == Launcher.dev_stage) {
			if (Gdx.input.getInputProcessor() != this) {
				Gdx.input.setInputProcessor(this);
			}
		}

		Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		super.draw();
		
		nextTurn.setPosition(clientPlayer.placemat.getLeft().getX() - (currentPlayer.placemat.getLeft().getWidth() / 2), clientPlayer.placemat.getLeft().getY() - clientPlayer.placemat.getLeft().getHeight() - 40);
		
		frameTime += Gdx.graphics.getDeltaTime();
		if (frameTime > 0.1) {
			fps.setText(Integer.toString((int) (1 / Gdx.graphics.getDeltaTime())));
			frameTime = 0f;
		}
	}

	public void executeTurn(TurnPacket request) {
		request.execute(this);
	}
}