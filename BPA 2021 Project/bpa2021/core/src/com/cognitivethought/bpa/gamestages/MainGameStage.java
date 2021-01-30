package com.cognitivethought.bpa.gamestages;

import java.util.Arrays;
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
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.external.GifDecoder;
import com.cognitivethought.bpa.game.Player;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.multiplayer.NuclearWarServer;
import com.cognitivethought.bpa.multiplayer.StringPacket;
import com.cognitivethought.bpa.multiplayer.TurnPacket;
import com.cognitivethought.bpa.prefabs.Card;
import com.cognitivethought.bpa.prefabs.GameMap;
import com.cognitivethought.bpa.sound.Sounds;
import com.cognitivethought.bpa.tidiness.Strings;

public class MainGameStage extends GameStage {

	public boolean warInitiated = false;

	public HashMap<String, Player> players = new HashMap<String, Player>();

	public Player currentPlayer;
	public Player clientPlayer;
	public Label fps;

	public ImageButton nextTurn;
	public TextButton returnToMenu;

	public GameMap map;
	public InputMultiplexer im = new InputMultiplexer();

	public float frameTime = 0f;

	public Sprite dark;
	
	public boolean enableDark = false;
	
	public int clickedCountry;

	public Card executingCard;

	public Animation<TextureRegion> warDeclaration;
	public Animation<TextureRegion> background;
	float elapsed;

	float drawnTime = 0f;
	
	String[] turns;

	public Label pleaseWait;
	
	public boolean hasWon = false;

	public boolean shouldSendData = false;
	
	public MainGameStage(Viewport vp) {
		super(vp);
	}

	@Override
	public void populate() {
		super.populate();

		int vol_i = (int) Backendless.UserService.CurrentUser().getProperty("nw_volume");
		float vol = (float)(vol_i) / 100f;
		Sounds.music_intro.stop();
		Sounds.music_war.stop();
		Sounds.music_queue.stop();
		Sounds.music_queue.setLooping(Sounds.music_queue.play(vol), true);
		
		addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				if (character == 'g') {
					System.out.println("Printing deck");
					System.out.println(Arrays.toString(NuclearWarServer.DECK.toArray()));
				}

				return super.keyTyped(event, character);
			}
		});

		// NOTE: GifDecoder written by Johannes Borchardt and converted to the latest
		// LibGDX version by Anton Persson
		background = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP,
				new FileHandle(Strings.URL_PLACEMAT_BACKGROUND).read());
		
		warDeclaration = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, new FileHandle(Strings.URL_ALERT_ATTACK_GIF).read());
		
		dark = new Sprite(new Texture(Strings.URL_IMG_UI_DARK));
		executingCard = Card.BLANK;
		
		warInitiated = false;
		
		im.addProcessor(this);

		currentPlayer = players.get(Backendless.UserService.CurrentUser().getProperty("name").toString());

		clientPlayer = currentPlayer;
		
		for (String s : players.keySet()) {
			System.out.println("Is client? " + s.equals(Backendless.UserService.CurrentUser().getProperty("name").toString()));
			players.get(s).populate(this, s.equals(Backendless.UserService.CurrentUser().getProperty("name").toString()), s);
		}
		
		turns = new String[players.keySet().size()];
		int j = 0;
		for (String s : players.keySet()) {
			turns[j] = s;
			j++;
		}
		
		nextTurn = new ImageButton(new Image(new Texture(Strings.URL_NEXT_TURN)).getDrawable());
		nextTurn.setPosition(
				clientPlayer.placemat.getLeftCard().getX() - (currentPlayer.placemat.getLeftCard().getWidth() / 2),
				clientPlayer.placemat.getLeftCard().getY() - clientPlayer.placemat.getLeftCard().getHeight() - 40);
		
		final MainGameStage mgs = this;
		nextTurn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (clientPlayer.username.equals(turns[NuclearWarServer.currentTurn])) {
					clientPlayer.placemat.advance(mgs, clientPlayer.username);
					new Thread() {
						public void run() {
							while (!mgs.shouldSendData) {
								try {
									sleep(1);
								} catch (InterruptedException e) {
									e.printStackTrace();
						        	Launcher.log();
								}
							}
							
							if (mgs.shouldSendData) {
								NuclearWarServer.client.sendTCP(clientPlayer.tp);
								clientPlayer.tp.reset();
								mgs.shouldSendData = false;
								try {
									this.join();
								} catch (InterruptedException e) {
									e.printStackTrace();
						        	Launcher.log();
								}
							}
						};
					}.start();
				}
				super.clicked(event, x, y);
			}
		});

		fps = new Label("00", labelStyle);
		fps.setPosition(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() * 1.5f) - fps.getHeight());
		
		returnToMenu = new TextButton("Quit to Menu", buttonStyle);
		returnToMenu.pad(10f);
		returnToMenu.setPosition(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() * 1.5f) - fps.getHeight() - returnToMenu.getHeight() - 15);
		returnToMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				NuclearWarServer.closeServer();
				NuclearWarServer.disconnectClient();
				Sounds.music_queue.stop();
				Launcher.setStage(Launcher.game_menu_stage);
				super.clicked(event, x, y);
			}
		});
		
		
		pleaseWait = new Label("Please wait for your turn...", labelStyle);
		pleaseWait.setFontScale(0.75f);
		pleaseWait.setPosition(Gdx.graphics.getWidth() - (pleaseWait.getWidth() / 2), Gdx.graphics.getHeight());
		pleaseWait.setAlignment(Align.center);
		
		map = new GameMap();
		map.setWidth(clientPlayer.placemat.getWidth() * 1.5f);
		map.setHeight((map.getWidth() * 224) / 400); // 224 height
		map.setPosition(clientPlayer.placemat.getX() - (map.getWidth() / 6),
				((clientPlayer.hand.getY() + clientPlayer.placemat.getY()) / 2) - (map.getHeight() / 3));

		for (String s : players.keySet()) {
			System.out.println("Added player " + s + " playing as " + GameMap.idToString(players.get(s).countryId));
			map.addPlayer(players.get(s));
		}

		addActor(fps);
		addActor(map);
		for (int i = 1; i < map.countries.length; i++) {
			addActor(map.countries[i]);
		}
		addActor(clientPlayer);
		addActor(nextTurn);
		addActor(pleaseWait);
		addActor(returnToMenu);
		
		pleaseWait.setVisible(false);
		
