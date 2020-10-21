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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Align;

public class Card extends Widget {

	public static final ArrayList<Card> DECK = new ArrayList<Card>();

	private Label l_name, l_desc, l_type, l_capacity, l_weight;
//	private Table desc_wrap;

	private Type type;

	private String name;
	private String desc;

	private long populationDelta;
	private long capacity;
	private long weight;

	private long quantity;

	public int scale;
	public int rad = 20;

	public Vector2 originalPos, originalSize;

	private Pixmap pm;

	private boolean hovering;
	private boolean mouseDown;
	
	public int spacing = 0;

	private FreeTypeFontGenerator gen;
	private FreeTypeFontGenerator.FreeTypeFontParameter param;

	private LabelStyle nameStyle;
	private LabelStyle descStyle;
	private LabelStyle typeStyle;

	public Card(Card card) {
		type = card.type;
		name = card.name;
		desc = card.desc;

		populationDelta = card.populationDelta;
		capacity = card.capacity;
		weight = card.weight;

		quantity = card.quantity;

		scale = card.scale;
		rad = card.rad;

		originalPos = card.originalPos;
		originalSize = card.originalSize;

		pm = card.pm;

		hovering = card.hovering = false;

		spacing = card.spacing = 0;

		gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_UBUNTU_REGULAR));
		param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		nameStyle = new LabelStyle();
		descStyle = new LabelStyle();
		typeStyle = new LabelStyle();

		this.pm = new Pixmap(60, 100, Pixmap.Format.RGBA8888);

		int fontSize = 20;

		param.size = fontSize * (1 + scale);
		nameStyle.font = gen.generateFont(param);
		nameStyle.font.setColor(Color.BLACK);

		param.size = 10 * (1 + scale);
		descStyle.font = gen.generateFont(param);
		descStyle.font.setColor(Color.BLACK);

		param.size = 15 * (1 + scale);
		typeStyle.font = gen.generateFont(param);
		typeStyle.font.setColor(Color.BLACK);

		param.size = fontSize * (1 + scale);

		l_name = new Label(name, nameStyle);
		l_name.setColor(Color.BLACK);
		l_name.setFontScaleX(1f);

		l_desc = new Label(desc, descStyle);
		l_desc.setColor(Color.BLACK);
		l_desc.setFontScaleX(1f);

		l_type = new Label(type.toString().replace('_', ' '), typeStyle);
		l_type.setColor(Color.BLACK);
		l_type.setFontScaleX(1f);

//		desc_wrap = new Table();

		originalPos = new Vector2(getX(), getY());
	}
	
	public Card(Type type, String name, String desc, long popDelta, long cap, long weight, long quantity) {
		this.type = type;
		this.name = name;
		this.desc = desc;
		this.populationDelta = popDelta;
		this.capacity = cap;
		this.weight = weight;
		this.quantity = quantity;

		this.pm = new Pixmap(60, 100, Pixmap.Format.RGBA8888);

		gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_UBUNTU_REGULAR));
		param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		nameStyle = new LabelStyle();
		descStyle = new LabelStyle();
		typeStyle = new LabelStyle();

		int fontSize = 20;

		param.size = fontSize * (1 + scale);
		nameStyle.font = gen.generateFont(param);
		nameStyle.font.setColor(Color.BLACK);

		param.size = 10 * (1 + scale);
		descStyle.font = gen.generateFont(param);
		descStyle.font.setColor(Color.BLACK);

		param.size = 15 * (1 + scale);
		typeStyle.font = gen.generateFont(param);
		typeStyle.font.setColor(Color.BLACK);

		param.size = fontSize * (1 + scale);

		l_name = new Label(name, nameStyle);
		l_name.setColor(Color.BLACK);
		l_name.setFontScaleX(1f);

		l_desc = new Label(desc, descStyle);
		l_desc.setColor(Color.BLACK);
		l_desc.setFontScaleX(1f);

		l_type = new Label(type.toString().replace('_', ' '), typeStyle);
		l_type.setColor(Color.BLACK);
		l_type.setFontScaleX(1f);

