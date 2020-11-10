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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.launcher.Launcher;

public class Card extends Widget {
	
	public static final int WIDTH = 90 * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768));
	public static final int HEIGHT = 140 * ((Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) / (1366 / 768));
	
	public static final ArrayList<Card> DECK = new ArrayList<Card>();

	public static final Card BLANK = new Card(Type.BLANK, "", "", 0, 0, 0, 1, null, "");

	private Label l_name, l_desc, l_type;
//	private Table desc_wrap;

	private Type type;

	private Image art;

	private String name;
	private String desc;
	private String short_desc;

	private Color fontColor;

	private long populationDelta;
	private long capacity;
	private long weight;

	private long quantity;
	
	public int scale;
	public int rad = 20;

	public boolean placematHover = false;
	
	public Vector2 originalPos, originalSize;

	private Pixmap pm;
	private Texture tex;
	
	private boolean hovering;

	public int spacing = 0;

	private FreeTypeFontGenerator gen;
	private FreeTypeFontGenerator.FreeTypeFontParameter param;

	private LabelStyle nameStyle;
	private LabelStyle descStyle;
	private LabelStyle typeStyle;
	
	private TextTooltip t;
	
	public Card(Card card) {
		type = card.type;
		name = card.name;
		desc = card.desc;
		
		short_desc = card.short_desc;
		
		art = card.art;

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

		gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		nameStyle = new LabelStyle();
		descStyle = new LabelStyle();
		typeStyle = new LabelStyle();
		
		t = card.t;

		switch (type) {
		case ANTI_MISSILE:
			fontColor = Colors.FONT_AM;
			break;
		case DELIVERY_SYSTEM:
			fontColor = Colors.FONT_DS;
			break;
		case PROPAGANDA:
			fontColor = Colors.FONT_PR;
			break;
		case SECRET:
			fontColor = Colors.FONT_SE;
			break;
		case SPECIAL:
			fontColor = Colors.FONT_SP;
			break;
		case WARHEAD:
			fontColor = Colors.FONT_WH;
			break;
		case BLANK:
			fontColor = Color.BLACK;
			break;
		default:
			break;
		}
		
		init();
	}

	public Card(Type type, String name, String desc, long popDelta, long cap, long weight, long quantity,
			String art_path, String short_desc) {
		this.type = type;
		this.name = name;
		this.desc = desc;
		this.populationDelta = popDelta;
		this.capacity = cap;
		this.weight = weight;
		this.quantity = quantity;
		
		this.short_desc = short_desc;
		
		if (art_path != null)
			art = new Image(new Texture(Strings.URL_LOCATOR + art_path));
		else
			art = null;

		switch (type) {
		case ANTI_MISSILE:
			fontColor = Colors.FONT_AM;
			break;
		case DELIVERY_SYSTEM:
			fontColor = Colors.FONT_DS;
			break;
		case PROPAGANDA:
			fontColor = Colors.FONT_PR;
			break;
		case SECRET:
			fontColor = Colors.FONT_SE;
			break;
		case SPECIAL:
			fontColor = Colors.FONT_SP;
			break;
		case WARHEAD:
			fontColor = Colors.FONT_WH;
			break;
		case BLANK:
			fontColor = Color.BLACK;
			break;
		default:
			break;
		}

		init();
	}

	public void init() {
		originalSize = new Vector2(getWidth(), getHeight());

		this.pm = new Pixmap(60, 100, Pixmap.Format.RGBA8888);

		gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		nameStyle = new LabelStyle();
		descStyle = new LabelStyle();
		typeStyle = new LabelStyle();

		int fontSize = 15;

		param.size = fontSize * (1 + scale);
		nameStyle.font = gen.generateFont(param);
		nameStyle.font.setColor(fontColor);

		param.size = 7 * (1 + scale);
		descStyle.font = gen.generateFont(param);
		descStyle.font.setColor(fontColor);

		param.size = 10 * (1 + scale);
		typeStyle.font = gen.generateFont(param);
		typeStyle.font.setColor(fontColor);

		param.size = fontSize * (1 + scale);

		l_name = new Label(name, nameStyle);
		l_name.setColor(fontColor);
		l_name.setFontScaleX(1f);
		l_name.setWidth(l_name.getWidth() - 20);

		l_desc = new Label(desc, descStyle);
		l_desc.setColor(fontColor);
		l_desc.setFontScaleX(1f);

		TextTooltipStyle ttStyle = new TextTooltipStyle();
		Pixmap ttPm = new Pixmap((int)100, (int)300, Pixmap.Format.RGBA8888);
		ttPm.setColor(0, 0, 0, 0.2f);
		ttPm.fill();
		ttStyle.background = new Image(new Texture(ttPm)).getDrawable();
		ttPm.dispose();
		ttStyle.label = descStyle;
		ttStyle.wrapWidth = 1;
		this.t = new TextTooltip(desc, MainGameStage.MANAGER, ttStyle);

		l_type = new Label(type.toString().replace('_', ' '), typeStyle);
		l_type.setColor(fontColor);
		l_type.setFontScaleX(1f);

//		desc_wrap = new Table();

		originalPos = new Vector2(getX(), getY());

		final Card card = this;
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				if (type != Type.BLANK)
					((MainGameStage) Launcher.dev_stage).holdCard(card);
			}
		});
		
		gen.dispose();
		
		tex = new Texture(drawCardShape());
