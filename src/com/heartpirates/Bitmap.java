package com.heartpirates;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Bitmap {

	int[] pixels;

	int w;
	int h;
	int x;
	int y;

	int transparentColor = 0;

	public Bitmap(BufferedImage bimg) {
		this.w = bimg.getWidth();
		this.h = bimg.getHeight();

		try {
			this.pixels = ((DataBufferInt) bimg.getRaster().getDataBuffer())
					.getData();
		} catch (ClassCastException cce) {
			cce.printStackTrace();
			this.pixels = bimg.getRGB(0, 0, w, h, null, 0, w);
		}
	}

	public Bitmap(int w, int h) {
		this.w = w;
		this.h = h;
		this.pixels = new int[w * h];
	}

	public Bitmap(int[] pixels, int w, int h) {
		this(w, h);
		this.pixels = pixels;
	}

	public void renderTo(Bitmap screen, int xoff, int yoff) {

		int startX = 0;
		int startY = 0;
		int maxW = w;
		int maxH = h;

		// Check out of bounds
		if (xoff < 0)
			startX = -xoff;
		if (yoff < 0)
			startY = -yoff;
		if (xoff + w > screen.w)
			maxW = w - (xoff + w - screen.w);
		if (yoff + h > screen.h)
			maxH = h - (yoff + h - screen.h);

		for (int i = startX; i < maxW; i++) {
			for (int j = startY; j < maxH; j++) {
				int source = i + j * w;
				int color = pixels[source];

				if ((color & 0xFF000000) == 0)
					continue;

				int target = (i + xoff) + (j + yoff) * screen.w;
				screen.pixels[target] = color;
			}
		}
	}

	public void flippedRenderTo(Bitmap screen, int xoff, int yoff,
			boolean xflip, boolean yflip) {

		int startX = 0;
		int startY = 0;
		int maxW = w;
		int maxH = h;

		// Check out of bounds
		if (xoff < 0)
			startX = -xoff;
		if (yoff < 0)
			startY = -yoff;
		if (xoff + w > screen.w)
			maxW = w - (xoff + w - screen.w);
		if (yoff + h > screen.h)
			maxH = h - (yoff + h - screen.h);

		if (!xflip) {
			for (int i = startX; i < maxW; i++) {
				for (int j = startY; j < maxH; j++) {
					int source = i + j * w;
					int color = pixels[source];

					if ((color & 0xFF000000) == 0)
						continue;

					int target = (i + xoff) + (j + yoff) * screen.w;
					screen.pixels[target] = color;
				}
			}
		} else {
			for (int i = startX; i < maxW; i++) {
				for (int j = startY; j < maxH; j++) {
					int source = (w - i - 1) + j * w;
					int color = pixels[source];

					if ((color & 0xFF000000) == 0)
						continue;

					int target = (i + xoff) + (j + yoff) * screen.w;
					screen.pixels[target] = color;
				}
			}
		}
	}

	public void flippedRenderToColorized(Bitmap screen, int xoff, int yoff,
			boolean xflip, boolean yflip, int shadeColor) {

		int startX = 0;
		int startY = 0;
		int maxW = w;
		int maxH = h;

		// Check out of bounds
		if (xoff < 0)
			startX = -xoff;
		if (yoff < 0)
			startY = -yoff;
		if (xoff + w > screen.w)
			maxW = w - (xoff + w - screen.w);
		if (yoff + h > screen.h)
			maxH = h - (yoff + h - screen.h);

		if (!xflip) {
			for (int i = startX; i < maxW; i++) {
				for (int j = startY; j < maxH; j++) {
					int source = i + j * w;
					int color = pixels[source];

					if ((color & 0xFF000000) == 0)
						continue;

					int target = (i + xoff) + (j + yoff) * screen.w;
					screen.pixels[target] = colorize(color, shadeColor);
				}
			}
		} else {
			for (int i = startX; i < maxW; i++) {
				for (int j = startY; j < maxH; j++) {
					int source = (w - i - 1) + j * w;
					int color = pixels[source];

					if ((color & 0xFF000000) == 0)
						continue;

					int target = (i + xoff) + (j + yoff) * screen.w;
					screen.pixels[target] = colorize(color, shadeColor);
				}
			}
		}

	}

	public int colorize(int color, int shadeColor) {
		int a = (color & 0xFF000000);

		int r1 = (color & 0xFF0000) >> 16;
		int g1 = (color & 0xFF00) >> 8;
		int b1 = (color & 0xFF) >> 0;

		int r2 = (shadeColor & 0xFF0000) >> 16;
		int g2 = (shadeColor & 0xFF00) >> 8;
		int b2 = (shadeColor & 0xFF) >> 0;

		int r = (r1 + r2);
		if ((r & 0xFFFFFF00) != 0)
			r = 255;
		int g = (g1 + g2);
		if ((g & 0xFFFFFF00) != 0)
			g = 255;
		int b = (b1 + b2);
		if ((b & 0xFFFFFF00) != 0)
			b = 255;

		return (a | (r << 16) | (g << 8) | b);
	}

	public void transformToGrayScale() {
		// fast semi-accurate gray scale conversion

		for (int i = 0; i < this.pixels.length; i++) {
			int pixel = this.pixels[i];
			int r = (pixel & 0xFF0000) >> 16;
			int g = (pixel & 0xFF00) >> 8;
			int b = (pixel & 0xFF) >> 0;
			int gray = (r + g + b + 255) >> 2;
			gray = (gray << 16 | gray << 8 | gray);
			this.pixels[i] = gray;
		}
	}

	public void transformTimeStopEffect() {
		// fast semi-accurate gray scale conversion

		for (int i = 0; i < this.pixels.length; i++) {
			int rand = 200;
			rand += (i % 77) + (System.currentTimeMillis() % (333 | (i & 0x050505)));
			int pixel = this.pixels[i];
			int r = (pixel & 0xFF0000) >> 16;
			int g = (pixel & 0xFF00) >> 8;
			int b = (pixel & 0xFF) >> 0;
			int gray = (r + g + b + rand) >> 2;
			gray = (gray << 16 | gray << 8 | gray);
			this.pixels[i] = gray;
		}
	}
	
	public void transformTimeStopEffect(int factor) {
		// fast semi-accurate gray scale conversion
		
		if (factor <= 0)
			factor = (-factor) + 50;
		
		for (int i = 0; i < this.pixels.length; i++) {
			int rand = 200;
			rand += (i % 77) + (System.currentTimeMillis() % (333 | (i & 0x050505)));
			rand = rand % factor;
			int pixel = this.pixels[i];
			int r = (pixel & 0xFF0000) >> 16;
		int g = (pixel & 0xFF00) >> 8;
			int b = (pixel & 0xFF) >> 0;
			int gray = (r + g + b + rand) >> 2;
			gray = (gray << 16 | gray << 8 | gray);
			this.pixels[i] = gray;
		}
	}
}