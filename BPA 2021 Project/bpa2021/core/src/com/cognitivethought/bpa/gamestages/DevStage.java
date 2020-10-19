package com.cognitivethought.bpa.gamestages;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.Card;

public class DevStage extends GameStage {
	
	ArrayList<Card> cards;
	
	HorizontalGroup hand;
	
	public DevStage(Viewport vp) {
		super(vp);
	}

	@Override
	public void clearFields() {
		
	}
	
}