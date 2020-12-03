package com.cognitivethought.bpa.multiplayer;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.tidiness.Colors;
import com.cognitivethought.bpa.tidiness.Strings;
import com.cognitivethought.bpa.uistages.UIStage;

public class MultiplayerQueueStage extends UIStage {
	
	Label mq_errors, title;
	TextButton back;
	VerticalGroup mq_elements;
	public SelectBox<String> select_country;
	public ArrayList<String> player_names = new ArrayList<String>();
	public VerticalGroup players;
	
	
	public float delay = 0;
	
	public MultiplayerQueueStage(Viewport vp) {
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
		
		Skin s = new Skin(Gdx.files.absolute(Strings.URL_SKINS_DEFAULT_FILE), new TextureAtlas(Gdx.files.absolute(Strings.URL_SKINS_DEFAULT_ATLAS)));
		select_country = new SelectBox<String>(s);
		
		select_country.setAlignment(Align.center);
		select_country.setSize(400, 50);
		select_country.setItems("Asguard", "Bagmad", "Bananaland", "Bermudania", 
				"Bitland", "Great Bigland", "Han", "Hinja", "Hurria", "Nippyo", 
				"Popula", "Radonia", "Visalia");
		
		mq_elements = new VerticalGroup();
		mq_elements.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mq_elements.space(50);
		
		players = new VerticalGroup();
		players.space(50);
		
//		gm_elements.scaleBy(scale);

		param.size = (int)labelStyle.font.getLineHeight() * (1 + scale);
		LabelStyle style = new LabelStyle();
		style.font = gen.generateFont(param);
		style.fontColor = Color.RED;
		title = new Label(Strings.MUI_TITLE, style);
//		title.scaleBy(scale);
		title.setAlignment(Align.center);
		title.setPosition(mq_elements.getX() - (title.getWidth() / 2), (int)(getViewport().getScreenHeight() * 1.3));
		
		players.setPosition(mq_elements.getX() - (players.getWidth() / 2), mq_elements.getY());
		
		mq_errors = new Label(Strings.EMPTY, labelStyle);
		mq_errors.getStyle().fontColor = Colors.TEXT_INFO;
		
		back = new TextButton(Strings.JSS_BACK, buttonStyle);
		
		back.align(Align.center);
		
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (NuclearWarServer.client != null)
					NuclearWarServer.disconnectClient();
				if (NuclearWarServer.server != null)
					NuclearWarServer.closeServer();
				Launcher.setStage(Launcher.game_menu_stage);
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
		
		mq_elements.addActor(select_country);
		mq_elements.addActor(back);
		mq_elements.addActor(mq_errors);
		mq_elements.addActor(players);
		
		mq_elements.align(Align.center);
		
		addActor(title);
		addActor(mq_elements);
		addActor(select_country);
		
		refreshList();
	}
	
	@Override
	public void draw() {
		
		delay += delay < 30 ? 1 : 0;
		if (player_names.size() == 0 && delay >= 20) {
			Launcher.setStage(Launcher.game_menu_stage);
		}
		
		super.draw();
	}
	
	public void refreshList() {
		players.remove();
		players.clear();
		for (int i = 0; i < player_names.size(); i++) {
			players.addActor(new Label(player_names.get(i), labelStyle));
		}
		addActor(players);
	}
	
	@Override
	public void clearFields() {
		
	}
	
}