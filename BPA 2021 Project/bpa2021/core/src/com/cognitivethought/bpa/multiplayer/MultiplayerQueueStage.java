package com.cognitivethought.bpa.multiplayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.backendless.Backendless;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.prefabs.Card;
import com.cognitivethought.bpa.prefabs.GameMap;
import com.cognitivethought.bpa.sound.Sounds;
import com.cognitivethought.bpa.tidiness.Colors;
import com.cognitivethought.bpa.tidiness.Strings;
import com.cognitivethought.bpa.uistages.UIStage;

public class MultiplayerQueueStage extends UIStage {
	
	Label mq_errors, code;
	TextButton back, start_game;
	VerticalGroup mq_elements;
	CheckBox mq_ready;
	public SelectBox<String> select_country;
	public ArrayList<String> player_names = new ArrayList<String>();
	public VerticalGroup players;
	public boolean start = false;
	public boolean shouldLoad;
	
	String[] debugUsers = new String[] {"XD_gamer2023", "meme.mlgpro91", "totallY_areal.player4013", "402funnynumbers6029"};
	
	public float delay = 0;
	
	public MultiplayerQueueStage(Viewport vp) {
		super(vp);
	}
	
	@Override
	public void populate() {
		super.populate();
		
		int vol_i = (int) Backendless.UserService.CurrentUser().getProperty("nw_volume");
		float vol = (float)(vol_i) / 100f;
		Sounds.music_queue.stop();
		Sounds.music_intro.stop();
		Sounds.music_war.stop();
		Sounds.music_queue.setLooping(Sounds.music_queue.play(vol), true);
		
		int scale = ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768));
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 15 * (1 + scale);

		//	buttonStyle.font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		buttonStyle.font = gen.generateFont(param);
		
		Skin s = new Skin(Gdx.files.absolute(Strings.URL_SKINS_DEFAULT_FILE), new TextureAtlas(Gdx.files.absolute(Strings.URL_SKINS_DEFAULT_ATLAS)));
		select_country = new SelectBox<String>(s);
		
		select_country.setAlignment(Align.center);
		select_country.setSize(400, 50);
		select_country.setItems("None", "Asguard", "Bagmad", "Bananaland", "Bermudania", 
				"Bitland", "Great Bigland", "Han", "Hinja", "Hurria", "Nippyo", 
				"Popula", "Radonia", "Visalia");
		select_country.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				((MainGameStage)Launcher.game_stage).players.get(Launcher.currentUser.getProperty("name")).countryId = select_country.getSelectedIndex();
				StringPacket changeCountry = new StringPacket("%change%;" + Launcher.currentUser.getProperty("name") + ";" + ((MainGameStage)Launcher.game_stage).players.get((String)Launcher.currentUser.getProperty("name")).countryId);
				System.out.println("PACKET " + "%change%;" + Launcher.currentUser.getProperty("name") + ";" + ((MainGameStage)Launcher.game_stage).players.get((String)Launcher.currentUser.getProperty("name")).countryId);
				NuclearWarServer.client.sendTCP(changeCountry);
			}
		});
//		change = select_country.getActions()
		System.out.println("GET ACTIONS SIZE: " + select_country.getActions().size);
		
		mq_ready = new CheckBox("Check this box when you\'re ready to play",
				new Skin(new FileHandle(Strings.URL_SKINS_DEFAULT_FILE),
						new TextureAtlas(new FileHandle(Strings.URL_SKINS_DEFAULT_ATLAS))));
		mq_ready.setSize(32, 32);
		mq_ready.setClip(false);

		mq_ready.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (mq_ready.isChecked()) {
					if (Launcher.currentUser != null) {
						StringPacket ready = new StringPacket("?ready:true:" + Launcher.currentUser.getProperty("name"));
						NuclearWarServer.client.sendTCP(ready);
//						mq_ready.setDisabled(true);
					}
				} else {
					if (Launcher.currentUser != null) {
						StringPacket ready = new StringPacket("?ready:false:" + Launcher.currentUser.getProperty("name"));
						NuclearWarServer.client.sendTCP(ready);
					}
				}
				
				refreshList();
			}
		});
		
		mq_elements = new VerticalGroup();
		mq_elements.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		param.size = (int)labelStyle.font.getLineHeight() * (1 + scale);
		LabelStyle style = new LabelStyle();
		style.font = gen.generateFont(param);
		style.fontColor = Color.RED;
		
		players = new VerticalGroup();
		players.align(Align.center);
		players.space(10);
		