//		desc_wrap = new Table();

		originalPos = new Vector2(getX(), getY());
	}
	
	public void drawCardShape(Pixmap pm) {
		Color fill = mouseDown ? Color.GRAY : Color.WHITE;
		Color outline = hovering ? Color.RED : Color.BLACK;

		if (type != null) {
			switch (type) {
			case DELIVERY_SYSTEM:
				fill = Color.CYAN;
				break;
			case PROPAGANDA:
				fill = Color.GRAY;
				break;
			case SECRET:
				fill = Color.LIME;
				break;
			case SPECIAL:
				fill = Color.GOLD;
				break;
			case WARHEAD:
				fill = Color.RED;
				break;
			default:
				fill = Color.WHITE;
				break;
			}
		}

		pm.setColor(fill);
		pm.fillCircle(rad, pm.getHeight() - rad, rad);
		pm.fillCircle(pm.getWidth() - rad, pm.getHeight() - rad, rad);
		pm.fillCircle(pm.getWidth() - rad, rad, rad);
		pm.fillCircle(rad, rad, rad);
		pm.setColor(outline);
		pm.drawCircle(rad, pm.getHeight() - rad, rad);
		pm.drawCircle(pm.getWidth() - rad, pm.getHeight() - rad, rad);
		pm.drawCircle(pm.getWidth() - rad, rad, rad);
		pm.drawCircle(rad, rad, rad);
		pm.fillRectangle(rad, 0, (pm.getWidth()) - (rad * 2) + 1, pm.getHeight());
		pm.fillRectangle(0, rad, pm.getWidth(), pm.getHeight() - (rad * 2));
		pm.setColor(fill);
		pm.fillRectangle(rad, 1, (pm.getWidth()) - (rad * 2) + 1, pm.getHeight() - 2);
		pm.fillRectangle(1, rad, pm.getWidth() - 2, pm.getHeight() - (rad * 2));
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(new Texture(pm), getX(), getY(), getWidth(), getHeight());
		
		pm.setColor(Color.CLEAR);
		pm.fill();
		
		Vector2 mouseScreenPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		Vector2 mouseLocalPosition = screenToLocalCoordinates(mouseScreenPosition);

		Actor box = hit(mouseLocalPosition.x, mouseLocalPosition.y, false);

		int hoverScale = 50;
		float speed = 10f;
		
		if (box != null) {
			if (mouseLocalPosition.x < box.getX() + spacing)
				hovering = true;
			else
				hovering = false;
		} else {
			hovering = false;
		}

		if (hovering) {
			this.getParent().toFront();
			Vector2 newPosition = new Vector2(getX(), getY())
					.lerp(new Vector2(originalPos.x, originalPos.y + hoverScale), Gdx.graphics.getDeltaTime() * speed);
			setPosition(newPosition.x, newPosition.y);
			Vector2 newSize = new Vector2(getWidth(), getHeight()).lerp(
					new Vector2(originalSize.x + hoverScale, originalSize.y + hoverScale),
					Gdx.graphics.getDeltaTime() * speed);
			setSize((int) newSize.x, (int) newSize.y);
			int name_newScale = (int) MathUtils.lerp(l_name.getFontScaleX(), 1.25f,
					Gdx.graphics.getDeltaTime() * speed);
			l_name.setFontScale(name_newScale);
			int desc_newScale = (int) MathUtils.lerp(l_desc.getFontScaleX(), 1.25f,
					Gdx.graphics.getDeltaTime() * speed);
			l_desc.setFontScale(desc_newScale);
			int type_newScale = (int) MathUtils.lerp(l_desc.getFontScaleX(), 1.25f,
					Gdx.graphics.getDeltaTime() * speed);
			l_type.setFontScale(type_newScale);
		} else {
			Vector2 newPosition = new Vector2(getX(), getY()).lerp(originalPos, Gdx.graphics.getDeltaTime() * speed);
			setPosition(newPosition.x, newPosition.y);
			Vector2 newSize = new Vector2(getWidth(), getHeight()).lerp(originalSize,
					Gdx.graphics.getDeltaTime() * speed);
			setSize((int) newSize.x, (int) newSize.y);
			int name_newScale = (int) MathUtils.lerp(l_name.getFontScaleX(), 1f, Gdx.graphics.getDeltaTime() * speed);
			l_name.setFontScale(name_newScale);
			int desc_newScale = (int) MathUtils.lerp(l_desc.getFontScaleX(), 1f, Gdx.graphics.getDeltaTime() * speed);
			l_desc.setFontScale(desc_newScale);
			int type_newScale = (int) MathUtils.lerp(l_desc.getFontScaleX(), 1f,
					Gdx.graphics.getDeltaTime() * speed);
			l_type.setFontScale(type_newScale);
		}

//		desc_wrap.setDebug(true, true);

		if ((int) l_name.getFontScaleX() <= 0)
			l_name.setFontScale(0.000001f);
		if ((int) l_desc.getFontScaleX() <= 0)
			l_desc.setFontScale(0.000001f);

		drawCardShape(pm);
		
		l_name.draw(batch, parentAlpha);
		l_desc.draw(batch, parentAlpha);
		l_type.draw(batch, parentAlpha);

//		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_UBUNTU_REGULAR));
//		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
//		param.size = 10 * (1 + scale);
//		gen.dispose();
	}

	public void setScale(int scale) {
		this.scale = scale;
		this.setSize(getWidth() * (1 + scale), getHeight() * (1 + scale));

		this.pm = new Pixmap(60 * (1 + scale), 100 * (1 + scale), Pixmap.Format.RGBA8888);
		
//		if (l_name != null) {
//			param.size = 20 * (1 + scale);
//			l_name.getStyle().font = gen.generateFont(param);
////			l_name.setPosition(getWidth() / 2, getHeight());
//			if (l_desc != null) {
//				param.size = 15 * (1 + scale);
//				l_desc.getStyle().font = gen.generateFont(param);
////				l_desc.setPosition(getWidth() / 2, l_name.getY() - l_desc.getHeight() - 10);
//			}
//		}

		originalSize = new Vector2(getWidth(), getHeight());
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);

		l_name.setWidth((int) (getWidth() + (getWidth() % l_name.getStyle().font.getLineHeight())) - 25);
		l_name.setWrap(true);
		l_name.setAlignment(Align.center);

		l_desc.setWidth((int) (getWidth() + (getWidth() % l_desc.getStyle().font.getCapHeight())) - 20);
		l_desc.setWrap(true);
		l_desc.setAlignment(Align.left);
