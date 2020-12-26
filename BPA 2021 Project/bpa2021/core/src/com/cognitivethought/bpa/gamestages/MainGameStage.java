package com.cognitivethought.bpa.gamestages;

import java.util.HashMap;

import com.backendless.Backendless;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.external.GifDecoder;
import com.cognitivethought.bpa.game.Player;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.multiplayer.TurnPacket;
import com.cognitivethought.bpa.prefabs.Card;
import com.cognitivethought.bpa.prefabs.GameMap;
import com.cognitivethought.bpa.tidiness.Strings;

public class MainGameStage extends GameStage {

	public static boolean warInitiated = false;
	
	public HashMap<String, Player> players = new HashMap<String, Player>();
	
	public Player currentPlayer;
	public Player clientPlayer;
	public Label fps;
	
	public ImageButton nextTurn;
	
	public GameMap map;
	public InputMultiplexer im = new InputMultiplexer();
	
	public float frameTime = 0f;
	
	public Sprite dark;
	
	public boolean enableDark = false;

	public int clickedCountry;

	public Card executingCard;
	
	public Animation<TextureRegion> background;
	float elapsed;
	
	public MainGameStage(Viewport vp) {
		super(vp);
		
		// NOTE: GifDecoder written by Johannes Borchardt and converted to the latest LibGDX version by Anton Persson
		background = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, new FileHandle(Strings.URL_PLACEMAT_BACKGROUND).read());
		
		dark = new Sprite(new Texture(Strings.URL_IMG_UI_DARK));
		executingCard = Card.BLANK;
	}
	
	@Override
	public void populate() {
		super.populate();
		
		im.addProcessor(this);
		
		currentPlayer = players.get((String)Backendless.UserService.CurrentUser().getProperty("name"));
		
		currentPlayer.populate(this);
		clientPlayer = currentPlayer;
		
		nextTurn = new ImageButton(new Image(new Texture(Strings.URL_NEXT_TURN)).getDrawable());
		nextTurn.setPosition(clientPlayer.placemat.getLeft().getX() - (currentPlayer.placemat.getLeft().getWidth() / 2), clientPlayer.placemat.getLeft().getY() - clientPlayer.placemat.getLeft().getHeight() - 40);
		
		// TODO: Make it so that the turn can not be ended if any of the three center cards are empty, and implement turns using queue
		
		final MainGameStage mgs = this;
		nextTurn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				clientPlayer.placemat.advance(mgs, clientPlayer.username);
				super.clicked(event, x, y);
			}
		});
		
		fps = new Label("", labelStyle);
		fps.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getWidth() / 2);
		
		map = new GameMap();
		map.setWidth(clientPlayer.placemat.getWidth() * 1.5f);
		map.setHeight((map.getWidth() * 224) / 400); // 224 height
		map.setPosition(clientPlayer.placemat.getX() - (map.getWidth() / 6), ((clientPlayer.hand.getY() + clientPlayer.placemat.getY()) / 2) - (map.getHeight() / 3));
		
		for (String s : players.keySet()) {
			System.out.println("Added player " + s + " playing as " + GameMap.idToString(players.get(s).country_id));
			map.addPlayer(players.get(s));
		}

		addActor(fps);
		addActor(map);
		for (int i = 1; i < map.countries.length; i++) {
			addActor(map.countries[i]);
		}
		addActor(clientPlayer);
		addActor(nextTurn);
		
//		setDebugAll(true);
	}

	@Override
	public void clearFields() {

	}

	@Override
	public void draw() {
		if (Launcher.currentStage == Launcher.game_stage) {
			if (Gdx.input.getInputProcessor() != this) {
				Gdx.input.setInputProcessor(this);
			}
		}

		Gdx.gl.glClearColor(0f, 0.25f, 0f, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		elapsed += Gdx.graphics.getDeltaTime() * 2.5f;
		getBatch().begin();
		getBatch().setColor(0f, 1f, 0f, 0.9f);
		getBatch().draw(background.getKeyFrame(elapsed), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		getBatch().end();
		
		super.draw();
		
		nextTurn.setPosition(clientPlayer.placemat.getLeft().getX() - (currentPlayer.placemat.getLeft().getWidth() / 2), clientPlayer.placemat.getLeft().getY() - clientPlayer.placemat.getLeft().getHeight() - 40);
		
		frameTime += Gdx.graphics.getDeltaTime();
		if (frameTime > 0.1) {
			fps.setText(Integer.toString((int) (1 / Gdx.graphics.getDeltaTime())));
			frameTime = 0f;
		}
		
		checkOnlyOneCountrySelected();
		
		if (enableDark) {
			getBatch().begin();
			getBatch().setColor(1, 1, 1, 0.5f);
			getBatch().draw(dark, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			getBatch().setColor(1.0f, 1.0f, 1.0f, 1.0f);
			getBatch().end();
		}
		
		clientPlayer.setVisible(!enableDark);
		nextTurn.setVisible(!enableDark);
		
		if (clickedCountry > 0 && (!executingCard.equals(Card.BLANK))) {
			executingCard.play(this, clickedCountry);
			clickedCountry = 0;
			executingCard = clientPlayer.placemat.getTopCard();
		}
		
		for (int i = 1; i < map.countries.length; i++) {
			if (map.countries[i].isClicked) {
				System.out.println(map.countries[i].getAssignedPlayer().username + " has " + map.countries[i].getAssignedPlayer().pop_i + "M population");
				map.countries[i].isClicked = false;
				break;
			}
		}
	}
	
	public void checkOnlyOneCountrySelected() {
		boolean[] selected = new boolean[map.countries.length];
		int trueSize = 0;
		for (int i = 1; i < map.countries.length; i++) {
			selected[i] = map.countries[i].selected;
			if (selected[i]) trueSize++;
		}
		
		if (trueSize > 1) {
			for (int i = 1; i < map.countries.length; i++) {
				map.countries[i].selected = false;
			}
		}
	}
	
	public void executeTurn(TurnPacket request) {
		request.execute(this);
	}

	public void selectTarget(Card card) {
		final MainGameStage mgs = this;
		this.executingCard  = card;
		new Thread() {
			@Override
			public void run() {
				setName("selectTarget");
				mgs.enableDark = true;
				boolean wait = true;
				int i = 1;
				while (wait) {
					for (i = 1; i < mgs.map.countries.length; i++) {
						if (mgs.map.countries[i].isClicked) {
							wait = false;
							break;
						}
					}
				}
			}
			
			@Override
			public void destroy() {
				System.out.println("CLICKED A COUNTRY");
				mgs.enableDark = false;
			};
		}.start();
	}
}