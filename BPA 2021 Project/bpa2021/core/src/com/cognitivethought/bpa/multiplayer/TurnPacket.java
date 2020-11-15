package com.cognitivethought.bpa.multiplayer;

import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.prefabs.Card;

public class TurnPacket {
	
	String issuer;
	String requests;
	
	public static final int ADD_DETERR = 0;
	public static final int REVOKE = 1;
	public static final int GIVE = 2;
	public static final int TAKE = 3;
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	public void addDeterrent(MainGameStage mgs, String target, String deterrent_id, int leftOrRight) {
		int i = 0;
		for (;i < Card.DECK.size(); i++) {
			if (Card.DECK.get(i).getId().equals(deterrent_id))
				break;
		}
		if (leftOrRight == LEFT) {
			mgs.players.get(target).placemat.setLeft(Card.DECK.get(i));
		} else if (leftOrRight == RIGHT) {
			mgs.players.get(target).placemat.setRight(Card.DECK.get(i));
		}
	}
	
	public void revokeTurn(MainGameStage mgs, String target, int quantity) {
		mgs.players.get(target).skipNextTurn = true;
	}
	
	public void givePopulation(MainGameStage mgs, String target, int quantity) {
		mgs.players.get(target).givePopulation(quantity);
	}

	public void removePopulation(MainGameStage mgs, String target, int quantity) {
		mgs.players.get(target).removePopulation(quantity);
	}
	
	/**
	 * Example turn (Give Foo 3 pop, remove 3 pop from Bar, & add deterrent ATLAS to foo in left slot)
	 * 2;Foo;3$3;Bar;3$0;Foo;delivery_atlas;
	 * Becomes
	 * Command 1:
	 * 		2; Foo; 3
	 * Command 2:
	 * 		3; Bar; 3
	 * Command 3:
	 * 		0; Foo; delivery_atlas; 0;
	 * 
	 * Becomes
	 * 
	 * givePopulation("Foo", 3);
	 * takePopulation("Bar", 3);
	 * addDeterrent("Foo", "delivery_atlas", LEFT);
	 */
	public void execute(MainGameStage mgs) {
		String[] commands = requests.split("$");
		for (int i = 0; i < commands.length; i++) {
			String[] comm = commands[i].split(";");
			switch (Integer.parseInt(comm[0])) {
			case ADD_DETERR:
				addDeterrent(mgs, comm[1], comm[2], Integer.parseInt(comm[3]));
				break;
			case REVOKE:
				revokeTurn(mgs, comm[1], Integer.parseInt(comm[2]));
				break;
			case GIVE:
				givePopulation(mgs, comm[1], Integer.parseInt(comm[2]));
				break;
			case TAKE:
				removePopulation(mgs, comm[1], Integer.parseInt(comm[2]));
				break;
			}
		}
	}
	
	/**
	 * @return The player who issued the command to run the turn
	 */
	public String getIssuer() {
		return issuer;
	}

}
