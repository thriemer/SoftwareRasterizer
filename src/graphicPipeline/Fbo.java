package graphicPipeline;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class Fbo {

	private BufferedImage fboImage;
	private int[] pixels;
	private float[] depth;
	private Graphics2D g2d;
	private int width;
	private int height;

	public Fbo(int width, int height) {
		fboImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g2d = fboImage.createGraphics();
		g2d.setBackground(Color.BLACK);
		pixels = ((DataBufferInt) fboImage.getRaster().getDataBuffer()).getData();
		this.width = width;
		this.height = height;
	}

	public BufferedImage getFbo() {
		return fboImage;
	}

	public void drawPointOnFbo(int x, int y, int red, int green, int blue) {
		if (x > -1 && x < getWidth() && y > -1 && y < getHeight()) {
			int pos = x + y * fboImage.getWidth();
			if (pos >= 0 && pos < pixels.length) {
				pixels[pos] = (red << 16) | (green << 8) | blue;
			}
		}
	}

	public void drawPointOnFbo(int x, int y, int color) {
		if (x > -1 && x < getWidth() && y > -1 && y < getHeight()) {
			int pos = x + y * fboImage.getWidth();
			if (pos >= 0 && pos < pixels.length) {
				pixels[pos] = color;
			}
		}
	}

	public void setClearColor(Color c) {
		g2d.setBackground(c);
	}

	public void clear() {
		Arrays.fill(depth, -Float.MAX_VALUE);
		Arrays.fill(pixels, Color.white.getRGB());
	}

	public void overwriteColorWithDepth() {
		for (int i = 0; i < depth.length; i++) {
			if (depth[i] != -Float.MAX_VALUE) {
				pixels[i] = (int) depth[i];
			}
		}

	}

	public void renderFboOnScreen(Graphics g) {
		g.drawImage(fboImage, 0, 0, null);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void enableDepthBuffer() {
		depth = new float[pixels.length];
		clear();
	}

	public void putDepthIntoBuffer(int x, int y, float depth) {
		if (x > -1 && x < getWidth() && y > -1 && y < getHeight()) {
			int pos = x + y * getWidth();
			if (pos >= 0 && pos < pixels.length) {
				this.depth[pos] = depth;
			}
		}
	}

	public float getDepthValueAt(int x, int y) {
		if (x > -1 && x < getWidth() && y > -1 && y < getHeight()) {
			int pos = x + y * getWidth();
			if (pos >= 0 && pos < pixels.length) {
				return this.depth[pos];
			}
		}
		return -Float.MAX_VALUE;
	}
}
