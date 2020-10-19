package com.cognitivethought.bpa;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Card {
	
	public static final ArrayList<Card> CARDS = new ArrayList<Card>();
	
	private Type type;

	private String name;
	private String desc;

	private long populationDelta;
	private long capacity;
	private long weight;
	
	private long quantity;
	
	public Card(Type type, String name, String desc, long popDelta, long cap, long weight, long quantity) {
		this.type = type;
		this.name = name;
		this.desc = desc;
		this.populationDelta = popDelta;
		this.capacity = cap;
		this.weight = weight;
		this.quantity = quantity;
	}
	
	public static void loadCards() {
		JSONObject cards;
		try {
			File f = new File(Strings.URL_LOCATOR + "assets\\json\\Cards.json");
			JSONParser parser = new JSONParser();
			cards = (JSONObject) parser.parse(new FileReader(f));
			
			for (Object o : cards.keySet()) {
				JSONArray json_card = (JSONArray)cards.get(o);
				JSONObject data = (JSONObject) json_card.get(0);
				String name = (String)data.get("Name");
				String type = (String)data.get("Type");
				String desc = (String)data.get("Description");
				long popDelta = (long)data.get("PopulationDelta");
				long weight = (long)data.get("Weight");
				long cap = (long)data.get("Capacity");
				long quantity = (long)data.get("Quantity");
				Card card = new Card(stringToType(type), name, desc, popDelta, cap, weight, quantity);
				for (int i = 0; i < quantity; i++) CARDS.add(card);
				System.out.println(name + "\n\t" + type + "\n\t" + desc + "\n\tPopulation Delta: " + popDelta + "\n\tCan Carry " + cap + " Megatons\n\tWeight: " + weight);
			}
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static Type stringToType(String s) {
		switch (s) {
		default:
			return null;
		case "WARHEAD":
			return Type.WARHEAD;
		case "SECRET":
			return Type.SECRET;
		case "DELIVERY_SYSTEM":
			return Type.DELIVERY_SYSTEM;
		case "SPECIAL":
			return Type.SPECIAL;
		case "PROPAGANDA":
			return Type.PROPAGANDA;
		}
	}
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getPopulationDelta() {
		return populationDelta;
	}

	public void setPopulationDelta(long populationDelta) {
		this.populationDelta = populationDelta;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}

	public long getWeight() {
		return weight;
	}

	public void setWeight(long weight) {
		this.weight = weight;
	}
	
	public long getQuantity() {
		return quantity;
	}
	
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
}

enum Type {
	WARHEAD, SECRET, DELIVERY_SYSTEM, SPECIAL, PROPAGANDA;
}