//		desc_wrap.setPosition(getX(), getY());
		
		l_type.setWidth((int)(getWidth() + (getWidth() % l_type.getStyle().font.getCapHeight())) - 20);
		l_type.setWrap(false);
		l_type.setAlignment(Align.left);

//		l_name.setScale(1 + scale);
		l_name.setPosition((getX()), getHeight() - l_name.getHeight() - (l_name.getGlyphLayout().height / 2));
//		l_desc.setScale(1 + scale);
		l_desc.setPosition((getWidth() / 2) - ((l_desc.getWidth() / 2)), getHeight() - l_desc.getFontScaleX()
				- l_name.getHeight() - l_desc.getHeight() - (l_desc.getGlyphLayout().height / 2) - 25);
		l_type.setPosition((getX() + rad), getY() + l_type.getGlyphLayout().height);
	}

	public static void loadCards() {
		JSONObject cards;
		try {
			File f = new File(Strings.URL_LOCATOR + "assets\\json\\Cards.json");
			JSONParser parser = new JSONParser();
			cards = (JSONObject) parser.parse(new FileReader(f));

			for (Object o : cards.keySet()) {
				JSONArray json_card = (JSONArray) cards.get(o);
				JSONObject data = (JSONObject) json_card.get(0);
				String name = (String) data.get("Name");
				String type = (String) data.get("Type");
				String desc = (String) data.get("Description");
				long popDelta = (long) data.get("PopulationDelta");
				long weight = (long) data.get("Weight");
				long cap = (long) data.get("Capacity");
				long quantity = (long) data.get("Quantity");
				Card card = new Card(stringToType(type), name, desc, popDelta, cap, weight, quantity);
				for (int i = 0; i < quantity + 1; i++)
					DECK.add(card);
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
		return name /*
					 * + "\n\t" + type + "\n\t" + desc + "\n\tPopulation Delta: " + populationDelta
					 * + "\n\tCan Carry " + capacity + " Megatons\n\tWeight: " + weight
					 */;
	}

	public static Type stringToType(String s) {
		switch (s) {
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
		default:
			return Type.WARHEAD;
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
