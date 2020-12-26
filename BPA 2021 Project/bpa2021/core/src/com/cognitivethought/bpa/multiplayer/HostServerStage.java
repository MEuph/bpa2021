package com.cognitivethought.bpa.multiplayer;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.tidiness.Colors;
import com.cognitivethought.bpa.tidiness.Strings;
import com.cognitivethought.bpa.uistages.UIStage;

public class HostServerStage extends UIStage {
	
	Label hss_errors;
	TextButton host, start, back;
	TextField host_code;
	VerticalGroup hss_elements;
	public ArrayList<String> player_names = new ArrayList<String>();
	public VerticalGroup players;
	
	public HostServerStage(Viewport vp) {
		super(vp);
	}
	
	@Override
	public void populate() {
		super.populate();
		
		int scale = ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768));
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 15 * (1 + scale);

		//	buttonStyle.font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		buttonStyle.font = gen.generateFont(param);
		
		hss_elements = new VerticalGroup();
		hss_elements.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hss_elements.space(50);
		
		players = new VerticalGroup();
		players.space(50);
		
//		gm_elements.scaleBy(scale);
		param.size = 10;
		labelStyle.font = gen.generateFont(param);
		hss_errors = new Label(Strings.EMPTY, labelStyle);
		hss_errors.getStyle().fontColor = Colors.TEXT_INFO;

		param.size = (int)labelStyle.font.getLineHeight() * (1 + scale);
		LabelStyle style = new LabelStyle();
		style.font = gen.generateFont(param);
		style.fontColor = Color.RED;
		
		players.setPosition(hss_elements.getX() - (players.getWidth() / 2), hss_elements.getY());
		
		
		start = new TextButton(Strings.HSS_START, buttonStyle);
		host = new TextButton(Strings.HSS_HOST, buttonStyle);
		back = new TextButton(Strings.JSS_BACK, buttonStyle);
		
		host_code = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));
		host_code.setMessageText(Strings.MUI_HOST_FIELD);
		host_code.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					host_code.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (host_code.getText().equals(Strings.JSS_CODE_FIELD)) {
						host_code.setText("");
					}
				}
			}
		});
		
		host_code.setSize(250, textStyle.font.getLineHeight() + 10);
		host_code.setAlignment(Align.center);
		
		start.align(Align.center);
		host.align(Align.center);
		back.align(Align.center);
		
		host.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (NuclearWarServer.server == null) {
					try {
						int code = Integer.parseInt(host_code.getText());
						NuclearWarServer.openServer(code);
						if (NuclearWarServer.server == null) {
							throw new Exception();
						} else {
							Launcher.setStage(Launcher.mq_stage);
						}
					} catch (Exception e) {
						hss_errors.getStyle().fontColor = Colors.TEXT_ERROR;
						hss_errors.setText("Please enter a valid 4-digit host code between 3000 and 9999");
					}
					
					// TODO: THEN ALLOW PLAYERS TO SEND TURN DATA TO SERVER, THEN PRINT DEBUG DATA TO CONSOLE TO SHOW THAT TURNS ARE INDEED BEING SENT BACK AND FORTH AND PLAYERS ARE CHANGING
					
					if (NuclearWarServer.server != null) {
						hss_errors.getStyle().fontColor = Colors.TEXT_INFO;
						hss_errors.setText("Succesfully hosting server with code: " + NuclearWarServer.code);
					} else {
						hss_errors.getStyle().fontColor = Colors.TEXT_ERROR;
						hss_errors.setText("Please enter any 4-digit code from 3001-9999");
					}
				} else {
					hss_errors.getStyle().fontColor = Colors.TEXT_ERROR;
					hss_errors.setText("Already hosting server with code: " + NuclearWarServer.code);
				}
			}
		});
		
		start.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				StringPacket startGamePacket = new StringPacket();
				startGamePacket.data = "@start@" + NuclearWarServer.code;
				NuclearWarServer.server.sendToAllTCP(startGamePacket);
				Launcher.setStage(Launcher.game_stage);
			}
		});
		
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.game_menu_stage);
			}
		});
		
		final HostServerStage jss_stage = this;
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(hss_elements, x, y, host_code, host, back)) {
					jss_stage.unfocusAll();
				}
				super.clicked(event, x, y);
			}
		});
		
		hss_elements.addActor(host);
		hss_elements.addActor(back);
		hss_elements.addActor(host_code);
		hss_elements.addActor(hss_errors);
		hss_elements.addActor(players);
		
		hss_elements.align(Align.center);
		
		addActor(hss_elements);
		
		refreshList();
	}
	
	public void refreshList() {
		if (player_names.size() > 1) {
			start.remove();
			addActor(start);
		}
		
		players.remove();
		players.clear();
		for (int i = 0; i < player_names.size(); i++) {
			players.addActor(new Label(player_names.get(i), labelStyle));
		}
		addActor(players);
	}
	
	@Override
	public void clearFields() {
		host_code.setText(Strings.EMPTY);
	}
	
}