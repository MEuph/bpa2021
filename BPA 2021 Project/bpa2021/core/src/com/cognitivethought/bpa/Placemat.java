package com.cognitivethought.bpa;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class Placemat extends Widget {

	Card left, right, top, bottom, center;
	Pixmap mat;

	public Placemat() {
		mat = new Pixmap(new FileHandle(Strings.URL_PLACEMAT));
	}

	public Card getLeft() {
		return left;
	}

	public void setLeft(Card left) {
		this.left = left;
	}

	public Card getRightCard() {
		return right;
	}

	public void setRight(Card right) {
		this.right = right;
	}

	public Card getTopCard() {
		return top;
	}

	public void setTop(Card top) {
		this.top = top;
	}

	public Card getBottom() {
		return bottom;
	}

	public void setBottom(Card bottom) {
		this.bottom = bottom;
	}

	public Card getCenter() {
		return center;
	}

	public void setCenter(Card center) {
		this.center = center;
	}

	public Vector2 pixelToRelative(float xPix, float yPix) {
		float retX = 0;
		float retY = 0;

		retX = (xPix * getWidth()) / mat.getWidth();
		retY = (yPix * getHeight()) / mat.getHeight();

		return new Vector2(retX, retY);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		batch.draw(new Texture(mat), getX(), getY(), getWidth(), getHeight());
		
		if (left != null)
			left.setPosition(pixelToRelative(40, 88).x, pixelToRelative(40, 88).y);
		if (top != null)
			top.setPosition(pixelToRelative(159, 24).x, pixelToRelative(159, 24).y);
		if (center != null)
			center.setPosition(pixelToRelative(159, 88).x, pixelToRelative(159, 88).y);
		if (bottom != null)
			bottom.setPosition(pixelToRelative(159, 152).x, pixelToRelative(159, 152).y);
		if (right != null)
			right.setPosition(pixelToRelative(278, 88).x, pixelToRelative(278, 88).y);

		if (left != null)
			left.draw(batch, parentAlpha);
		if (top != null)
			top.draw(batch, parentAlpha);
		if (center != null)
			center.draw(batch, parentAlpha);
		if (bottom != null)
			bottom.draw(batch, parentAlpha);
		if (right != null)
			right.draw(batch, parentAlpha);
	}

}