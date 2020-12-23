package graphicPipeline;

import java.awt.image.BufferedImage;

import maths.Maths;

public class Texture {

	private int[] colors;
	int index;
	int width;
	int height;

	public Texture(BufferedImage image) {
		this.width = image.getWidth();
		this.height = image.getHeight();
		colors = new int[width * height];
		image.getRGB(0, 0, width, height, colors, 0, width);
	}

	public Texture(int index, int width, int height) {
		this.index = index;
		this.width = width;
		this.height = height;
	}

	public int getColorAtPos(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height) {
			return colors[x + y * width];
		} else {
			return 0;
		}
	}

	public int getColorAtUV(float u, float v) {
		int x = (int) (u * width);
		int y = (int) (v * height);
		return getColorAtPos(x, y);
	}

	public int getBilinearFiltertColorAtUV(float u, float v) {
		int x = (int) (u * width);
		int y = (int) (v * height);
		int x1 = (int) Math.floor(u * width);
		int y1 = (int) Math.floor(v * height);
		float xDiff = (float)Math.abs(Math.floor(u * width)-(u * width));
		float yDiff = (float)Math.abs(Math.floor(v * height)-(v * height));
		float[] c1 = colorToFloatArray(getColorAtPos(x, y));
		float[] c2 = colorToFloatArray(getColorAtPos(x1, y));
		float[] c3 = colorToFloatArray(getColorAtPos(x1, y1));
		float[] c4 = colorToFloatArray(getColorAtPos(x, y1));
		float[] b1 = Maths.mix(c1, c2, 1-xDiff);
		float[] b2 = Maths.mix(c3, c4, 1-xDiff);
		float[] rt = Maths.mix(b1, b2, 1-yDiff);
		return floatArrayToColor(rt);
	}

	public static float[] colorToFloatArray(int color) {
		float[] colour = new float[3];
		colour[0] = (color >> 16) & 0xff;
		colour[1] = (color >> 8) & 0xff;
		colour[2] = color & 0xff;
		return colour;
	}

	public static int floatArrayToColor(float[] rgb) {
		return ((Maths.clamp((int) rgb[0], 0, 255) << 16) | (Maths.clamp((int) rgb[1], 0, 255) << 8)
				| Maths.clamp((int) rgb[2], 0, 255));
	}

}
