package com.cognitivethought.bpa.gamestages;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.Card;

public class DevStage extends GameStage {
	
	HorizontalGroup hand;
	ArrayList<WidgetGroup> cards;
	
	public DevStage(Viewport vp) {
		super(vp);
	}
	
	@Override
	public void populate() {
		super.populate();
		
		cards = new ArrayList<>();
		
		hand = new HorizontalGroup();
		
		for (int i = 0; i < 8; i++) {
			Card card = new Card(Card.Type.WARHEAD, "Test Warhead", "This is a test description", -100, 0, 10, 1);
			card.setSize(90, 140);
			card.setScale(((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));
			
			WidgetGroup w = new WidgetGroup();
			w.addActor(card);
			
			cards.add(w);
			
			// TODO: Implement card focusing on hover
		}
		
		hand.align(Align.center);
		hand.space(90);
		hand.setSize(Gdx.graphics.getWidth() - 200, 400);
		hand.setPosition(100 + Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		hand.debugAll();
		
		for (WidgetGroup wg : cards) {
			hand.addActor(wg);
		}
		
		addActor(hand);
	}
	
	@Override
	public void clearFields() {
		
	}
}