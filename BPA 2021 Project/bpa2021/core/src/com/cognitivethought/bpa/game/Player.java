package com.cognitivethought.bpa.game;

import java.util.ArrayList;
import java.util.Random;

import com.backendless.Backendless;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.cognitivethought.bpa.gamestages.GameStage;
import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.multiplayer.NuclearWarServer;
import com.cognitivethought.bpa.multiplayer.StringPacket;
import com.cognitivethought.bpa.multiplayer.TurnPacket;
import com.cognitivethought.bpa.prefabs.Card;
import com.cognitivethought.bpa.prefabs.Placemat;
import com.cognitivethought.bpa.prefabs.PopulationCard;
import com.cognitivethought.bpa.prefabs.Spinner;
import com.cognitivethought.bpa.tidiness.Strings;

public class Player extends WidgetGroup {

	public HorizontalGroup hand;
	public Placemat placemat;
	public Spinner spinner;
	public VerticalGroup population;

	public ArrayList<WidgetGroup> cards;
	public ArrayList<WidgetGroup> populationCards;

	public int pop_i = 100;
	public int country_id;

	public int[] possible_combos = { 100, 50, 20, 10, 1 };
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

	public TurnPacket tp;
	public boolean shouldDrawCard;
	public boolean shouldRefreshPopulation;
	
	public Player() {
		
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		placemat.act(delta);
		for (WidgetGroup card : cards) {
			if (card.getChildren().size > 0) {
				if (card.getChild(0) instanceof Card) {
					card.getChild(0).act(delta);
				}
			}
		}
	}
	
