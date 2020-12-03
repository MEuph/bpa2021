package com.cognitivethought.bpa.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.cognitivethought.bpa.tidiness.Strings;

public class GameMap extends Widget {
	
	public static final int ID_ASGUARD = 0;
	public static final int ID_BAGMAD = 1;
	public static final int ID_BANANALAND = 2;
	public static final int ID_BERMUDANIA = 3;
	public static final int ID_BITLAND = 4;
	public static final int ID_GREATBIGLAND = 5;
	public static final int ID_HAN = 6;
	public static final int ID_HINJA = 7;
	public static final int ID_HURRIA = 8;
	public static final int ID_NIPPYO = 9;
	public static final int ID_POPULA = 10;
	public static final int ID_RADONIA = 11;
	public static final int ID_VISALIA = 12;
	
	public Country[] countries = new Country[13];
	public Player[] players = new Player[13];
	
	public Sprite outline;
	
	public GameMap() {
		countries[ID_ASGUARD] = new Country(new Texture(Strings.URL_ASGUARD));
		countries[ID_BAGMAD] = new Country(new Texture(Strings.URL_BAGMAD));
		countries[ID_BANANALAND] = new Country(new Texture(Strings.URL_BANANALAND));
		countries[ID_BERMUDANIA] = new Country(new Texture(Strings.URL_BERMUDANIA));
		countries[ID_BITLAND] = new Country(new Texture(Strings.URL_BITLAND));
		countries[ID_GREATBIGLAND] = new Country(new Texture(Strings.URL_GREATBIGLAND));
		countries[ID_HAN] = new Country(new Texture(Strings.URL_HAN));
		countries[ID_HINJA] = new Country(new Texture(Strings.URL_HINJA));
		countries[ID_HURRIA] = new Country(new Texture(Strings.URL_HURRIA));
		countries[ID_NIPPYO] = new Country(new Texture(Strings.URL_NIPPYO));
		countries[ID_POPULA] = new Country(new Texture(Strings.URL_POPULA));
		countries[ID_RADONIA] = new Country(new Texture(Strings.URL_RADONIA));
		countries[ID_VISALIA] = new Country(new Texture(Strings.URL_VISALIA));
		
		outline = new Sprite(new Texture(Strings.URL_MAP_OUTLINE));
	}
	
	public void addPlayer(Player p) {
		players[p.country_id] = p;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		for (Country country : countries) {
			country.draw(batch);
		}
		
		outline.draw(batch);
	}
}