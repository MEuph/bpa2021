package com.cognitivethought.bpa.multiplayer;

import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.prefabs.Card;

/**
 * Sends turn data to server to be distributed as a string to players on the server
 * 
 * Example turn (Give Foo 3 pop, remove 3 pop from Bar, add deterrent ATLAS to Foo in left slot, & add 5m propaganda to Foo's placemat)
 * 2;Foo;3$3;Bar;3$0;Foo;delivery_atlas;0;$4;Foo;prop10mil;
 * Becomes
 * Command 1:
 * 		2; Foo; 3
 * Command 2:
 * 		3; Bar; 3
 * Command 3:
 * 		0; Foo; delivery_atlas; 0;
 * Command 4:
 * 		4; Foo; prop10mil;
 * 
 * Becomes
 * 
 * givePopulation(mgs, "Foo", 3);
 * takePopulation(mgs, "Bar", 3);
 * addDeterrent(mgs, "Foo", "delivery_atlas", LEFT);
 * addCard(mgs, "Foo", "prop10mil");
 */
public class TurnPacket {
	
	String issuer;
	String data;
	
	public static final int ADD_DETERR = 0;
	public static final int REVOKE = 1;
	public static final int GIVE = 2;
	public static final int TAKE = 3;
	public static final int ADD_CARD = 4;
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	public TurnPacket() {
		
	}
	
	public void data_addDeterrent(String target, String deterrent_id, int leftOrRight) {
		data += "$" + ADD_DETERR + ";" + target + ";" + deterrent_id + ";" + leftOrRight + ";";
	}
	
	public void data_revokeTurn(String target, int quantity) {
		data += "$" + REVOKE + ";" + target + ";" + quantity + ";";
	}
	
	public void data_givePopulation(String target, int quantity) {
		data += "$" + GIVE + ";" + target + ";" + quantity + ";";
	}
	
	public void data_removePopulation(String target, int quantity) {
		data += "$" + TAKE + ";" + target + ";" + quantity + ";";
	}
	
	public void data_addCard(String target, String card_id) {
		data += "$" + ADD_CARD + ";" + target + ";" + card_id + ";";
	}
	
	public void exec_addDeterrent(MainGameStage mgs, String target, String deterrent_id, int leftOrRight) {
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
		
		System.out.println("Added deterrent " + deterrent_id + "to " + (leftOrRight == LEFT ? "left " : "right ") + "slot (" + target + ")");
	}
	
	public void exec_revokeTurn(MainGameStage mgs, String target, int quantity) {
		mgs.players.get(target).skipNextTurn = true;
		
		System.out.println("Revoked " + quantity + " turn(s) from " + target);
	}
	
	public void exec_givePopulation(MainGameStage mgs, String target, int quantity) {
		mgs.players.get(target).givePopulation(quantity);
		
		System.out.println("Gave " + quantity + "M population to " + target);
	}

	public void exec_removePopulation(MainGameStage mgs, String target, int quantity) {
		mgs.players.get(target).removePopulation(quantity);
		
		System.out.println("Remoked " + quantity + "M population from " + target);
	}
	
	public void exec_addCard(MainGameStage mgs, String target, String card_id) {
		int i = 0;
		for (;i < Card.DECK.size(); i++) { 
			if (Card.DECK.get(i).getId().equals(card_id)) break;
		}
		mgs.players.get(target).placemat.advance();
		mgs.players.get(target).placemat.setBottom(Card.DECK.get(i));
		
		System.out.println("Put card " + card_id + " onto " + target + "\'s placemat");
	}
	
	public void execute(MainGameStage mgs) {
		String[] commands = data.split("$");
		for (int i = 0; i < commands.length; i++) {
			String[] comm = commands[i].split(";");
			switch (Integer.parseInt(comm[0])) {
			case ADD_DETERR:
				exec_addDeterrent(mgs, comm[1], comm[2], Integer.parseInt(comm[3]));
				break;
			case REVOKE:
				exec_revokeTurn(mgs, comm[1], Integer.parseInt(comm[2]));
				break;
			case GIVE:
				exec_givePopulation(mgs, comm[1], Integer.parseInt(comm[2]));
				break;
			case TAKE:
				exec_removePopulation(mgs, comm[1], Integer.parseInt(comm[2]));
				break;
			case ADD_CARD:
				exec_addCard(mgs, comm[1], comm[2]);
			}
		}
	}
	
	public void reset() {
		data = "";
	}
	
	/**
	 * @return The player who issued the command to run the turn
	 */
	public String getIssuer() {
		return issuer;
	}

}
