package com.cognitivethought.bpa.game;

import java.util.ArrayList;

import com.backendless.Backendless;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.cognitivethought.bpa.gamestages.GameStage;
import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.prefabs.Card;
import com.cognitivethought.bpa.prefabs.Placemat;
import com.cognitivethought.bpa.prefabs.PopulationCard;
import com.cognitivethought.bpa.prefabs.Spinner;

public class Player extends Group {
	
	public HorizontalGroup hand;
	public Placemat placemat;
	public Spinner spinner;
	public VerticalGroup population;
	
	public ArrayList<WidgetGroup> cards;
	public ArrayList<WidgetGroup> populationCards;
	
	public int pop_i = 100;
	public int country_id;
	
	public int[] possible_combos = {100, 50, 20, 10, 1};
	public int[] pop = split(pop_i);
	
	public float cardWidth = 0;
	public float popCardWidth = 0;
	
	public Label totalPop;
	public Label dispUsername;
	
	public Card initialState;
	public Card currentlyHeldCard;
	
	public String username;
	
	public boolean skipNextTurn = false;
	public boolean ready = false;
	
	public Player() {
	}
	
	public void populate(GameStage g) {
		username = Backendless.UserService.CurrentUser().getProperty("name").toString();
		totalPop = new Label("", g.labelStyle);
//		totalPop.setColor(Color.BLUE);

		cards = new ArrayList<>();
		populationCards = new ArrayList<>();

		hand = new HorizontalGroup();
		population = new VerticalGroup();
		placemat = new Placemat();
		spinner = new Spinner();

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
		hand.setPosition(Gdx.graphics.getWidth() - cardWidth / 2, Gdx.graphics.getHeight() / 2);

		population.align(Align.center);
		population.space(((60 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
		population.setPosition((Gdx.graphics.getWidth() * 1.5f) - (popCardWidth + 70), Gdx.graphics.getHeight());
		
		for (WidgetGroup wg : cards) {
			hand.addActor(wg);
		}

		for (WidgetGroup wg : populationCards) {
			population.addActor(wg);
		}

		totalPop.setPosition(population.getX(),
				population.getY() - ((((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)) * 32)
						* population.getChildren().size));
		totalPop.setAlignment(Align.left);
		
		
		float scale = 2f;
		placemat.setSize((400 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)),
				(224 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));
		placemat.setPosition(hand.getX() - (placemat.getWidth() / 2) + 100,
				(Gdx.graphics.getHeight() * 1.5f) - placemat.getHeight());
		placemat.setSize((400 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)),
				(224 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));

//		hand.setDebug(true, true);

		spinner.setSize(400 * scale, 224 * scale);
		spinner.setPosition(hand.getX() - (spinner.getWidth() / 2) + 100,
				(Gdx.graphics.getHeight() * 1.5f) - spinner.getHeight());
		
		dispUsername = new Label(username, g.labelStyle);
		dispUsername.setPosition(hand.getX() - (Gdx.graphics.getWidth() / 2) + dispUsername.getWidth(), Gdx.graphics.getHeight() * 1.4f);

		addActor(spinner.clickArrow);
		addActor(placemat.clickArrow);
		addActor(placemat);
		addActor(hand);
		addActor(population);
		addActor(totalPop);
		addActor(spinner);
		addActor(dispUsername);

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
	
	public void holdCard(Card card) {
		currentlyHeldCard = card;
		initialState = card;
		WidgetGroup wg = (WidgetGroup) card.getParent();

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
	
	int sum(int[] arr) {
		int ret = 0;
		for (int i : arr)
			ret += i;
		return ret;
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
		hand.setPosition(Gdx.graphics.getWidth() - cardWidth / 2, Gdx.graphics.getHeight() / 2);

		addActor(hand);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		placemat.clickArrow.setPosition(placemat.getX() + (placemat.getWidth() / 2)
				- (MainGameStage.warInitiated ? placemat.clickArrow.getWidth() : placemat.clickArrow.getWidth() / 2), placemat.getY() - placemat.clickArrow.getHeight() + 5);
		
		totalPop.setText(Integer.toString(sum(pop)) + "M");

		if (currentlyHeldCard != null) {
			currentlyHeldCard.setSize(currentlyHeldCard.originalSize.x / 1.15f,
					currentlyHeldCard.originalSize.y / 1.15f);
			currentlyHeldCard.setPosition(
					Gdx.input.getX() + (Gdx.graphics.getWidth() / 2) - (currentlyHeldCard.getWidth() / 2),
					Gdx.graphics.getHeight() - Gdx.input.getY()
							+ (Gdx.graphics.getHeight() >= 1080 ? currentlyHeldCard.getHeight() * 2
									: currentlyHeldCard.getHeight()));
			if (!getChildren().contains((Actor) currentlyHeldCard, true))
				addActor(currentlyHeldCard);

			if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
				if (placemat.getLeft().placematHover && (currentlyHeldCard.getType() == Card.Type.ANTI_MISSILE || currentlyHeldCard.getType() == Card.Type.DELIVERY_SYSTEM)) {
					currentlyHeldCard.setPosition(placemat.getLeft().getX(), placemat.getLeft().getY());
					currentlyHeldCard.setSize(placemat.getLeft().getWidth(), placemat.getLeft().getHeight());
					placemat.setLeft(currentlyHeldCard);

					currentlyHeldCard.remove();
					currentlyHeldCard = null;
				} else if (placemat.getRightCard().placematHover && (currentlyHeldCard.getType() == Card.Type.ANTI_MISSILE || currentlyHeldCard.getType() == Card.Type.DELIVERY_SYSTEM)) {
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
	}
	
	public void givePopulation(int quantity) {
		populationCards.clear();
		population.clear();
		
		pop_i += quantity;
		pop = split(pop_i);
		
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
		
		population.align(Align.center);
		population.space(((60 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
		population.setPosition((Gdx.graphics.getWidth() * 1.5f) - (popCardWidth + 70), Gdx.graphics.getHeight());

		for (WidgetGroup wg : populationCards) {
			population.addActor(wg);
		}

		totalPop.setPosition(population.getX(),
				population.getY() - ((((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)) * 32)
						* population.getChildren().size));
		totalPop.setAlignment(Align.left);
	}

	public void removePopulation(int quantity) {
		populationCards.clear();
		population.clear();
		
		pop_i -= quantity;
		pop = split(pop_i);
		
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
		
		population.align(Align.center);
		population.space(((60 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
		population.setPosition((Gdx.graphics.getWidth() * 1.5f) - (popCardWidth + 70), Gdx.graphics.getHeight());

		for (WidgetGroup wg : populationCards) {
			population.addActor(wg);
		}

		totalPop.setPosition(population.getX(),
				population.getY() - ((((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)) * 32)
						* population.getChildren().size));
		totalPop.setAlignment(Align.left);
	}
}