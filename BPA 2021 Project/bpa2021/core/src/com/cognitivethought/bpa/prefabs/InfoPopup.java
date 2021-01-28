package com.cognitivethought.bpa.prefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.cognitivethought.bpa.prefabs.Card.Type;
import com.cognitivethought.bpa.tidiness.Strings;

public class InfoPopup extends WidgetGroup {

	Label player_name;
	Label deterrents;
	Label population;
	Country country;

	Sprite bg;

	public InfoPopup() {
		Texture t = new Texture(Strings.URL_IMG_UI_DARK);

		t.getTextureData().prepare();
		Pixmap pm = t.getTextureData().consumePixmap();
		pm.setColor(Color.DARK_GRAY);
		pm.drawRectangle(0, 0, pm.getWidth(), pm.getHeight());

		t = new Texture(pm);

		bg = new Sprite(t);

		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();

		LabelStyle nameStyle, infoStyle;

		nameStyle = new LabelStyle();
		infoStyle = new LabelStyle();

		param.size = 15;
		nameStyle.font = gen.generateFont(param);

		param.size = 10;
		infoStyle.font = gen.generateFont(param);

		nameStyle.fontColor = Color.WHITE;
		infoStyle.fontColor = Color.LIGHT_GRAY;

		player_name = new Label("", nameStyle);
		deterrents = new Label("", infoStyle);
		population = new Label("", infoStyle);

		player_name.setAlignment(Align.center);
		deterrents.setAlignment(Align.center);
		population.setAlignment(Align.center);

		player_name.setWrap(true);
		deterrents.setWrap(true);
		population.setWrap(true);

		addActor(player_name);
		addActor(deterrents);
		addActor(population);
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);

		bg.setPosition(x, y);
		player_name.setPosition(x, y + getHeight() - player_name.getHeight() - 5);
		deterrents.setPosition(x, y + getHeight() / 2);
		population.setPosition(x, y + population.getHeight());
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);

		bg.setSize(width, height);
		player_name.setSize(width, 30);
		deterrents.setSize(width, 30);
		population.setSize(width, 30);
//		information.space((getHeight() - 20) / 3);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		bg.draw(batch);

//		System.out.println();
//		System.out.println();
//		System.out.println();
//		
//		System.out.println(new Vector2(bg.getWidth(), bg.getHeight()).toString());
//		System.out.println(new Vector2(information.getWidth(), information.getHeight()).toString());
//		
//		System.out.println(new Vector2(player_name.getWidth(), player_name.getHeight()).toString());
//		System.out.println(new Vector2(deterrents.getWidth(), deterrents.getHeight()).toString());
//		System.out.println(new Vector2(population.getWidth(), population.getHeight()).toString());
//		
//		System.out.println();
//		
//		System.out.println(new Vector2(bg.getX(), bg.getY()).toString());
//		System.out.println(new Vector2(information.getX(), information.getY()).toString());
//		
//		System.out.println(new Vector2(player_name.getX(), player_name.getY()).toString());
//		System.out.println(new Vector2(deterrents.getX(), deterrents.getY()).toString());
//		System.out.println(new Vector2(population.getX(), population.getY()).toString());

		player_name.draw(batch, parentAlpha);
		population.draw(batch, parentAlpha);
		deterrents.draw(batch, parentAlpha);
	}

	public void setCountry(Country country) {
		this.country = country;

		if (country.getAssignedPlayer() != null) {
			player_name.setText(country.getAssignedPlayer().username);
			population.setText("Population: " + country.getAssignedPlayer().pop_i + "M");
			int i = 0;
			String d = "";

			if (country.getAssignedPlayer().placemat.getLeftCard().getType() != Type.BLANK) {
				i++;
				switch (country.getAssignedPlayer().placemat.getLeftCard().getType()) {
				case WARHEAD:
					d += country.getAssignedPlayer().placemat.getLeftCard().getLName().getText() + "\n\n";
					break;
				case DELIVERY_SYSTEM:
					d += "Delivery System\nof " + country.getAssignedPlayer().placemat.getLeftCard().getLName().getText().toString() + "\n\n";
					break;
				case PROPAGANDA:
					d += "Propaganda: "
					+ country.getAssignedPlayer().placemat.getLeftCard().getLName().getText().toString()
					+ "\n\n";
					break;
				default:
					break;
				}
			}

			if (country.getAssignedPlayer().placemat.getRightCard().getType() != Type.BLANK) {
				i++;
				switch (country.getAssignedPlayer().placemat.getRightCard().getType()) {
				case WARHEAD:
					d += country.getAssignedPlayer().placemat.getRightCard().getLName().getText() + "\n\n";
					break;
				case DELIVERY_SYSTEM:
					d += country.getAssignedPlayer().placemat.getRightCard().getLName().getText() + "\n\n";
					break;
				case PROPAGANDA:
					d += "Propaganda: "
					+ country.getAssignedPlayer().placemat.getRightCard().getLName().getText().toString()
					+ "\n\n";
					break;
				default:
					break;
				}
			}

			deterrents.setText(i + " Deterrents\n\n" + d);
		}
	}
}