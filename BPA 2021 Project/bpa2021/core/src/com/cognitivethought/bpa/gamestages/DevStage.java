package com.cognitivethought.bpa.gamestages;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.Card;

public class DevStage extends GameStage {
	
	HorizontalGroup hand;
	ArrayList<WidgetGroup> cards;
	
	Label fps;
	
	public DevStage(Viewport vp) {
		super(vp);
	}
	
	@Override
	public void populate() {
		super.populate();
		
		fps = new Label("", labelStyle);
		fps.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getWidth() / 2);
		
		cards = new ArrayList<>();
		
		hand = new HorizontalGroup();
		
		for (int i = 0; i < 8; i++) {
			Card card = new Card(Card.DECK.get(i));
			card.setSize(90, 140);
			card.setScale(((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));
			card.spacing = ((int)(card.getWidth() / 2) - 2);
			
			WidgetGroup w = new WidgetGroup();
			w.addActor(card);
			
			cards.add(w);
			
			Card.DECK.remove(i);
		}
		
		hand.align(Align.center);
		hand.space(((90 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
//		hand.setSize(Gdx.graphics.getWidth() - 200, 400);
		hand.setPosition(Gdx.graphics.getWidth() - cards.get(0).getChild(0).getWidth() / 2, Gdx.graphics.getHeight() / 2);
		hand.debugAll();
		
		for (WidgetGroup wg : cards) {
			hand.addActor(wg);
		}
		
//		hand.setDebug(true, true);
		
//		addActor(fps);
		addActor(hand);
	}
	
	@Override
	public void clearFields() {
		
	}
	
	float frameTime = 0f;
	
	@Override
	public void draw() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.draw();
		
		frameTime += Gdx.graphics.getDeltaTime();
		if (frameTime > 0.1) {
			fps.setText(Integer.toString((int)(1 / Gdx.graphics.getDeltaTime())));
			frameTime = 0f;
		}
	}
}