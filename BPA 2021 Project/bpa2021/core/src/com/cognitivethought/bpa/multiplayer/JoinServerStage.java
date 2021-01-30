package com.cognitivethought.bpa.multiplayer;

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

public class JoinServerStage extends UIStage {
	
	Label jss_errors;
	TextButton join, back;
	TextField server_code;
	VerticalGroup jss_elements;
	
	public JoinServerStage(Viewport vp) {
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
		
		jss_elements = new VerticalGroup();
		jss_elements.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		jss_elements.space(50);
		
//		gm_elements.scaleBy(scale);

		param.size = (int)labelStyle.font.getLineHeight() * (1 + scale);
		LabelStyle style = new LabelStyle();
		style.font = gen.generateFont(param);
		style.fontColor = Color.RED;
				
		jss_errors = new Label(Strings.EMPTY, labelStyle);
		jss_errors.getStyle().fontColor = Colors.TEXT_INFO;
		
		join = new TextButton(Strings.JSS_JOIN, buttonStyle);
		back = new TextButton(Strings.JSS_BACK, buttonStyle);
		
		server_code = new TextField(Strings.EMPTY, new TextFieldStyle(textStyle));
		server_code.setMessageText(Strings.MUI_HOST_FIELD);
		server_code.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					server_code.getStyle().fontColor = Colors.TEXT_DEFAULT;
					if (server_code.getText().equals(Strings.JSS_CODE_FIELD)) {
						server_code.setText("");
					}
				}
			}
		});
		
		server_code.setSize(250, textStyle.font.getLineHeight() + 10);
		server_code.setAlignment(Align.center);
		
		join.align(Align.center);
		back.align(Align.center);
		
		join.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				boolean success = false;
				
				try {
					NuclearWarServer.hostIpv4 = server_code.getText().split(":")[0];
					NuclearWarServer.joinServer(Integer.parseInt(server_code.getText().split(":")[1]));
					jss_errors.setText("Trying to connect...");
					success = true;
				} catch (Exception e) {
					jss_errors.setText("Error, couldn\'t connect to server!");
		        	Launcher.log();
				}
				
				if (success) {
					NuclearWarServer.code = Integer.parseInt(server_code.getText().split(":")[1]);
					Launcher.setStage(Launcher.mq_stage);
				}
			}
		});
		
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.previousStage);
			}
		});
		
		final JoinServerStage jss_stage = this;
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!isClickingOnUIElement(jss_elements, x, y, server_code, join, back)) {
					jss_stage.unfocusAll();
				}
				super.clicked(event, x, y);
			}
		});
		
		jss_elements.addActor(join);
		jss_elements.addActor(back);
		jss_elements.addActor(server_code);
		jss_elements.addActor(jss_errors);
		
		jss_elements.align(Align.center);
		
		addActor(jss_elements);
	}
	
	@Override
	public void clearFields() {
		server_code.setText(Strings.EMPTY);
	}
	
}