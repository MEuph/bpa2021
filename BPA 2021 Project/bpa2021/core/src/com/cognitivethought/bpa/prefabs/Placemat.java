package com.cognitivethought.bpa.prefabs;

import com.backendless.Backendless;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cognitivethought.bpa.external.GifDecoder;
import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.launcher.Launcher;
import com.cognitivethought.bpa.prefabs.Card.Type;
import com.cognitivethought.bpa.sound.Sounds;
import com.cognitivethought.bpa.tidiness.Strings;

public class Placemat extends Group {

	// 83 x 46 CARD SIZE

	public static float cardWidth = 33;
	public static float cardHeight = 49;
	public static float matWidth = 440, matHeight = 224;
	
	float downY, upY;
	
	Card left, right, bottom, top, center;
	Pixmap mat;
	Texture crack;
	Texture tex;
	Pixmap arrow;
	Texture aTex;
	public ImageButton clickArrow;
	Texture outline;
	Animation<TextureRegion> background;
	float elapsed;
	Label errorLabel;
	
	boolean isDown = false;
	public boolean firstTurn = true;
	
	boolean highlightTop = false,
			highlightCenter = false,
			highlightBottom = false;
	
	int alphaTop = 0, alphaCenter = 1, alphaBottom = 2;
	int[][][] alphaMap;
	
	public Placemat() {
		mat = new Pixmap(new FileHandle(Strings.URL_PLACEMAT_SPOTS));
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(Strings.URL_PIXEL_FONT_REGULAR));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 15;
		
		tex = new Texture(mat);
		crack = new Texture(new Pixmap(new FileHandle(Strings.URL_PLACEMAT_CRACK)));
		
