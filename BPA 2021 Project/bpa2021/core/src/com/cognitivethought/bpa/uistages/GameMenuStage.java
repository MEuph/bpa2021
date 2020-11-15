package com.cognitivethought.bpa.uistages;

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
import com.cognitivethought.bpa.multiplayer.NuclearWarServer;
import com.cognitivethought.bpa.tidiness.Colors;
import com.cognitivethought.bpa.tidiness.Strings;

public class GameMenuStage extends UIStage {
	
	Label mui_errors, title;
	TextButton start, help, quit, host_server;
	TextField host_code;
	VerticalGroup gm_elements;
	
	public GameMenuStage(Viewport vp) {
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
		
		gm_elements = new VerticalGroup();
		gm_elements.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gm_elements.space(50);
		
//		gm_elements.scaleBy(scale);

		param.size = (int)labelStyle.font.getLineHeight() * (1 + scale);
		LabelStyle style = new LabelStyle();
		style.font = gen.generateFont(param);
		style.fontColor = Color.RED;
		title = new Label(Strings.MUI_TITLE, style);
//		title.scaleBy(scale);
		title.setAlignment(Align.center);
		title.setPosition(gm_elements.getX() - (title.getWidth() / 2), (int)(getViewport().getScreenHeight() * 1.3));
				
		mui_errors = new Label(Strings.EMPTY, labelStyle);
		mui_errors.getStyle().fontColor = Colors.TEXT_INFO;
		
		start = new TextButton(Strings.MUI_START, buttonStyle);
		help = new TextButton(Strings.MUI_HELP, buttonStyle);
		quit = new TextButton(Strings.MUI_QUIT, buttonStyle);
		host_server = new TextButton(Strings.MUI_HOST, buttonStyle);
		
		host_code = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));
		host_code.setMessageText(Strings.MUI_HOST_FIELD);
		host_code.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					host_code.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (host_code.getText().equals(Strings.MUI_HOST_FIELD)) {
						host_code.setText("");
					}
				}
			}
		});
		
		host_code.setSize(250, textStyle.font.getLineHeight() + 10);
		host_code.setAlignment(Align.center);
		
		start.align(Align.center);
		help.align(Align.center);
		quit.align(Align.center);
		host_server.align(Align.center);
		
		start.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.dev_stage);
			}
		});
		
		quit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		host_server.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (NuclearWarServer.server == null) {
					try {
						int code = Integer.parseInt(host_code.getText());
						NuclearWarServer.openServer(code);
						if (NuclearWarServer.server == null) {
							throw new Exception();
						}
					} catch (Exception e) {
						mui_errors.getStyle().fontColor = Colors.TEXT_ERROR;
						mui_errors.setText("Please enter a valid 4-digit host code between 3000 and 9999");
					}
					
					// TODO: ALLOW PLAYERS TO CONNECT TO SERVERS, THEN ALLOW PLAYERS TO SEND TURN DATA TO SERVER, THEN PRINT DEBUG DATA TO CONSOLE TO SHOW THAT TURNS ARE INDEED BEING SENT BACK AND FORTH AND PLAYERS ARE CHANGING
					
					if (NuclearWarServer.server != null) {
						mui_errors.getStyle().fontColor = Colors.TEXT_INFO;
						mui_errors.setText("Succesffuly hosting server with access code: " + NuclearWarServer.code);
					} else {
						mui_errors.getStyle().fontColor = Colors.TEXT_ERROR;
						mui_errors.setText("Please enter a valid 4-digit host code between 3000 and 9999");
					}
				} else {
					mui_errors.getStyle().fontColor = Colors.TEXT_ERROR;
					mui_errors.setText("Already hosting server with access code: " + NuclearWarServer.code);
				}
			}
		});
		
		final GameMenuStage mui_stage = this;
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(gm_elements, x, y, host_code, host_server)) {
					mui_stage.unfocusAll();
				}
				super.clicked(event, x, y);
			}
		});
		
		gm_elements.addActor(start);
		gm_elements.addActor(help);
		gm_elements.addActor(quit);
		gm_elements.addActor(host_server);
		gm_elements.addActor(host_code);
		gm_elements.addActor(mui_errors);
		
		gm_elements.align(Align.center);
		
		addActor(title);
		addActor(gm_elements);
	}
	
	@Override
	public void clearFields() {
		host_code.setText(Strings.EMPTY);
	}
}