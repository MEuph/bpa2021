package com.cognitivethought.bpa.game;

import java.util.ArrayList;

import com.backendless.Backendless;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.cognitivethought.bpa.gamestages.GameStage;
import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.multiplayer.NuclearWarServer;
import com.cognitivethought.bpa.multiplayer.StringPacket;
import com.cognitivethought.bpa.multiplayer.TurnPacket;
import com.cognitivethought.bpa.prefabs.Card;
import com.cognitivethought.bpa.prefabs.Placemat;
import com.cognitivethought.bpa.prefabs.PopulationCard;
import com.cognitivethought.bpa.sound.Sounds;
import com.cognitivethought.bpa.tidiness.Strings;

/**
 * 
 * @author Christopher Harris
 * 
 * The player class is used to house data about players, such as population, placemat, and hand
 *
 */
public class Player extends WidgetGroup {
	
	// Used to display cards in a horizontal pattern
	public HorizontalGroup hand;
	
	// This player's placemat, used for showing deterrents and playing cards
	public Placemat placemat;
	
	// Used to display population in a vertical pattern
	public VerticalGroup population;

	// The rendered cards in the hand, since Card extends Widget
	public ArrayList<WidgetGroup> cards;
	
	// The rendered population cards since PopulationCard extends Widget
	public ArrayList<WidgetGroup> populationCards;

	// The primitive integer value of the player's population in millions
	public int populationInteger = 0;
	
	// The id of the country that this player is playing
	public int countryId;

	// The possible combos of population cards that have art created for them
	public int[] possibleCombos = { 100, 50, 20, 10, 1 };
	
	// Splits the population primitive value into population cards
	public int[] populationValues = split(populationInteger);
	
	// The width of the cards in the hand
	float cardWidth = 0;
	
	// The width of a population card
	public float popCardWidth = 0;
	
	// The total population this player has
	public Label totalPopulation;
	
	// The displayed username of this player
	public Label dispUsername;
	
	// The initial state of the currently held card
	public Card initialState;
	
	// The card that the player is currently holding
	public Card currentlyHeldCard;

	// The username of this player
	public String username;
	
	// Whether or not the player is going to skip their next turn. UNUSED
	public boolean skipNextTurn = false;
	
	// Whether or not the player is ready to begin the game. Used in MultiplayerQueueStage
	public boolean ready = false;
	
	// This player's turn, summarized into a TurnPacket to be sent to the server
	public TurnPacket tp;
	
	// Listeners to avoid OpenGL context errors in the NuclearWarServer class
	public boolean shouldDrawCard;
	public boolean shouldRefreshPopulation;
	
	// Provided blank constructor initially for the purpose of sending back and forth as packet. Ultimately did not work
	public Player() {
		
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		
		// Make the placemat's cards act
		placemat.act(delta);
		for (WidgetGroup card : cards) {
			if (card.getChildren().size > 0) {
				if (card.getChild(0) instanceof Card) {
					card.getChild(0).act(delta);	// Make all of the cards in the hand act
				}
			}
		}
	}
	
	/*
	 * Used to make a player draw a card. This also lets the server know that a card has been drawn from the deck
	 * and to update it accordingly for all other players
	 */
	public void drawCard() {
		System.out.println("Drawing card");
		
		// Get the top card on the deck
		Card card = new Card(NuclearWarServer.DECK.get(0));
		card.setSize(90, 140); // Set its size to the standard card size
		card.setScale(((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768))); // Scale it based on original development monitor
		card.spacing = ((int) (card.getWidth() / 2) - 2); // Space card out
		cardWidth = card.getWidth(); // Change the cardWidth variable accordingly since there is now a new possible standard
		
		// Move card up to simulate the card moving from the deck to the hand
		card.setPosition(card.getX(), card.getY() + 400);
		
		// The container of the card
		WidgetGroup w = new WidgetGroup();
		w.addActor(card); // Puts the card in the container
		
		// Further containerizes the card, allowing for the cards to be displayed horizonatally
		hand.addActor(w);
		
		// Remove the top card from the deck, and let the server know accordingly
		NuclearWarServer.DECK.remove(0);
		NuclearWarServer.client.sendTCP(new StringPacket("%drawCard%"));
		
		// Used to invalidate the layout of the hand, and force all the card positions to update
		resetHand();
	}
	