//		setDebugAll(true);
	}

	@Override
	public void clearFields() {

	}
	
	boolean b = false;
	float y = -200;

	float wd_elapsed = 0;

	
	@Override
	public void draw() {
		
		if ((b != warInitiated) && warInitiated) {
			int vol_i = (int) Backendless.UserService.CurrentUser().getProperty("nw_volume");
			float vol = (float)(vol_i) / 100f;
			Sounds.music_queue.stop();
			Sounds.music_war.stop();
			Sounds.music_war.setLooping(Sounds.music_war.play(vol), true);
			b = warInitiated;
		}
		
		if (clientPlayer.populationInteger > 0) {
			int num_lost = 0;
			for (String s : players.keySet()) {
				Player p = players.get(s);
				if (p.populationInteger <= 0) {
					num_lost++;
				}
				
				if (num_lost == players.keySet().size() - 1) { // client won
					hasWon = true;
				}
			}
		}
		
		if (hasWon) {
			enableDark = true;
		}
		
		if (clientPlayer.populationInteger <= 0) {
			NuclearWarServer.currentTurn++;
		}
		
		if (NuclearWarServer.currentTurn >= turns.length) {
			NuclearWarServer.currentTurn = 0;
		}
		
		if (!turns[NuclearWarServer.currentTurn].equals(clientPlayer.username)) { // it's not this user's turn
			enableDark = true;
		} else {
			if (executingCard == null) {
				if (!hasWon) {
					enableDark = false;
					pleaseWait.setVisible(false);
				}
			}
		}
		
		if (clientPlayer.populationInteger <= 0) {
			enableDark = true;
			pleaseWait.setText("You lost!\n\nIf you\'re the host, please wait for all other players\n\nto finish before exiting");
			pleaseWait.setVisible(true);
		}
		
		if (NuclearWarServer.DECK.size() <= 0) {
			Card.loadCards(); // Reshuffles deck
		}

		if (Launcher.currentStage == Launcher.game_stage) {
			if (Gdx.input.getInputProcessor() != this) {
				Gdx.input.setInputProcessor(this);
			}
		}
		
		for (String s : players.keySet()) {
			if (players.get(s).shouldDrawCard) {
				players.get(s).drawCard();
				players.get(s).shouldDrawCard = false;
			}
		}
		
		Gdx.gl.glClearColor(0f, 0.25f, 0f, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		elapsed += Gdx.graphics.getDeltaTime() * 2.5f;
		getBatch().begin();
		if (warInitiated) {
			getBatch().setColor(1f, 0f, 0f, 0.9f);
		} else {
			getBatch().setColor(0f, 1f, 0f, 0.9f);
		}
		getBatch().draw(background.getKeyFrame(elapsed), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2,
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		getBatch().end();

		clientPlayer.act(Gdx.graphics.getDeltaTime());


		nextTurn.setPosition(
				clientPlayer.placemat.getLeftCard().getX() - (currentPlayer.placemat.getLeftCard().getWidth() / 2),
				clientPlayer.placemat.getLeftCard().getY() - clientPlayer.placemat.getLeftCard().getHeight() - 40);

		frameTime += Gdx.graphics.getDeltaTime();
		if (frameTime > 0.1) {
			fps.setText(Integer.toString((int) (1 / Gdx.graphics.getDeltaTime())) + " FPS");
			frameTime = 0f;
		}
		
		clientPlayer.dispUsername.setPosition(clientPlayer.dispUsername.getX(), fps.getY() - 150);

		checkOnlyOneCountrySelected();

		if (enableDark) {
			if (hasWon) pleaseWait.setText("Congratulations! You won!");
			
			if (NuclearWarServer.currentTurn >= turns.length) {
				NuclearWarServer.client.sendTCP(new StringPacket("&turn:0"));
				NuclearWarServer.currentTurn = 0;
			}
			
			if (!turns[NuclearWarServer.currentTurn].equals(clientPlayer.username) || hasWon) {
				pleaseWait.setVisible(true);
			}
			
			pleaseWait.toFront();
			returnToMenu.toFront();
			
			if (clientPlayer.populationInteger <= 0 || hasWon) {
				map.setVisible(false);
				
				returnToMenu.setPosition(Gdx.graphics.getWidth() - (returnToMenu.getWidth() / 2), Gdx.graphics.getHeight() - 100);
				
				for (int i = 1; i < map.countries.length; i++) {
					map.countries[i].setVisible(false);
				}
			}
			
			if (clientPlayer.isVisible()) clientPlayer.setVisible(false);
			if (nextTurn.isVisible()) nextTurn.setVisible(false);
		} else {
			if (!clientPlayer.isVisible()) clientPlayer.setVisible(true);
			if (!nextTurn.isVisible()) nextTurn.setVisible(true);
		}
		

		if (clickedCountry > 0 && (!executingCard.equals(Card.BLANK))) {
			executingCard.play(this, clickedCountry);
			clickedCountry = 0;
			executingCard = null;
		}

		for (int i = 1; i < map.countries.length; i++) {
			if (map.countries[i].isClicked && map.countries[i].getAssignedPlayer() != null) {
				System.out.println(map.countries[i].getAssignedPlayer().username + " has "
						+ map.countries[i].getAssignedPlayer().populationInteger + "M population");
				map.countries[i].isClicked = false;
				break;
			}
		}
		
		super.draw();
		
		if (enableDark) {
			getBatch().begin();
			getBatch().setColor(1, 1, 1, clientPlayer.populationInteger <= 0 ? 0.9f : 0.5f);
			getBatch().draw(dark, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight());
			if (pleaseWait.isVisible()) pleaseWait.draw(getBatch(), 1);
			returnToMenu.draw(getBatch(), 1);
			getBatch().setColor(1.0f, 1.0f, 1.0f, 1.0f);
			getBatch().end();
		}
	}

	public void checkOnlyOneCountrySelected() {
		boolean[] selected = new boolean[map.countries.length];
		int trueSize = 0;
		for (int i = 1; i < map.countries.length; i++) {
			selected[i] = map.countries[i].selected;
			if (selected[i])
				trueSize++;
		}

		if (trueSize > 1) {
			for (int i = 1; i < map.countries.length; i++) {
				map.countries[i].selected = false;
			}
		}
		
		selected = new boolean[0];
	}

	public void executeTurn(TurnPacket request) {
		request.execute(this);
	}
	
	public void selectTarget(Card card) {
		final MainGameStage mgs = this;
		this.executingCard = card;
		new Thread() {
			@Override
			public void run() {
				setName("selectTarget");
				System.out.println("Selecting target");
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