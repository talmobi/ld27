package com.heartpirates;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public final class Art {

	public final Bitmap[][] chars = getImage("art/charsMini.png", 7, 8);
	public final Bitmap[][] tiles = getImage("art/tiles2x2.png", 2, 2);

	public final BufferedImage roomImage = getBufferedImage("art/room.png");

	private BufferedImage getBufferedImage(String location) {
		URL url = this.getClass().getClassLoader().getResource(location);

		BufferedImage bimg = null;
		try {
			bimg = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn't load Art!");
			System.exit(0);
		}

		return bimg;
	}

	private Bitmap[][] getImage(String location, int w, int h) {

		URL url = this.getClass().getClassLoader().getResource(location);

		Bitmap[][] bm = null;
		BufferedImage bimg = null;
		try {
			bimg = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn't load Art!");
			System.exit(0);
		}

		int n = bimg.getWidth() / w;
		int m = bimg.getHeight() / h;

		bm = new Bitmap[n][m];

		for (int x = 0; x < n; x++) {
			for (int y = 0; y < m; y++) {
				int[] pixels = bimg.getRGB(x * w, y * h, w, h, null, 0, w);
				Bitmap bitmap = new Bitmap(pixels, w, h);
				bm[x][y] = bitmap;
			}
		}

		return bm;
	}
}