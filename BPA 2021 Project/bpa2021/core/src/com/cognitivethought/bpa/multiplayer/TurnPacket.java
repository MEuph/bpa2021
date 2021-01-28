package com.cognitivethought.bpa.multiplayer;

import java.util.Arrays;

import com.cognitivethought.bpa.gamestages.MainGameStage;

/**
 * Sends turn data to server to be distributed as a string to players on the server
 * 
 * Example turn (Give Foo 3 pop, remove 3 pop from Bar, add deterrent ATLAS to Foo in left slot, & add 5m propaganda to Foo's placemat)
 * 2;Foo;3%3;Bar;3%0;Foo;delivery_atlas;0;%4;Foo;prop10mil;
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
	
	String issuer = "";
	String data = "";
	
	public static final int ADD_DETERR = 0;
	public static final int GIVE = 2;
	public static final int TAKE = 3;
	public static final int ADD_CARD = 4;
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int BOTTOM = 2;
	public static final int CENTER = 3;
	public static final int TOP = 4;
	
	public TurnPacket() {
		
	}
	
	public void data_addDeterrent(String target, String deterrent_id, int leftOrRight) {
		data += "%" + ADD_DETERR + ";" + target + ";" + deterrent_id + ";" + leftOrRight + ";";
	}
	
	public void data_givePopulation(String target, int quantity) {
		data += "%" + GIVE + ";" + target + ";" + quantity + ";";
		System.out.println("UPDATED DATA TO " + data);
	}
	
	public void data_removePopulation(String target, int quantity) {
		data += "%" + TAKE + ";" + target + ";" + quantity + ";";
		System.out.println("UPDATED DATA TO " + data);
	}
	
	public void data_addCard(String target, String card_id, int placeOnPlacemat) {
		data += "%" + ADD_CARD + ";" + target + ";" + card_id + ";" + placeOnPlacemat;
		System.out.println("id: " + card_id + " on placemat spot " + placeOnPlacemat);
		System.out.println("UPDATED DATA TO " + data);
	}
	
	public void data_removeCard(String target, String card_id, int placeOnPlacemat) {
		data.replace("%" + ADD_CARD + ";" + target + ";" + card_id + ";" + placeOnPlacemat, "");
		System.out.println("UPDATED DATA TO " + data);
	}
	
	public void exec_addDeterrent(MainGameStage mgs, String target, String deterrent_id, int leftOrRight) {
		int i = 0;
		for (;i < NuclearWarServer.DECK.size(); i++) {
			if (NuclearWarServer.DECK.get(i).getId().equals(deterrent_id))
				break;
		}
		if (leftOrRight == LEFT) {
			mgs.players.get(target).placemat.setLeftCard(NuclearWarServer.DECK.get(i));
		} else if (leftOrRight == RIGHT) {
			mgs.players.get(target).placemat.setRightCard(NuclearWarServer.DECK.get(i));
		}
		
		System.out.println("Added deterrent " + deterrent_id + "to " + (leftOrRight == LEFT ? "left " : "right ") + "slot (" + target + ")");
	}
	
	public void exec_givePopulation(MainGameStage mgs, String target, int quantity) {
		mgs.players.get(target).givePopulation(quantity);
		
		System.out.println("Gave " + quantity + "M population to " + target);
	}

	public void exec_removePopulation(MainGameStage mgs, String target, int quantity) {
		mgs.players.get(target).removePopulation(quantity);
		
		System.out.println("Removed " + quantity + "M population from " + target);
	}
	
	public void exec_addCard(MainGameStage mgs, String target, String card_id, int placeOnPlacemat) {
		int i = 0;
		for (;i < NuclearWarServer.DECK.size(); i++) { 
			if (NuclearWarServer.DECK.get(i).getId().equals(card_id)) break;
		}
		System.out.println("TARGET IS " + target);
		switch (placeOnPlacemat) {
		case BOTTOM:
			mgs.players.get(target).placemat.setBottomCard(NuclearWarServer.DECK.get(i));
			break;
		case CENTER:
			mgs.players.get(target).placemat.setCenterCard(NuclearWarServer.DECK.get(i));
			break;
		case TOP:
			mgs.players.get(target).placemat.setTopCard(NuclearWarServer.DECK.get(i));
			break;
		case LEFT:
			mgs.players.get(target).placemat.setLeftCard(NuclearWarServer.DECK.get(i));
			break;
		case RIGHT:
			mgs.players.get(target).placemat.setRightCard(NuclearWarServer.DECK.get(i));
			break;
		default:
			System.err.println(placeOnPlacemat + " is not a valid place to put a card on the placemat");
			break;
		}
		
		System.out.println("Put card " + card_id + " onto " + target + "\'s placemat" + " at " + placeOnPlacemat + "th slot");
	}
	
	public void execute(MainGameStage mgs) {
//		if (getIssuer().equals(mgs.clientPlayer.username)) return;
		if (data.isEmpty()) return;
		String[] commands = data.split("%");
		System.out.println(Arrays.toString(commands));
		for (int i = 0; i < commands.length; i++) {
			String[] comm = commands[i].split(";");
			if (comm.length > 0) {
				if (!comm[0].isEmpty()) {
					System.out.println(Arrays.toString(comm));
					switch (Integer.parseInt(comm[0])) {
					case ADD_DETERR:
						exec_addDeterrent(mgs, comm[1], comm[2], Integer.parseInt(comm[3]));
						break;
					case GIVE:
						exec_givePopulation(mgs, comm[1], Integer.parseInt(comm[2]));
						break;
					case TAKE:
						exec_removePopulation(mgs, comm[1], Integer.parseInt(comm[2]));
						break;
					case ADD_CARD:
						exec_addCard(mgs, comm[1], comm[2], Integer.parseInt(comm[3]));
					}
				}
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

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}


}