		// NOTE: GifDecoder written by Johannes Borchardt and converted to the latest LibGDX version by Anton Persson
		background = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, new FileHandle(Strings.URL_PLACEMAT_BACKGROUND).read());
		
		arrow = new Pixmap(new FileHandle(Strings.URL_PLACEMAT_ARROW));
		
		aTex = new Texture(arrow);
		Image i = new Image(aTex);
		clickArrow = new ImageButton(i.getDrawable());
		
		outline = new Texture(new Pixmap(new FileHandle(Strings.URL_PLACEMAT_OUTLINE)));
		
		LabelStyle style = new LabelStyle();
		param.size = 9;
		style.font = gen.generateFont(param);
		style.fontColor = Color.RED;
		errorLabel = new Label("Please fill the center column of cards,\n\nthen click [Next Turn]", style);
		errorLabel.setPosition(getX() + 20, getY() + getHeight() - 70);
		
		clickArrow.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				isDown = !isDown;
				if (isDown) {
					int vol_i = (int) Backendless.UserService.CurrentUser().getProperty("nw_volume");
					float vol = (float)(vol_i) / 100f;
					Sounds.placemat_pulled_down.play(vol);
				}
			}
		});

		left = new Card(Card.BLANK);
		right = new Card(Card.BLANK);
		bottom = new Card(Card.BLANK);
		top = new Card(Card.BLANK);
		center = new Card(Card.BLANK);
		
		addActor(errorLabel);
	}
	
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);

		if (!isDown) {
			downY = getY();
			setPosition(getX(), getY() + getHeight());
			upY = getY();
		}		
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);

		errorLabel.setPosition(getX() + 20, getY() + getHeight() - 70);
		
		left.setPosition(getX() + pixelToRelative(64, 131).x, getY() + pixelToRelative(64, 131).y);
		left.setSize(cardWidth * (getWidth() / matWidth) + 5, cardHeight * (getHeight() / matHeight));
		
		right.setPosition(getX() + pixelToRelative(305, 131).x, getY() + pixelToRelative(305, 131).y);
		right.setSize(cardWidth * (getWidth() / matWidth) + 5, cardHeight * (getHeight() / matHeight));
		
		top.setPosition(getX() + pixelToRelative(185, 64).x, getY() + pixelToRelative(185, 64).y);
		top.setSize(cardWidth * (getWidth() / matWidth) + 5, cardHeight * (getHeight() / matHeight));
		
		center.setPosition(getX() + pixelToRelative(185, 131).x, getY() + pixelToRelative(185, 131).y);
		center.setSize(cardWidth * (getWidth() / matWidth) + 5, cardHeight * (getHeight() / matHeight));
		
		bottom.setPosition(getX() + pixelToRelative(185, 198).x, getY() + pixelToRelative(185, 198).y);
		bottom.setSize(cardWidth * (getWidth() / matWidth) + 5, cardHeight * (getHeight() / matHeight));
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (getTopCard().getType() != Card.Type.BLANK) getTopCard().act(delta);
		if (getCenterCard().getType() != Card.Type.BLANK) getCenterCard().act(delta);
		if (getBottomCard().getType() != Card.Type.BLANK) getBottomCard().act(delta);
		if (getRightCard().getType() != Card.Type.BLANK) getRightCard().act(delta);
		if (getLeftCard().getType() != Card.Type.BLANK) getLeftCard().act(delta);
	}
	
	public Card getLeftCard() {
		return left;
	}

	public void setLeftCard(Card left) {
		this.left = left;
	}

	public Card getRightCard() {
		return right;
	}

	public void setRightCard(Card right) {
		this.right = right;
	}

	public Card getBottomCard() {
		return bottom;
	}

	public void setBottomCard(Card bottom) {
		this.bottom = bottom;
	}

	public Card getTopCard() {
		return top;
	}

	public void setTopCard(Card top) {
		this.top = top;
	}

	public Card getCenterCard() {
		return center;
	}

	public void setCenterCard(Card center) {
		this.center = center;
	}

	public Vector2 pixelToRelative(float xPix, float yPix) {
		float retX = 0;
		float retY = 0;
		
		retX = (xPix * getWidth()) / mat.getWidth();
		retY = ((matHeight - yPix) * getHeight()) / mat.getHeight();

		return new Vector2(retX, retY);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		setHighlighting(false/*getTopCard().getType() == Card.Type.BLANK*/, getCenterCard().getType() == Card.Type.BLANK, getBottomCard().getType() == Card.Type.BLANK);
		
		elapsed += Gdx.graphics.getDeltaTime() * 5;
		batch.draw(background.getKeyFrame(elapsed), getX(), getY(), getWidth(), getHeight());
		batch.draw(tex, getX(), getY(), getWidth(), getHeight());
		clickArrow.draw(batch, parentAlpha);
		
		left.draw(batch, parentAlpha);
		bottom.draw(batch, parentAlpha);
		center.draw(batch, parentAlpha);
		top.draw(batch, parentAlpha);
		right.draw(batch, parentAlpha);
		
		left.isOnPlacemat = true;
		bottom.isOnPlacemat = true;
		center.isOnPlacemat = true;
		top.isOnPlacemat = true;
		right.isOnPlacemat = true;
		
		left.isDeterrent = left.getType() != Type.BLANK;
		right.isDeterrent = right.getType() != Type.BLANK;
		bottom.isDeterrent = false;
		center.isDeterrent = false;
		top.isDeterrent = false;
		
		if (isDown) {
			if (getY() >= downY + 2) {
				Vector2 newPos = new Vector2(getX(), getY()).
						lerp(new Vector2(0, downY), Gdx.graphics.getDeltaTime() * 5f);
				setPosition(getX(), newPos.y);
				clickArrow.setPosition(getX() + (getWidth() / 2) - (clickArrow.getWidth()), getY() - clickArrow.getHeight() + 5);
			} else if (getY() < downY + 2) {
				setPosition(getX(), downY);
				clickArrow.setPosition(getX() + (getWidth() / 2) - (clickArrow.getWidth()), getY() - clickArrow.getHeight() + 5);
			}
			
			((MainGameStage)getStage()).map.clearPopups();
		}
		
		if (!isDown && getY() <= upY - 2) {
			Vector2 newPos = new Vector2(getX(), getY()).
					lerp(new Vector2(0, upY), Gdx.graphics.getDeltaTime() * 5f);
			setPosition(getX(), newPos.y);
			clickArrow.setPosition(getX() + (getWidth() / 2) - (clickArrow.getWidth()), getY() - clickArrow.getHeight() + 5);
		} else if (!isDown && getY() > upY - 2) {
			setPosition(getX(), upY);
			clickArrow.setPosition(getX() + (getWidth() / 2) - (clickArrow.getWidth()), getY() - clickArrow.getHeight() + 5);
		}
		
		if (!getTopCard().getType().equals(Card.Type.BLANK) && !getCenterCard().getType().equals(Card.Type.BLANK) && !getBottomCard().getType().equals(Card.Type.BLANK) && firstTurn) {
			firstTurn = false;
			errorLabel.setText("");
		}
		
		batch.draw(outline, getX(), getY(), getWidth(), getHeight());
		if (((MainGameStage)Launcher.game_stage).clientPlayer.populationInteger <= 25)
			batch.draw(crack, getX(), getY(), getWidth(), getHeight());
		if (firstTurn)
			errorLabel.draw(batch, parentAlpha);
		else if (!firstTurn && !errorLabel.getText().toString().isEmpty()) {
			errorLabel.draw(batch, parentAlpha);
		}

		super.draw(batch, parentAlpha);
		
		if (getLeftCard() == null) {
			setLeftCard(Card.BLANK);
		}
		
		if (getRightCard() == null) {
			setRightCard(Card.BLANK);
		}
	}

	public void advance(MainGameStage mgs, String advancingPlayer) {
		if (!(getTopCard().getType() != Card.Type.BLANK && getCenterCard().getType() != Card.Type.BLANK && getBottomCard().getType() != Card.Type.BLANK)) {
			errorLabel.setText("Please make sure the center\ncolumn is filled");
			return;
		} else {
			errorLabel.setText("");
		}
		System.out.println("Advancing " + advancingPlayer + "\'s placemat");
		((MainGameStage)Launcher.game_stage).players.get(advancingPlayer).shouldDrawCard = true;
		if (top.getType().equals(Card.Type.DELIVERY_SYSTEM)) {
			if (getCenterCard().getType().equals(Card.Type.WARHEAD)) {
				getTopCard().setCapacity(getTopCard().getCapacity() - getCenterCard().getWeight());
				if (getTopCard().getCapacity() >= 0) {
					getTopCard().resetLName();
					getCenterCard().play(((MainGameStage)this.getParent().getStage()));
					getTopCard().hasBeenOnTop = false;
					errorLabel.setText("");
					if (getTopCard().getCapacity() == 0) {
						getTopCard().discard(false, mgs, advancingPlayer);
						getTopCard().discard(false, mgs, advancingPlayer);
						if (!getTopCard().isConsumable())
							errorLabel.setText("Previous Delivery system was used\n\nfor only one warhead!\n\nThis is fine, but\n\ntry to underfill non-single-use\n\ndelivery systems");
					}
				} else {
					if (getTopCard().getCapacity() < 0) errorLabel.setText("The payload was too much for that\n\ndelivery system. Both cards have been\n\ndiscarded!");
					getTopCard().discard(false, mgs, advancingPlayer);
					getTopCard().discard(false, mgs, advancingPlayer);
					((MainGameStage)Launcher.game_stage).shouldSendData = true;
				}
			} else {
				getTopCard().discard(false, mgs, advancingPlayer);
				((MainGameStage)Launcher.game_stage).shouldSendData = true;
			}
		} else if (top.getType().equals(Card.Type.WARHEAD)) {
			top.discard(false, mgs, advancingPlayer);
			((MainGameStage)Launcher.game_stage).shouldSendData = true;
		} else if (top.getType().equals(Card.Type.PROPAGANDA)) {
			top.play((MainGameStage)this.getParent().getStage());
			System.out.println("ADVANCING");
			setTopCard(getCenterCard());
			setCenterCard(getBottomCard());
			setBottomCard(new Card(Card.BLANK));
			System.out.println("Played top, moved center to top, moved bottom to center, set bottom to blank");
			return;
		}
		
		if (getCenterCard().getType().equals(Card.Type.BLANK)) {
			if (!getBottomCard().equals(Card.BLANK)) {
				setCenterCard(getBottomCard());
				setBottomCard(new Card(Card.BLANK));
				System.out.println("moved bottom to center, set bottom to blank");
				return;
			}
		}
	}
	
	public void setHighlighting(boolean top, boolean center, boolean bottom) {
		if (highlightTop != top) {
			Rectangle box = new Rectangle(164, 224 - 148 - 63, 73, 63);
			for (int x = (int)box.getX(); x <= (int)box.getX() + box.getWidth(); x++) {
				for (int y = (int)box.getY() + (int)box.getHeight(); y >= (int)box.getY(); y--) {
					float r = 1;
					float g = top ? 0 : 1;
					float b = top ? 0 : 1;
					Color c = new Color();
					Color.rgba8888ToColor(c, mat.getPixel(x, y));
					int a = (int)(c.a); // never change the alpha value
					int pix = Color.rgba8888(r, g, b, a);
					mat.drawPixel(x, y, pix);
				}
			}
			if (highlightTop) System.out.println("Top " + this.top.getId());
			highlightTop = top;
		}
		
		if(highlightCenter != center) {
			Rectangle box = new Rectangle(173, 224 - 81 - 63, 61, 63);
			for (int x = (int)box.getX(); x <= (int)box.getX() + box.getWidth(); x++) {
				for (int y = (int)box.getY() + (int)box.getHeight(); y >= (int)box.getY(); y--) {
					float r = 1;
					float g = center ? 0 : 1;
					float b = center ? 0 : 1;
					Color c = new Color();
					Color.rgba8888ToColor(c, mat.getPixel(x, y));
					int a = (int)(c.a); // never change the alpha value
					int pix = Color.rgba8888(r, g, b, a);
					mat.drawPixel(x, y, pix);
				}
			}
			if (highlightCenter) System.out.println("Center " + this.center.getId());
			highlightCenter = center;
		}
		
		if (highlightBottom != bottom) {
			Rectangle box = new Rectangle(164, 224 - 14 - 63, 76, 63);
			for (int x = (int)box.getX(); x <= (int)box.getX() + box.getWidth(); x++) {
				for (int y = (int)box.getY() + (int)box.getHeight(); y >= (int)box.getY(); y--) {
					float r = 1;
					float g = bottom ? 0 : 1;
					float b = bottom ? 0 : 1;
					Color c = new Color();
					Color.rgba8888ToColor(c, mat.getPixel(x, y));
					int a = (int)(c.a); // never change the alpha value
					int pix = Color.rgba8888(r, g, b, a);
					mat.drawPixel(x, y, pix);
				}
			}
			if (highlightBottom) System.out.println("Bottom " + this.bottom.getId());
			highlightBottom = bottom;
		}
		
		tex.dispose();
		tex = new Texture(mat);
	}
}