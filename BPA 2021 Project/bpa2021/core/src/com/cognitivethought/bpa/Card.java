package com.cognitivethought.bpa;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class Card extends Widget {
	
	public static final ArrayList<Card> DECK = new ArrayList<Card>();
	
	private Type type;

	private String name;
	private String desc;

	private long populationDelta;
	private long capacity;
	private long weight;
	
	private long quantity;
	
	public int scale;
	public int rad = 20;
	
	public Vector2 originalPos;
	
	private Pixmap pm;
	
	public Card(Type type, String name, String desc, long popDelta, long cap, long weight, long quantity) {
		this.type = type;
		this.name = name;
		this.desc = desc;
		this.populationDelta = popDelta;
		this.capacity = cap;
		this.weight = weight;
		this.quantity = quantity;
		
		this.pm = new Pixmap(60, 100, Pixmap.Format.RGBA8888);
		
		pm.setColor(Color.WHITE);
		pm.fillCircle(rad, pm.getHeight() - rad, rad);
		pm.fillCircle(pm.getWidth() - rad, pm.getHeight() - rad, rad);
		pm.fillCircle(pm.getWidth() - rad, rad, rad);
		pm.fillCircle(rad, rad, rad);
		pm.setColor(Color.RED);
		pm.drawCircle(rad, pm.getHeight() - rad, rad);
		pm.drawCircle(pm.getWidth() - rad, pm.getHeight() - rad, rad);
		pm.drawCircle(pm.getWidth() - rad, rad, rad);
		pm.drawCircle(rad, rad, rad);
		pm.fillRectangle(rad, 0, (pm.getWidth()) - (rad * 2) + 1, pm.getHeight());
		pm.fillRectangle(0, rad, pm.getWidth(), pm.getHeight() - (rad * 2));
		pm.setColor(Color.WHITE);
		pm.fillRectangle(rad, 1, (pm.getWidth()) - (rad * 2) + 1, pm.getHeight() - 2);
		pm.fillRectangle(1, rad, pm.getWidth() - 2, pm.getHeight() - (rad * 2));
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(new Texture(pm), getX(), getY(), getWidth(), getHeight());
		
		Vector2 mouseScreenPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		Vector2 mouseLocalPosition = screenToLocalCoordinates(mouseScreenPosition);
		
		if (hit(mouseLocalPosition.x, mouseLocalPosition.y, false) != null) {
			System.out.println("test");
		}
	}
	
	public void setScale(int scale) {
		this.scale = scale;
		this.setSize(getWidth() * (1 + scale), getHeight() * (1 + scale));
		
		this.pm = new Pixmap(60 * (1 + scale), 100 * (1 + scale), Pixmap.Format.RGBA8888);
		
		pm.setColor(Color.WHITE);
		pm.fillCircle(rad, pm.getHeight() - rad, rad);
		pm.fillCircle(pm.getWidth() - rad, pm.getHeight() - rad, rad);
		pm.fillCircle(pm.getWidth() - rad, rad, rad);
		pm.fillCircle(rad, rad, rad);
		pm.setColor(Color.RED);
		pm.drawCircle(rad, pm.getHeight() - rad, rad);
		pm.drawCircle(pm.getWidth() - rad, pm.getHeight() - rad, rad);
		pm.drawCircle(pm.getWidth() - rad, rad, rad);
		pm.drawCircle(rad, rad, rad);
		pm.fillRectangle(rad, 0, (pm.getWidth()) - (rad * 2) + 1, pm.getHeight());
		pm.fillRectangle(0, rad, pm.getWidth(), pm.getHeight() - (rad * 2));
		pm.setColor(Color.WHITE);
		pm.fillRectangle(rad, 1, (pm.getWidth()) - (rad * 2) + 1, pm.getHeight() - 2);
		pm.fillRectangle(1, rad, pm.getWidth() - 2, pm.getHeight() - (rad * 2));
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
				for (int i = 0; i < quantity; i++) DECK.add(card);
			}
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 1000; i++) {
			Collections.shuffle(DECK);
		}
		
		for (Card card : DECK) {
			System.out.println(card.toString());
		}
	}
	
	@Override
	public String toString() {
		return name /*+ "\n\t" + type + "\n\t" + desc + "\n\tPopulation Delta: " + populationDelta + "\n\tCan Carry " + capacity + " Megatons\n\tWeight: " + weight*/;
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
	
	public enum Type {
		WARHEAD, SECRET, DELIVERY_SYSTEM, SPECIAL, PROPAGANDA;
	}
}
