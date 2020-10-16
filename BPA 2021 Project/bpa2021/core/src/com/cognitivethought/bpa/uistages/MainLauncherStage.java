package com.cognitivethought.bpa.uistages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.Colors;
import com.cognitivethought.bpa.Strings;
import com.cognitivethought.bpa.launcher.Launcher;

public class MainLauncherStage extends LauncherStage {

	VerticalGroup main_elements;
	TextButton main_launch;
	Label main_errors, main_title;

	public MainLauncherStage(Viewport vp) {
		super(vp);
	}
	
	@Override
	public void populate() {
		super.populate();
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_UBUNTU_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 50;
		BitmapFont titleFont = gen.generateFont(param);

		LabelStyle style = new LabelStyle();
		style.font = titleFont;
		style.fontColor = Colors.LNUI_TITLE;

		main_elements = new VerticalGroup();
		main_elements.align(Align.center);
		main_elements.space(20f);
		main_elements.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		main_title = new Label(Strings.LNUI_TITLE, style);
		main_title.setAlignment(Align.center);
		main_title.setPosition(main_elements.getX() - (main_title.getWidth() / 2), main_elements.getY() + 150);
		
		main_launch = new TextButton(Strings.LNUI_LAUNCH, buttonStyle);
		main_launch.setSize(100, buttonStyle.font.getLineHeight() + 10);
		main_launch.align(Align.center);
		
		main_launch.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Launcher.setStage(Launcher.game_menu_stage);
				
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
				Launcher.game_menu_stage.populate();
			}
		});
		
		main_elements.addActor(main_launch);
		
		addActor(main_title);
		addActor(main_elements);
	}

	@Override
	public void clearFields() {
		System.out.println("No fields to clear");
	}

}