	public void drawCard() {
		System.out.println("Drawing card");
		
		Card card = new Card(NuclearWarServer.DECK.get(0));
		card.setSize(90, 140);
		card.setScale(((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));
		card.spacing = ((int) (card.getWidth() / 2) - 2);
		cardWidth = card.getWidth();
		
		card.setPosition(card.getX(), card.getY() + 400);
		
		WidgetGroup w = new WidgetGroup();
		w.addActor(card);
		
		hand.addActor(w);
		
		NuclearWarServer.DECK.remove(0);
		NuclearWarServer.client.sendTCP(new StringPacket("%drawCard%"));
		
		resetHand();
	}
	
	public void populate(GameStage g, boolean isClientUser, String username) {
		
		System.out.println("BE Username: " + Backendless.UserService.CurrentUser().getProperty("name").toString());
		System.out.println("Given username: " + username);
		
		
		this.username = isClientUser ? Backendless.UserService.CurrentUser().getProperty("name").toString() : username;
		totalPop = new Label("", g.labelStyle);
//		totalPop.setColor(Color.BLUE);

		tp = new TurnPacket();
		tp.setIssuer(username);

		cards = new ArrayList<>();
		populationCards = new ArrayList<>();

		hand = new HorizontalGroup();
		population = new VerticalGroup();
		placemat = new Placemat();
		spinner = new Spinner();
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 15;
		
		Pixmap labelBg = new Pixmap(200, 200, Pixmap.Format.RGBA8888);
		labelBg.setColor(new Color(0, 0, 0, 0));
		labelBg.fill();

		LabelStyle labelStyle = new LabelStyle();
		param.size = 30;
		labelStyle.font = gen.generateFont(param);
		labelStyle.fontColor = Color.BLACK;
		labelStyle.background = new Image(new Texture(labelBg)).getDrawable();
		
		if (isClientUser) {
			for (int i = 0; i < 8; i++) {
				drawCard();
			}
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
		placemat.setPosition(hand.getX() - (placemat.getWidth() / 2) ,
				(Gdx.graphics.getHeight() * 1.5f) - placemat.getHeight());
		placemat.setSize((400 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)),
				(224 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));

//		hand.setDebug(true, true);

		spinner.setSize(400 * scale, 224 * scale);
		spinner.setPosition(hand.getX() - (spinner.getWidth() / 2) + 100,
				(Gdx.graphics.getHeight() * 1.5f) - spinner.getHeight());

		dispUsername = new Label(username, g.labelStyle);
		dispUsername.setPosition(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() * 1.4f);

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
		
		for (int i = 0; i < 8; i++) {
			pop_i += possible_combos[new Random().nextInt(possible_combos.length)];
		}
		
		refreshPopulation();
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
		currentlyHeldCard.getLDesc().setText("");
		currentlyHeldCard.getLName().setText("");
		currentlyHeldCard.getLType().setText("");
		WidgetGroup wg = (WidgetGroup) card.getParent();
		hand.removeActor(wg);
		hand.space(((90 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
	}

	public void putBackInHand() {
//		currentlyHeldCard = initialState;
		currentlyHeldCard.reset();
		currentlyHeldCard.resetLDesc();
		currentlyHeldCard.resetLName();
		currentlyHeldCard.resetLType();
		currentlyHeldCard.setSize(currentlyHeldCard.originalSize.x, currentlyHeldCard.originalSize.y);
		currentlyHeldCard.setPosition(0, 0);
		currentlyHeldCard.getLName().setFontScale(1.0f);
		System.out.println("new name: " + currentlyHeldCard.getName() + ", " + currentlyHeldCard.getLName().getText()
				+ ", " + currentlyHeldCard.getLName().getWidth() + ", " + currentlyHeldCard.getLName().getFontScaleX()
				+ ", " + currentlyHeldCard.getLName().isVisible());
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
		
		if (shouldRefreshPopulation) {
			refreshPopulation();
			shouldRefreshPopulation = false;
		}
		
		placemat.clickArrow.setPosition(
				placemat.getX() + (placemat.getWidth() / 2)
						- (MainGameStage.warInitiated ? placemat.clickArrow.getWidth()
								: placemat.clickArrow.getWidth() / 2),
				placemat.getY() - placemat.clickArrow.getHeight() + 5);

		totalPop.setText(Integer.toString(sum(pop)) + "M");

		if (currentlyHeldCard != null) {
			currentlyHeldCard.setSize(currentlyHeldCard.originalSize.x / 2f, currentlyHeldCard.originalSize.y / 2f);
			currentlyHeldCard.setPosition(
					Gdx.input.getX() + (Gdx.graphics.getWidth() / 2) - (currentlyHeldCard.getWidth() / 2),
					Gdx.graphics.getHeight() - Gdx.input.getY()
							+ (Gdx.graphics.getHeight() >= 1080 ? currentlyHeldCard.getHeight() * 3.5f
									: currentlyHeldCard.getHeight() * 2));
			if (!getChildren().contains((Actor) currentlyHeldCard, true))
				addActor(currentlyHeldCard);

			if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
				if (placemat.getLeftCard().placematHover) {
					currentlyHeldCard.prePlacematName = currentlyHeldCard.getLName();
					currentlyHeldCard.setPosition(placemat.getLeftCard().getX(), placemat.getLeftCard().getY());
					currentlyHeldCard.setSize(placemat.getLeftCard().getWidth(), placemat.getLeftCard().getHeight());
					placemat.setLeftCard(currentlyHeldCard);
					tp.data_addCard(this.username, placemat.getLeftCard().getId(), TurnPacket.LEFT);
					currentlyHeldCard.remove();
					currentlyHeldCard = null;
				} else if (placemat.getRightCard().placematHover) {
					currentlyHeldCard.prePlacematName = currentlyHeldCard.getLName();
					currentlyHeldCard.setPosition(placemat.getRightCard().getX(), placemat.getRightCard().getY());
					currentlyHeldCard.setSize(placemat.getLeftCard().getWidth(), placemat.getLeftCard().getHeight());
					placemat.setRightCard(currentlyHeldCard);
					tp.data_addCard(this.username, placemat.getRightCard().getId(), TurnPacket.RIGHT);
					currentlyHeldCard.remove();
					currentlyHeldCard = null;
				} else if (placemat.getBottomCard().placematHover) {
					System.out.println(currentlyHeldCard.getId());
					// TODO: This is what determines the card being placed on the placemat
					currentlyHeldCard.setPosition(placemat.getBottomCard().getX(), placemat.getBottomCard().getY());
					currentlyHeldCard.setSize(placemat.getLeftCard().getWidth(), placemat.getLeftCard().getHeight());
					placemat.setBottomCard(currentlyHeldCard);
					tp.data_addCard(this.username, placemat.getBottomCard().getId(), TurnPacket.BOTTOM);
					currentlyHeldCard.remove();
					currentlyHeldCard = null;
				} else if (placemat.getCenterCard().placematHover) {
					System.out.println(currentlyHeldCard.getId());
					// TODO: This is what determines the card being placed on the placemat
					currentlyHeldCard.setPosition(placemat.getCenterCard().getX(), placemat.getCenterCard().getY());
					currentlyHeldCard.setSize(placemat.getCenterCard().getWidth(),
							placemat.getCenterCard().getHeight());
					placemat.setCenterCard(currentlyHeldCard);
					tp.data_addCard(this.username, placemat.getCenterCard().getId(), TurnPacket.CENTER);
					currentlyHeldCard.remove();
					currentlyHeldCard = null;
				} else if (placemat.getTopCard().placematHover) {
					System.out.println(currentlyHeldCard.getId());
					// TODO: This is what determines the card being placed on the placemat
					currentlyHeldCard.setPosition(placemat.getTopCard().getX(), placemat.getTopCard().getY());
					currentlyHeldCard.setSize(placemat.getTopCard().getWidth(), placemat.getTopCard().getHeight());
					placemat.setTopCard(currentlyHeldCard);
					tp.data_addCard(this.username, placemat.getTopCard().getId(), TurnPacket.TOP);
					currentlyHeldCard.remove();
					currentlyHeldCard = null;
				} else {
					putBackInHand();
				}
			}
		} else if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
			System.out.println("Left pressed while not holding card. That\'s something i guess");
			if (placemat.getLeftCard().placematHover && (currentlyHeldCard == null ? currentlyHeldCard == null
					: currentlyHeldCard.getType().equals(Card.Type.BLANK)) && !placemat.getLeftCard().getType().equals(Card.Type.BLANK)) {
				currentlyHeldCard = placemat.getLeftCard();
				currentlyHeldCard.resetSize();
				placemat.setLeftCard(Card.BLANK);
				tp.data_removeCard(this.username, placemat.getRightCard().getId(), TurnPacket.RIGHT);
			} else if (placemat.getRightCard().placematHover && (currentlyHeldCard == null ? currentlyHeldCard == null
					: currentlyHeldCard.getType().equals(Card.Type.BLANK)) && !placemat.getRightCard().getType().equals(Card.Type.BLANK)) {
				currentlyHeldCard = placemat.getRightCard();
				currentlyHeldCard.resetSize();
				placemat.setRightCard(Card.BLANK);
				tp.data_removeCard(this.username, placemat.getRightCard().getId(), TurnPacket.RIGHT);
			}
		}
	}

	public void givePopulation(int quantity) {
		System.out.println("Gave " + quantity + "M population to " + username);
		
		pop_i += quantity;
		shouldRefreshPopulation = true;
	}

	public void removePopulation(int quantity) {
		System.out.println("Removed " + quantity + "M population from " + username);

		pop_i -= quantity;
		shouldRefreshPopulation = true;
	}
	
	public void refreshPopulation() {
		pop = split(pop_i);

		populationCards.clear();
		population.clear();

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