//		gm_elements.scaleBy(scale);
		
		
		mq_errors = new Label(Strings.EMPTY, labelStyle);
		mq_errors.getStyle().fontColor = Colors.TEXT_INFO;
		
		code = new Label("JOIN CODE: " + NuclearWarServer.hostIpv4 + ":" + Integer.toString(NuclearWarServer.code), labelStyle);
		
		back = new TextButton(Strings.JSS_BACK, buttonStyle);
		
		back.align(Align.center);
		
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				NuclearWarServer.closeServer();
				if (NuclearWarServer.client != null)
					NuclearWarServer.disconnectClient();
				Launcher.setStage(Launcher.game_menu_stage);
			}
		});
		
		start_game = new TextButton(Strings.HSS_START, buttonStyle) {
			@Override
			public void setDisabled(boolean isDisabled) {
				if (isDisabled) {
					if (mq_elements.getChildren().contains(this, false)) {
						mq_elements.removeActor(this);
					}
				} else {
					if (!mq_elements.getChildren().contains(this, false)) {
						mq_elements.addActorBefore(mq_elements.getChild(0), this);
					}
				}
				super.setDisabled(isDisabled);
			}
		};
		start_game.align(Align.center);
		start_game.addListener(new ClickListener() {
			@SuppressWarnings("rawtypes")
			@Override
			public void clicked(InputEvent event, float x, float y) {
				boolean duplicates = false;
				boolean none = false;
				ArrayList<Integer> ids = new ArrayList<>();
				Iterator it = ((MainGameStage)Launcher.game_stage).players.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pair = (Map.Entry)it.next();
			        ids.add(((MainGameStage)Launcher.game_stage).players.get(pair.getKey()).countryId);
			    }
			    
			    for (int i = 0; i < ids.size() && !duplicates && !none; i++) {
			    	if (i > 0) {
			    		if (ids.get(i - 1) == ids.get(i)) {
			    			duplicates = true;
			    		}			    		
			    	}
			    	
			    	if (ids.get(i) == GameMap.ID_NONE) {
			    		none = true;
			    	}
			    }
			    
			    if (!duplicates && !none) {
				    StringPacket startPacket = new StringPacket("@start@" + Integer.toString(NuclearWarServer.code));
					NuclearWarServer.server.sendToAllTCP(startPacket);
	//				Launcher.setStage(Launcher.game_stage);
			    } else {
			    	mq_errors.setText("Error! Please make sure each player has selected a unique country!");
			    }
			}
		});
		
		final MultiplayerQueueStage jss_stage = this;
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(mq_elements, x, y, back)) {
					jss_stage.unfocusAll();
				}
				super.clicked(event, x, y);
			}
		});
		
//		mq_elements.addActor(mq_errors);
		mq_elements.addActor(select_country);
		mq_elements.addActor(players);
		mq_elements.addActor(code);
		mq_elements.addActor(back);
		mq_elements.addActor(mq_ready);
		
		mq_elements.space(30);
		mq_elements.align(Align.center);
		
		addActor(mq_errors);
		addActor(mq_elements);
		
		refreshList();
	}
	
	@Override
	public void draw() {
		
		delay += delay < 30 ? 1 : 0;
		if (player_names.size() == 0 && delay >= 20) {
			Launcher.setStage(Launcher.game_menu_stage);
		}
		
		if (NuclearWarServer.server != null) {
			int x = 0;
			for (int i = 0; i < player_names.size(); i++) {
				if ((((MainGameStage)Launcher.game_stage).players.get(player_names.get(i)).ready)) {
					x++;
				}
			}
			
			if (start_game != null) {
				// If the majority of players are ready
				if (x >= ((float)(((MainGameStage)Launcher.game_stage).players.size())) / 2) {
					if (start_game.isDisabled()) {
						start_game.setDisabled(false);
					}
				} else {
					if (!start_game.isDisabled()) {
						start_game.setDisabled(true);
					}
				}
			}
		}
		
		try {
			if (getBatch().isDrawing()) getBatch().end();
			super.draw();
		} catch (Exception e) {
			e.printStackTrace();
        	Launcher.log();
			refreshList();
		}
		
		if (start) {
			Launcher.setStage(Launcher.game_stage);
		}
		
		if (shouldLoad) {
			Card.loadCards();
			shouldLoad = false;
		}
	}
	
	public void refreshList() {
		System.out.println("REFRESHING LIST");
//		players.remove();
		if (players == null) players = new VerticalGroup();
		players.clearChildren();
		for (int i = 0; i < player_names.size(); i++) {
			if (i > player_names.size()) break;
			System.out.println("PLAYER NAMES GET(i) " + i + " = null? " + (player_names.get(i) == null));
			System.out.println("PLAYER " + player_names.get(i) + " = null? " + ((((MainGameStage)Launcher.game_stage).players.get(player_names.get(i)) == null)));
			players.addActor(new Label(player_names.get(i) + (("(" + GameMap.idToString(((MainGameStage)Launcher.game_stage).players.get(player_names.get(i)).countryId) + ")") + (((MainGameStage)Launcher.game_stage).players.get(player_names.get(i)).ready ? "|Y" : "|N")), labelStyle));
		}
		players.align(Align.center);
//		players.setPosition(mq_elements.getX() - (players.getWidth() / 2), mq_elements.getY() - (mq_elements.getHeight() / 2));
//		mq_elements.addActor(players);
	}

	@Override
	public void clearFields() {
		start = false;
	}

}