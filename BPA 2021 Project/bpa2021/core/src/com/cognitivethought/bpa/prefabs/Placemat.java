package com.cognitivethought.bpa.prefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cognitivethought.bpa.external.GifDecoder;
import com.cognitivethought.bpa.gamestages.MainGameStage;
import com.cognitivethought.bpa.tidiness.Strings;

public class Placemat extends Actor {

	// 83 x 46 CARD SIZE

	public static float cardWidth = 33;
	public static float cardHeight = 49;
	public static float matWidth = 440, matHeight = 224;
	
	float downY, upY;
	
	Card left, right, bottom, top, center;
	Pixmap mat;
	Texture tex;
	Pixmap arrow;
	Texture aTex;
	public ImageButton clickArrow;
	Texture outline;
	Animation<TextureRegion> background;
	float elapsed;
	
	boolean isDown = false;
	
	boolean highlightTop = false,
			highlightCenter = false,
			highlightBottom = false;
	
	int alphaTop = 0, alphaCenter = 1, alphaBottom = 2;
	int[][][] alphaMap;
	
	public Placemat() {
		mat = new Pixmap(new FileHandle(Strings.URL_PLACEMAT_SPOTS));
		
		tex = new Texture(mat);
		
		// NOTE: GifDecoder written by Johannes Borchardt and converted to the latest LibGDX version by Anton Persson
		background = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, new FileHandle(Strings.URL_PLACEMAT_BACKGROUND).read());
		
		arrow = new Pixmap(new FileHandle(Strings.URL_PLACEMAT_ARROW));
		
		aTex = new Texture(arrow);
		Image i = new Image(aTex);
		clickArrow = new ImageButton(i.getDrawable());
		
		outline = new Texture(new Pixmap(new FileHandle(Strings.URL_PLACEMAT_OUTLINE)));
		
		clickArrow.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				isDown = !isDown;
			}
		});

		left = new Card(Card.BLANK);
		right = new Card(Card.BLANK);
		bottom = new Card(Card.BLANK);
		top = new Card(Card.BLANK);
		center = new Card(Card.BLANK);
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
		
		left.setPosition(getX() + pixelToRelative(64, 131).x, getY() + pixelToRelative(64, 131).y);
		left.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
		
		right.setPosition(getX() + pixelToRelative(305, 131).x, getY() + pixelToRelative(305, 131).y);
		right.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
		
		top.setPosition(getX() + pixelToRelative(185, 64).x, getY() + pixelToRelative(185, 64).y);
		top.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
		
		center.setPosition(getX() + pixelToRelative(185, 131).x, getY() + pixelToRelative(185, 131).y);
		center.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
		
		bottom.setPosition(getX() + pixelToRelative(185, 198).x, getY() + pixelToRelative(185, 198).y);
		bottom.setSize(cardWidth * (getWidth() / matWidth), cardHeight * (getHeight() / matHeight));
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
		super.draw(batch, parentAlpha);
		
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
		
		if (isDown && getY() >= downY + 2) {
			Vector2 newPos = new Vector2(getX(), getY()).
					lerp(new Vector2(0, downY), Gdx.graphics.getDeltaTime() * 5f);
			setPosition(getX(), newPos.y);
			clickArrow.setPosition(getX() + (getWidth() / 2) - (clickArrow.getWidth()), getY() - clickArrow.getHeight() + 5);
		} else if (isDown && getY() < downY + 2) {
			setPosition(getX(), downY);
			clickArrow.setPosition(getX() + (getWidth() / 2) - (clickArrow.getWidth()), getY() - clickArrow.getHeight() + 5);
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
		
		batch.draw(outline, getX(), getY(), getWidth(), getHeight());
	}

	public void advance(MainGameStage mgs, String advancingPlayer) {
		// TODO: Fix the executing of turns
		System.out.println("Advancing " + advancingPlayer + "\'s placemat");
		if (top.getType().equals(Card.Type.DELIVERY_SYSTEM)) {
			if (getCenterCard().getType().equals(Card.Type.WARHEAD)) {
				getCenterCard().play(((MainGameStage)this.getParent().getStage()));
			} else {
				getTopCard().discard(mgs, advancingPlayer);
			}
		} else if (top.getType().equals(Card.Type.WARHEAD)) {
			if (!getLeftCard().getType().equals(Card.Type.DELIVERY_SYSTEM) && !getRightCard().getType().equals(Card.Type.DELIVERY_SYSTEM)) {
				top.discard(mgs, advancingPlayer);
			} else {
				chooseDeliverySystem();
			}
		} else {
			if (!top.equals(Card.BLANK))
				top.play((MainGameStage)this.getParent().getStage());
			System.out.println("ADVANCING");
			setTopCard(getCenterCard());
			setCenterCard(getBottomCard());
			setBottomCard(Card.BLANK);
		}
		
		if (getCenterCard().equals(Card.BLANK)) {
			if (!getBottomCard().equals(Card.BLANK)) {
				setCenterCard(getBottomCard());
				setBottomCard(Card.BLANK);
			}
		}
		
		if (getTopCard().equals(Card.BLANK)) {
			if (!getCenterCard().equals(Card.BLANK)) {
				setTopCard(getCenterCard());
				setCenterCard(Card.BLANK);
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
		
		tex = new Texture(mat);
	}
	
	public void chooseDeliverySystem() {
		// TODO: Implement the choosing of a delivery system based on left or right card
	}
}