	/*
	 * Called when the player is first created and begins rendering in the MainGameStage
	 */
	public void populate(GameStage g, boolean isClientUser, String username) {
		
		System.out.println("BE Username: " + Backendless.UserService.CurrentUser().getProperty("name").toString());
		System.out.println("Given username: " + username);
		
		this.username = isClientUser ? Backendless.UserService.CurrentUser().getProperty("name").toString() : username;
		totalPopulation = new Label("", g.labelStyle);
		
		// ---- BEGIN UI Layout stuff ---- //
		tp = new TurnPacket();
		tp.setIssuer(username);

		cards = new ArrayList<>();
		populationCards = new ArrayList<>();

		hand = new HorizontalGroup();
		population = new VerticalGroup();
		placemat = new Placemat();
		
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

		for (int i = 0; i < possibleCombos.length; i++) {
			if (occurences(populationValues, possibleCombos[i]) > 0) {
				PopulationCard p = new PopulationCard(possibleCombos[i], occurences(populationValues, possibleCombos[i]));

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

		totalPopulation.setPosition(population.getX(),
				population.getY() - ((((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)) * 32)
						* population.getChildren().size));
		totalPopulation.setAlignment(Align.left);

		float scale = 2f;
		placemat.setSize((400 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)),
				(224 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));
		placemat.setPosition(hand.getX() - (placemat.getWidth() / 2) ,
				(Gdx.graphics.getHeight() * 1.5f) - placemat.getHeight());
		placemat.setSize((400 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)),
				(224 * scale) * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)));

		dispUsername = new Label(username, g.labelStyle);
		dispUsername.setPosition(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() * 1.4f);

		addActor(placemat.clickArrow);
		addActor(placemat);
		addActor(hand);
		addActor(population);
		addActor(totalPopulation);
		addActor(dispUsername);
		
		// ---- END UI Layout stuff ---- //
		
		// Sets the population to approximately the population of the United States
		populationInteger = 328;
		
		// Refreshes the population cards to reflect the change
		refreshPopulation();
	}

	/*
	 * Splits an integer, num, into an array of integers given limits on what values are allowed into the final list
	 */
	public int[] split(int num) {
		ArrayList<Integer> a = new ArrayList<>(); // Allows for a dynamically changing array

		for (int i = 0; i < possibleCombos.length && num > 0; i++) {
			while (num - possibleCombos[i] >= 0) { // If possible_combos[i] is contained within num
				a.add(possibleCombos[i]); // Add to the dynamic array
				num -= possibleCombos[i]; // Subtract from the original number
			}
		}

		// Convert the dynamic array to a fixed array and return the fixed array
		int[] ret = new int[a.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = a.get(i);
		}

		return ret;
	}

	// Find how many times a specific variable, x, appears in an array
	public int occurences(int[] array, int x) {
		int ret = 0;

		for (int i : array)
			if (i == x)
				ret++;

		return ret;
	}
	
	/*
	 * Allows the player to hold a card
	 */
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

	/*
	 * Allows the player to put a card back in their hand if they change their mind while holding the card
	 */
	public void putBackInHand() {
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
	
	/*
	 * Returns the sum of an array
	 */
	int sum(int[] array) {
		int ret = 0;
		for (int i : array)
			ret += i;
		return ret;
	}
	
	/*
	 * Resets the positions of all the cards in the hand
	 */
	public void resetHand() {
		hand.remove();

		hand.invalidate();
		hand.align(Align.center);
		hand.space(((90 * (1 + (((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)))))) / 2);
		hand.setPosition(Gdx.graphics.getWidth() - cardWidth / 2, Gdx.graphics.getHeight() / 2);

		addActor(hand);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		// If the player ran out of cards, give them 5 more
		if (hand.getChildren().size <= 0) {
			if (username.equals(Launcher.currentUser.getProperty("name").toString())) {
				for (int i = 0; i < 5; i++) {
					drawCard();
				}
			}
		}
		
		// If war has been declared, inform the user of such an event using their displayed username
		if (((MainGameStage)Launcher.game_stage).warInitiated) {
			if (!dispUsername.getText().contains("\n\n\nWar Declared")) {
				dispUsername.setText(dispUsername.getText() + "\n\n\nWar Declared\n\n\nPropaganda\nNow Useless!");
			}
		}
		
		// Used to avoid OpenGL Context errors
		if (shouldRefreshPopulation) {
			refreshPopulation();
			shouldRefreshPopulation = false;
		}
		
		// Sets the placemat's clickarrow in the middle-bottom of the placemat at all times
		placemat.clickArrow.setPosition(
				placemat.getX() + (placemat.getWidth() / 2)
						- (/*(MainGameStage.warInitiated ? placemat.clickArrow.getWidth()
								: */placemat.clickArrow.getWidth() / 2),
				placemat.getY() - placemat.clickArrow.getHeight() + 5);
		
		// Sums up the total population values and sets the totalpopulation information label to this summation
		totalPopulation.setText(Integer.toString(sum(populationValues)) + "M");
		
		/*
		 * Quite honestly a mess that works. This was written during an absolute heck of a caffeine rush very early in the project
		 * This allows cards to be placed on the placemat basically
		 */
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
					int vol_i = (int) Backendless.UserService.CurrentUser().getProperty("nw_volume");
					float vol = (float)(vol_i) / 100f;
					Sounds.card_placed.play(vol);
				} else if (placemat.getRightCard().placematHover) {
					currentlyHeldCard.prePlacematName = currentlyHeldCard.getLName();
					currentlyHeldCard.setPosition(placemat.getRightCard().getX(), placemat.getRightCard().getY());
					currentlyHeldCard.setSize(placemat.getLeftCard().getWidth(), placemat.getLeftCard().getHeight());
					placemat.setRightCard(currentlyHeldCard);
					tp.data_addCard(this.username, placemat.getRightCard().getId(), TurnPacket.RIGHT);
					currentlyHeldCard.remove();
					currentlyHeldCard = null;
					int vol_i = (int) Backendless.UserService.CurrentUser().getProperty("nw_volume");
					float vol = (float)(vol_i) / 100f;
					Sounds.card_placed.play(vol);
				} else if (placemat.getBottomCard().placematHover) {
					System.out.println(currentlyHeldCard.getId());
					// TODO: This is what determines the card being placed on the placemat
					currentlyHeldCard.setPosition(placemat.getBottomCard().getX(), placemat.getBottomCard().getY());
					currentlyHeldCard.setSize(placemat.getLeftCard().getWidth(), placemat.getLeftCard().getHeight());
					placemat.setBottomCard(currentlyHeldCard);
					tp.data_addCard(this.username, placemat.getBottomCard().getId(), TurnPacket.BOTTOM);
					currentlyHeldCard.remove();
					currentlyHeldCard = null;
					int vol_i = (int) Backendless.UserService.CurrentUser().getProperty("nw_volume");
					float vol = (float)(vol_i) / 100f;
					Sounds.card_placed.play(vol);
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
					int vol_i = (int) Backendless.UserService.CurrentUser().getProperty("nw_volume");
					float vol = (float)(vol_i) / 100f;
					Sounds.card_placed.play(vol);
				} else if (placemat.getTopCard().placematHover) {
					System.out.println(currentlyHeldCard.getId());
					// TODO: This is what determines the card being placed on the placemat
					currentlyHeldCard.setPosition(placemat.getTopCard().getX(), placemat.getTopCard().getY());
					currentlyHeldCard.setSize(placemat.getTopCard().getWidth(), placemat.getTopCard().getHeight());
					placemat.setTopCard(currentlyHeldCard);
					tp.data_addCard(this.username, placemat.getTopCard().getId(), TurnPacket.TOP);
					currentlyHeldCard.remove();
					currentlyHeldCard = null;
					int vol_i = (int) Backendless.UserService.CurrentUser().getProperty("nw_volume");
					float vol = (float)(vol_i) / 100f;
					Sounds.card_placed.play(vol);
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

	/*
	 * Give this player population
	 */
	public void givePopulation(int quantity) {
		System.out.println("Gave " + quantity + "M population to " + username);
		
		populationInteger += quantity;
		shouldRefreshPopulation = true;
	}

	/*
	 * Take population from this player
	 */
	public void removePopulation(int quantity) {
		System.out.println("Removed " + quantity + "M population from " + username);

		populationInteger -= quantity;
		shouldRefreshPopulation = true;
	}
	
	/*
	 * Refresh the population cards of this player
	 */
	public void refreshPopulation() {
		populationValues = split(populationInteger);

		populationCards.clear();
		population.clear();

		for (int i = 0; i < possibleCombos.length; i++) {
			if (occurences(populationValues, possibleCombos[i]) > 0) {
				PopulationCard p = new PopulationCard(possibleCombos[i], occurences(populationValues, possibleCombos[i]));

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

		totalPopulation.setPosition(population.getX(),
				population.getY() - ((((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768)) * 32)
						* population.getChildren().size));
		totalPopulation.setAlignment(Align.left);
	}
}