//		pm.dispose();
	}

	public Pixmap drawCardShape() {
		Color fill = null;
		if (type != null) {
			switch (type) {
			case DELIVERY_SYSTEM:
				fill = Colors.FILL_DS;
				break;
			case PROPAGANDA:
				fill = Colors.FILL_PR;
				break;
			case SECRET:
				fill = Colors.FILL_SE;
				break;
			case SPECIAL:
				fill = Colors.FILL_SP;
				break;
			case WARHEAD:
				fill = Colors.FILL_WH;
				break;
			case ANTI_MISSILE:
				fill = Colors.FILL_AM;
				break;
			default:
				fill = Color.WHITE;
				break;
			}
		}

		Pixmap ret = new Pixmap((int) WIDTH, (int) HEIGHT, Pixmap.Format.RGBA8888);
		ret.setColor(fill);
		ret.fillCircle(rad, ret.getHeight() - rad, rad);
		ret.fillCircle(ret.getWidth() - rad, ret.getHeight() - rad, rad);
		ret.fillCircle(ret.getWidth() - rad, rad, rad);
		ret.fillCircle(rad, rad, rad);
		ret.setColor(Color.BLACK);
		ret.drawCircle(rad, ret.getHeight() - rad, rad);
		ret.drawCircle(ret.getWidth() - rad, ret.getHeight() - rad, rad);
		ret.drawCircle(ret.getWidth() - rad, rad, rad);
		ret.drawCircle(rad, rad, rad);
		ret.fillRectangle(rad, 0, (ret.getWidth()) - (rad * 2) + 1, ret.getHeight());
		ret.fillRectangle(0, rad, ret.getWidth(), ret.getHeight() - (rad * 2));
		ret.setColor(fill);
		ret.fillRectangle(rad, 1, (ret.getWidth()) - (rad * 2) + 1, ret.getHeight() - 2);
		ret.fillRectangle(1, rad, ret.getWidth() - 2, ret.getHeight() - (rad * 2));
		
		return ret;
	}

	public void play() {
		if (type != Type.BLANK) System.out.println("Played card!");
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (getWidth() < getHeight()) {
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

			if (((MainGameStage) Launcher.dev_stage).currentlyHeldCard != null) {
				if (((MainGameStage) Launcher.dev_stage).currentlyHeldCard.equals(this)) {
					hovering = false;
				}
			}

			if (hovering) {
				this.getParent().toFront();
				Vector2 newPosition = new Vector2(getX(), getY()).lerp(
						new Vector2(originalPos.x, originalPos.y + hoverScale), Gdx.graphics.getDeltaTime() * speed);
				setPosition(newPosition.x, newPosition.y);
				Vector2 newSize = new Vector2(getWidth(), getHeight()).lerp(
						new Vector2(originalSize.x + hoverScale, originalSize.y + hoverScale),
						Gdx.graphics.getDeltaTime() * speed);
				setSize((int) newSize.x, (int) newSize.y);
			} else {
				if (((MainGameStage) Launcher.dev_stage).currentlyHeldCard != null) {
					if (!((MainGameStage) Launcher.dev_stage).currentlyHeldCard.equals(this)) {
						Vector2 newPosition = new Vector2(getX(), getY()).lerp(originalPos,
								Gdx.graphics.getDeltaTime() * speed);
						setPosition(newPosition.x, newPosition.y);
						Vector2 newSize = new Vector2(getWidth(), getHeight()).lerp(originalSize,
								Gdx.graphics.getDeltaTime() * speed);
						setSize((int) newSize.x, (int) newSize.y);
					} else {
						l_name.setFontScale(1f);
						l_desc.setFontScale(1f);
						l_type.setFontScale(1f);
					}
				} else {
					Vector2 newPosition = new Vector2(getX(), getY()).lerp(originalPos,
							Gdx.graphics.getDeltaTime() * speed);
					setPosition(newPosition.x, newPosition.y);
					Vector2 newSize = new Vector2(getWidth(), getHeight()).lerp(originalSize,
							Gdx.graphics.getDeltaTime() * speed);
					setSize((int) newSize.x, (int) newSize.y);
				}
			}

			// desc_wrap.setDebug(true, true);

			if ((int) l_name.getFontScaleX() <= 0)
				l_name.setFontScale(0.000001f);
			if ((int) l_desc.getFontScaleX() <= 0)
				l_desc.setFontScale(0.000001f);

			batch.draw(tex, getX(), getY(), getWidth(), getHeight());

			if (type == Type.WARHEAD) {
				art.setSize(getWidth() / 1.15f, getWidth() / 1.15f);
				art.setPosition(getX() + (getWidth() / 2) - (art.getWidth() / 2), getY() + (art.getHeight() / 7f));
				art.draw(batch, parentAlpha);
			}

			l_name.draw(batch, parentAlpha);
			l_desc.draw(batch, parentAlpha);
			l_type.draw(batch, parentAlpha);

			// FreeTypeFontGenerator gen = new
			// FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_UBUNTU_REGULAR));
			// FreeTypeFontGenerator.FreeTypeFontParameter param = new
			// FreeTypeFontGenerator.FreeTypeFontParameter();
			// param.size = 10 * (1 + scale);
			// gen.dispose();
		} else {
			if (type == Type.BLANK) {
				Vector2 mousePos = new Vector2(Gdx.input.getX() + Gdx.graphics.getWidth() / 2 - (16 / 2),
						(Gdx.graphics.getHeight() * 1.5f) - Gdx.input.getY() - (16 / 2));
				
				Rectangle bounds = new Rectangle(getX(), getY(), getWidth(), getHeight());
				Rectangle cursor = new Rectangle(mousePos.x, mousePos.y, 16, 16);
				
				placematHover = bounds.overlaps(cursor);
			} else {
				batch.draw(tex, getX(), getY(), getWidth(), getHeight());
				
				if (!getListeners().contains(t, false)) {
					addListener(t);
				}
				
				// desc_wrap.setDebug(true, true);
	
				if (type == Type.WARHEAD) {
					art.setSize(getHeight() - 15, getHeight() - 15);
					art.setPosition(getX() + getWidth() - art.getWidth(), getY());
					art.draw(batch, parentAlpha);
				}
				
				l_desc.setPosition(getX(), l_name.getY());
				l_desc.setWidth(getWidth() / 2);
				l_desc.setText(short_desc);
				l_desc.draw(batch, parentAlpha);
			}
		}
	}

	public void setScale(int scale) {
		this.scale = scale;
		this.setSize(getWidth() * (1 + scale), getHeight() * (1 + scale));

//		this.pm = new Pixmap(60 * (1 + scale), 100 * (1 + scale), Pixmap.Format.RGBA8888);

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

		l_type.setWidth((int) (getWidth() + (getWidth() % l_type.getStyle().font.getCapHeight())) - 20);
		l_type.setWrap(false);
		l_type.setAlignment(Align.left);

//		l_name.setScale(1 + scale);
		l_name.setPosition((getX()) + 5,
				getY() + getHeight() - l_name.getHeight() - (l_name.getGlyphLayout().height / 2));
//		l_desc.setScale(1 + scale);
		l_desc.setPosition(getX() + (getWidth() / 2) - ((l_desc.getWidth() / 2)),
				getY() + getHeight() - l_desc.getFontScaleX() - l_name.getHeight() - l_desc.getHeight()
						- (l_desc.getGlyphLayout().height / 2) - 40);
		l_type.setPosition((getX() + rad), getY() + l_type.getGlyphLayout().height);
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);

		setSize(getWidth(), getHeight());
	}

	public static void loadCards() {
		JSONObject cards;
		try {
			File f = new File(Strings.URL_LOCATOR + "\\json\\Cards.json");
			JSONParser parser = new JSONParser();
			cards = (JSONObject) parser.parse(new FileReader(f));

			for (Object o : cards.keySet()) {
				JSONArray json_card = (JSONArray) cards.get(o);
				JSONObject data = (JSONObject) json_card.get(0);
				String name = (String) data.get("Name");
				String type = (String) data.get("Type");
				String desc = (String) data.get("Description");
				String short_desc = (String)data.get("Short Desc");
				String art_path = "";
				try {
					art_path = (String) data.get("Path");
				} catch (Exception e) {
					art_path = "";
				}
				long popDelta = (long) data.get("PopulationDelta");
				long weight = (long) data.get("Weight");
				long cap = (long) data.get("Capacity");
				long quantity = (long) data.get("Quantity");
				Card card = new Card(stringToType(type), name, desc, popDelta, cap, weight, quantity, art_path, short_desc);
				for (int i = 0; i < quantity + 1; i++)
					DECK.add(card);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 1000; i++) {
			Collections.shuffle(DECK);
		}

//		for (Card card : DECK) {
//			System.out.println(card.toString());
//		}
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
		case "ANTI_MISSILE":
			return Type.ANTI_MISSILE;
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
		WARHEAD, SECRET, DELIVERY_SYSTEM, SPECIAL, PROPAGANDA, ANTI_MISSILE, BLANK;
	}
}
