package com.cognitivethought.bpa.gamestages;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cognitivethought.bpa.Card;
import com.cognitivethought.bpa.Cursor;
import com.cognitivethought.bpa.Placemat;
import com.cognitivethought.bpa.PopulationCard;
import com.cognitivethought.bpa.Strings;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.prefabs.Spinner;

public class MainGameStage extends GameStage {
	
	public static final TooltipManager MANAGER = new TooltipManager();
	
	HorizontalGroup hand;
	VerticalGroup population;
	
	public Cursor c;
	
	ArrayList<WidgetGroup> cards;
	ArrayList<WidgetGroup> populationCards;

	int[] possible_combos = {100, 50, 20, 10, 1};
	int[] pop = split(999);
	
	float cardWidth = 0;
	float popCardWidth = 0;
	
	Label totalPop;
	Label fps;

	public Card initialState;
	public Card currentlyHeldCard;

	public Placemat placemat;
	public final Spinner spinner = new Spinner();
	
	public ImageButton nextTurn;
	
	public MainGameStage(Viewport vp) {
		super(vp);
		MANAGER.instant();
		
		nextTurn = new ImageButton(new Image(new Texture(Strings.URL_NEXT_TURN)).getDrawable());
		nextTurn.setPosition(Gdx.graphics.getWidth() + 200, (Gdx.graphics.getHeight() * 2) - 200);
		
		nextTurn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				
				placemat.advance();
			}
		});
		
		addActor(nextTurn);
	}
	
	public int[] split(int num) {
		ArrayList<Integer> a = new ArrayList<>();
		
		for (int i = 0; i < possible_combos.length && num > 0; i++) {
			while (num - possible_combos[i] >= 0) {
				a.add(possible_combos[i]);
				num -= possible_combos[i];
			}
		}
		
		int[] ret = new int[a.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = a.get(i);
		}
		
		return ret;
		
	}
	
	public int occurences(int[] arr, int search) {
		int ret = 0;
		
		for (int i : arr)
			if (i == search)
				ret++;
		
		return ret;
	}
	
	@Override
	public void populate() {
		super.populate();
		
		c = new Cursor();
		
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
			cardWidth = card.getWidth();
			
			WidgetGroup w = new WidgetGroup();
			w.addActor(card);

			cards.add(w);

			Card.DECK.remove(i);
		}
		
		for (int i = 0; i < possible_combos.length; i++) {
			if (occurences(pop, possible_combos[i]) > 0) {
				PopulationCard p = new PopulationCard(possible_combos[i], occurences(pop, possible_combos[i]));
				
				p.setSize(64, 64);
				p.setScale(((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));
				popCardWidth = p.getWidth();
				
				WidgetGroup w = new WidgetGroup();
				w.addActor(p);

				populationCards.add(w);
			}
		}

		hand.align(Align.center);
		hand.space(((90 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
//		hand.setSize(Gdx.graphics.getWidth() - 200, 400);
		hand.setPosition(Gdx.graphics.getWidth() - cardWidth / 2,
				Gdx.graphics.getHeight() / 2);

		population.align(Align.center);
		population.space(((60 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
		population.setPosition((Gdx.graphics.getWidth() * 1.5f) - (popCardWidth + 70),
				Gdx.graphics.getHeight());

		for (WidgetGroup wg : cards) {
			hand.addActor(wg);
		}

		for (WidgetGroup wg : populationCards) {
			population.addActor(wg);
		}

		totalPop.setPosition(population.getX(), population.getY() - ((((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)) * 32) * population.getChildren().size));
		totalPop.setAlignment(Align.left);
		
		float scale = 2f;
		placemat.setSize((400 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)), (224 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));
		placemat.setPosition(hand.getX() - (placemat.getWidth() / 2) + 100, (Gdx.graphics.getHeight() * 1.5f) - placemat.getHeight());
		placemat.setSize((400 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)), (224 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));
		
//		hand.setDebug(true, true);

		spinner.setSize(400 * scale, 224 * scale);
		spinner.setPosition(hand.getX() - (spinner.getWidth() / 2) + 100, (Gdx.graphics.getHeight() * 1.5f) - spinner.getHeight());
		

		addActor(fps);
		
		addActor(spinner.clickArrow);
		addActor(placemat.clickArrow);
		addActor(placemat);
		addActor(hand);
		addActor(population);
		addActor(totalPop);
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
		
		addActor(c);
	}

	public void holdCard(Card card) {
		currentlyHeldCard = card;
		initialState = card;
		WidgetGroup wg = (WidgetGroup)card.getParent();
		
		hand.removeActor(wg);
		hand.space(((90 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
	}
	
	public void putBackInHand() {
		currentlyHeldCard = initialState;
		
		WidgetGroup w = new WidgetGroup();
		w.addActor(currentlyHeldCard);
		
		hand.addActor(w);
		hand.space(((90 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
		
		currentlyHeldCard = null;
		
		resetHand();
	}
	
	public void resetHand() {
		hand.remove();
		
//		HorizontalGroup originalHand = new HorizontalGroup();
//		for (int i = 0; i < hand.getChildren().size; i++) {
//			WidgetGroup g = (WidgetGroup)hand.getChild(i);
//			originalHand.addActor(g);
//		}
//		
//		hand = new HorizontalGroup();
//		for (int i = 0; i < originalHand.getChildren().size; i++) {
//			hand.addActor(originalHand.getChild(i));
//		}
		hand.invalidate();
		hand.align(Align.center);
		hand.space(((90 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
//		hand.setSize(Gdx.graphics.getWidth() - 200, 400);
		hand.setPosition(Gdx.graphics.getWidth() - cardWidth / 2,
				Gdx.graphics.getHeight() / 2);
		
		addActor(hand);
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
		if (Launcher.currentStage == Launcher.dev_stage) {
			if (Gdx.input.getInputProcessor() != this) {
				Gdx.input.setInputProcessor(this);
			}
		}
		
		Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		super.draw();

		totalPop.setText(Integer.toString(sum(pop)) + "M");

		if (currentlyHeldCard != null) {
			currentlyHeldCard.setSize(currentlyHeldCard.originalSize.x / 1.15f, currentlyHeldCard.originalSize.y / 1.15f);
			currentlyHeldCard.setPosition(
					Gdx.input.getX() + (Gdx.graphics.getWidth() / 2) - (currentlyHeldCard.getWidth() / 2),
					Gdx.graphics.getHeight() - Gdx.input.getY() + (Gdx.graphics.getHeight() >= 1080 ? currentlyHeldCard.getHeight() * 2 : currentlyHeldCard.getHeight()));
			if (!getActors().contains((Actor) currentlyHeldCard, true))
				addActor(currentlyHeldCard);
			
			if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
				if (placemat.getLeft().placematHover) {
					currentlyHeldCard.setPosition(placemat.getLeft().getX(), placemat.getLeft().getY());
					currentlyHeldCard.setSize(placemat.getLeft().getWidth(), placemat.getLeft().getHeight());
					placemat.setLeft(currentlyHeldCard);
					
					currentlyHeldCard.remove();
					currentlyHeldCard = null;
				} else if (placemat.getRightCard().placematHover) {
					currentlyHeldCard.setPosition(placemat.getRightCard().getX(), placemat.getRightCard().getY());
					currentlyHeldCard.setSize(placemat.getLeft().getWidth(), placemat.getLeft().getHeight());
					placemat.setRight(currentlyHeldCard);
					
					currentlyHeldCard.remove();
					currentlyHeldCard = null;
				} else if (placemat.getTopCard().placematHover) {
					currentlyHeldCard.setPosition(placemat.getTopCard().getX(), placemat.getTopCard().getY());
					currentlyHeldCard.setSize(placemat.getLeft().getWidth(), placemat.getLeft().getHeight());
					placemat.setTop(currentlyHeldCard);
					
					currentlyHeldCard.remove();
					currentlyHeldCard = null;
				} else {
					putBackInHand();
				}
			}
		}

		frameTime += Gdx.graphics.getDeltaTime();
		if (frameTime > 0.1) {
			fps.setText(Integer.toString((int) (1 / Gdx.graphics.getDeltaTime())));
			frameTime = 0f;
		}
		
		c.update();
	}
}