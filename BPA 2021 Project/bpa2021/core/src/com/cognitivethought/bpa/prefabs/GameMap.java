package com.cognitivethought.bpa.prefabs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.cognitivethought.bpa.game.Player;
import com.cognitivethought.bpa.tidiness.Strings;

public class GameMap extends Group {

	public static final int ID_NONE = 0;
	public static final int ID_ASGUARD = 1;
	public static final int ID_BAGMAD = 2;
	public static final int ID_BANANALAND = 3;
	public static final int ID_BERMUDANIA = 4;
	public static final int ID_BITLAND = 5;
	public static final int ID_GREATBIGLAND = 6;
	public static final int ID_HAN = 7;
	public static final int ID_HINJA = 8;
	public static final int ID_HURRIA = 9;
	public static final int ID_NIPPYO = 10;
	public static final int ID_POPULA = 11;
	public static final int ID_RADONIA = 12;
	public static final int ID_VISALIA = 13;

	public Country[] countries = new Country[14];
	public Player[] players = new Player[14];

	public Sprite outline;
	public Sprite background;

	private float mapBorderScale = 0.05f;

	public GameMap() {
		countries[ID_ASGUARD] = new Country(ID_ASGUARD, new Texture(Strings.URL_ASGUARD));
		countries[ID_BAGMAD] = new Country(ID_BAGMAD, new Texture(Strings.URL_BAGMAD));
		countries[ID_BANANALAND] = new Country(ID_BANANALAND, new Texture(Strings.URL_BANANALAND));
		countries[ID_BERMUDANIA] = new Country(ID_BERMUDANIA, new Texture(Strings.URL_BERMUDANIA));
		countries[ID_BITLAND] = new Country(ID_BITLAND, new Texture(Strings.URL_BITLAND));
		countries[ID_GREATBIGLAND] = new Country(ID_GREATBIGLAND, new Texture(Strings.URL_GREATBIGLAND));
		countries[ID_HAN] = new Country(ID_HAN, new Texture(Strings.URL_HAN));
		countries[ID_HINJA] = new Country(ID_HINJA, new Texture(Strings.URL_HINJA));
		countries[ID_HURRIA] = new Country(ID_HURRIA, new Texture(Strings.URL_HURRIA));
		countries[ID_NIPPYO] = new Country(ID_NIPPYO, new Texture(Strings.URL_NIPPYO));
		countries[ID_POPULA] = new Country(ID_POPULA, new Texture(Strings.URL_POPULA));
		countries[ID_RADONIA] = new Country(ID_RADONIA, new Texture(Strings.URL_RADONIA));
		countries[ID_VISALIA] = new Country(ID_VISALIA, new Texture(Strings.URL_VISALIA));

		outline = new Sprite(new Texture(Strings.URL_MAP_OUTLINE));
		background = new Sprite(new Texture(Strings.URL_MAP_BACKGROUND));

		for (int i = 1; i < countries.length; i++) {
			countries[i].setZIndex(0);
		}
	}

	public void addPlayer(Player p) {
		players[p.country_id] = p;
		if (p.country_id != 0)
			countries[p.country_id].assignPlayer(p);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		batch.draw(background, getX() - (getWidth() * mapBorderScale), getY() - (getHeight() * mapBorderScale),
				getWidth() + (getWidth() * mapBorderScale * 2), getHeight() + (getHeight() * mapBorderScale * 2));
		batch.draw(outline, getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		background.setSize(getWidth() + (getWidth() * mapBorderScale * 2), getHeight() + (getHeight() * mapBorderScale * 2));
		outline.setSize(getWidth(), getHeight());
		for (int i = 1; i < countries.length; i++) {
			countries[i].setSize(width, height);
		}
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		background.setSize(width + (width * mapBorderScale * 2), getHeight() + (getHeight() * mapBorderScale * 2));
		outline.setSize(width, outline.getHeight());
		for (int i = 1; i < countries.length; i++) {
			countries[i].setWidth(width);
		}
	}

	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		background.setSize(getWidth() + (getWidth() * mapBorderScale * 2), height + (height * mapBorderScale * 2));
		outline.setSize(outline.getWidth(), height);
		for (int i = 1; i < countries.length; i++) {
			countries[i].setHeight(height);
		}
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		background.setPosition(getX() - (getWidth() * mapBorderScale), getY() - (getHeight() * mapBorderScale));
		outline.setPosition(x, y);
		for (int i = 1; i < countries.length; i++) {
			countries[i].setPosition(x, y);
		}
	}

	public static String idToString(int country_id) {
		switch (country_id) {
		case ID_NONE:
			return "None";
		case ID_ASGUARD:
			return "Asguard";
		case ID_BAGMAD:
			return "Bagmad";
		case ID_BANANALAND:
			return "Bananaland";
		case ID_BERMUDANIA:
			return "Bermudania";
		case ID_BITLAND:
			return "Bitland";
		case ID_GREATBIGLAND:
			return "Great Bigland";
		case ID_HAN:
			return "Han";
		case ID_HINJA:
			return "Hinja";
		case ID_HURRIA:
			return "Hurria";
		case ID_NIPPYO:
			return "Nippyo";
		case ID_POPULA:
			return "Popula";
		case ID_RADONIA:
			return "Radonia";
		case ID_VISALIA:
			return "Visalia";
		default:
			return "";
		}
	}
}