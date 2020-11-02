package com.cognitivethought.bpa.gamestages;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.Card;
import com.cognitivethought.bpa.Placemat;
import com.cognitivethought.bpa.PopulationCard;
import com.cognitivethought.bpa.prefabs.Spinner;

public class MainGameStage extends GameStage {

	HorizontalGroup hand;
	VerticalGroup population;

	ArrayList<WidgetGroup> cards;
	ArrayList<WidgetGroup> populationCards;

	int[] pop = { 10, 10, 5 };

	Label totalPop;
	Label fps;

	public Card currentlyHeldCard;

	public Placemat placemat;
	
	public MainGameStage(Viewport vp) {
		super(vp);
	}

	@Override
	public void populate() {
		super.populate();

		fps = new Label("", labelStyle);
		fps.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getWidth() / 2);

		totalPop = new Label("", labelStyle);
//		totalPop.setColor(Color.BLUE);

		cards = new ArrayList<>();
		populationCards = new ArrayList<>();

		hand = new HorizontalGroup();
		population = new VerticalGroup();
		placemat = new Placemat();
		
		for (int i = 0; i < 8; i++) {
			Card card = new Card(Card.DECK.get(i));
			card.setSize(90, 140);
			card.setScale(((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));
			card.spacing = ((int) (card.getWidth() / 2) - 2);

			WidgetGroup w = new WidgetGroup();
			w.addActor(card);

			cards.add(w);

			Card.DECK.remove(i);
		}

		for (int i = 0; i < pop.length; i++) {
			PopulationCard p = new PopulationCard(pop[i]);
			p.setSize(60, 75);
			p.setScale(((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));

			WidgetGroup w = new WidgetGroup();
			w.addActor(p);

			populationCards.add(w);
		}

		hand.align(Align.center);
		hand.space(((90 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
//		hand.setSize(Gdx.graphics.getWidth() - 200, 400);
		hand.setPosition(Gdx.graphics.getWidth() - cards.get(0).getChild(0).getWidth() / 2,
				Gdx.graphics.getHeight() / 2);

		population.align(Align.center);
		population.space(((60 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
		population.setPosition((Gdx.graphics.getWidth() * 1.5f) - (populationCards.get(0).getWidth() + 70),
				Gdx.graphics.getHeight());

		for (WidgetGroup wg : cards) {
			hand.addActor(wg);
		}

		for (WidgetGroup wg : populationCards) {
			population.addActor(wg);
		}

		totalPop.setPosition(population.getX(), population.getY() - 100);
		totalPop.setAlignment(Align.left);
		
		placemat.setSize(400 * 2, 224 * 2);
		placemat.setPosition(hand.getX() - (placemat.getWidth() / 2) + 100, hand.getY() + (placemat.getHeight() / 2) + 75);
		
//		hand.setDebug(true, true);

//		addActor(fps);
		addActor(hand);
		addActor(population);
		addActor(totalPop);
		addActor(placemat);

		final Spinner spinner = new Spinner();
		spinner.setSize(250, 250);
		spinner.setPosition(hand.getX() - (hand.getWidth() / 2) - 575, hand.getY() + 400);

		addActor(spinner);

		addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.F4) {
					spinner.spin();
				}

				return super.keyDown(event, keycode);
			}
		});
	}

	public void holdCard(Card card) {
		currentlyHeldCard = card;
		hand.removeActor(card);
	}
	
	@Override
	public void clearFields() {

	}

	float frameTime = 0f;

	int sum(int[] arr) {
		int ret = 0;
		for (int i : arr)
			ret += i;
		return ret;
	}

	@Override
	public void draw() {
		Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		super.draw();

		totalPop.setText(Integer.toString(sum(pop)) + "M");

		if (currentlyHeldCard != null) {
			currentlyHeldCard.setPosition(
					Gdx.input.getX() + (Gdx.graphics.getWidth() / 2) - (currentlyHeldCard.getWidth() / 2),
					Gdx.graphics.getHeight() - Gdx.input.getY() + currentlyHeldCard.getHeight());
			if (!getActors().contains((Actor) currentlyHeldCard, true))
				addActor(currentlyHeldCard);
			
			if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
				currentlyHeldCard.play();
				currentlyHeldCard.remove();
				currentlyHeldCard = null;
			}
		}

		frameTime += Gdx.graphics.getDeltaTime();
		if (frameTime > 0.1) {
			fps.setText(Integer.toString((int) (1 / Gdx.graphics.getDeltaTime())));
			frameTime = 0f;
		}
	}
}