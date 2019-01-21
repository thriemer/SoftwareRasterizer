package test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;

import graphicPipeline.Fbo;
import graphicPipeline.Mesh;
import graphicPipeline.MeshRenderer;
import graphicPipeline.Texture;
import maths.Matrix;

public class Window extends KeyAdapter {

	private static JFrame frame;
	public static final int WIDTH = 1080, HEIGHT = 720;

	private static Fbo screenFbo = new Fbo(WIDTH, HEIGHT);
	private static MeshRenderer renderer = new MeshRenderer();
	static float transX = 0;
	static float transY =0;
	static float transZ =0;
	static float scale = 1;
	private static int fpsCounter = 0;
	private static long nextSecond = System.currentTimeMillis();

	public Window() {
		frame = new JFrame();
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(this);
		// frame.setUndecorated(true);
		frame.setVisible(true);
		 screenFbo.setClearColor(Color.BLUE);
	}

	public static void main(String args[]) {
		Window window = new Window();
		Graphics g = frame.getGraphics();
		Mesh m1 = Loader.loadOBJ("dragon");
		screenFbo.enableDepthBuffer();
		renderer.setTargetFbo(screenFbo);
		BasicShader shader = new BasicShader();
		renderer.setShader(shader);
		float[] projMatrix = Matrix.createProjectionMatrix(90, 0.01f, 1000, (float) WIDTH / (float) HEIGHT);
		shader.loadVertexUniform("proj", projMatrix);
		Texture boxTexture = Loader.loadTexture("orange");
		shader.setTexture(boxTexture);
		float ry = 40;
		float[] transformationMatrix = new float[16];
		float[] viewMatrix = new float[16];
		float[] projView = new float[16];
		Matrix.multiply4x4Matrix(projMatrix, viewMatrix, projView);
		shader.loadFragmentUniform("lightPos", new float[] {0,100,30});
		int cap = HEIGHT;
		while (true) {
			cap-=50;
			cap = cap<0?HEIGHT:cap;
			ry+=5;
			ry = ry % 360;
			shader.loadVertexUniform("transformation",
					Matrix.createTransformationMatrix(0, 0, 10, 0, ry, 0, scale, transformationMatrix));
			shader.loadVertexUniform("viewMatrix",
					Matrix.createTransformationMatrix(-transX, -transY, -transZ, 0, 0, 0, 1, viewMatrix));
			shader.loadVertexUniform("pv", projView);
			shader.loadFragmentUniform("camPos", new float[] {transX, transY, transZ});
			renderer.renderMesh(m1,cap);
			shader.loadVertexUniform("transformation",
					Matrix.createTransformationMatrix(15, 0, 65, ry, ry, ry, scale, transformationMatrix));
//			renderer.renderMesh(m1);
//			screenFbo.overwriteColorWithDepth();
			screenFbo.renderFboOnScreen(g);
			screenFbo.clear();
			Toolkit.getDefaultToolkit().sync();
			countFrames();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void countFrames() {
		fpsCounter++;
		if (System.currentTimeMillis() > nextSecond) {
			frame.setTitle("FPS: " + fpsCounter);
			fpsCounter = 0;
			nextSecond = System.currentTimeMillis() + 1000;
		}
	}

	@Override
	public void keyPressed(KeyEvent k) {
		int faktor = 2;
		if (k.getKeyCode() == KeyEvent.VK_LEFT) {
			transX -= faktor;
		}
		if (k.getKeyCode() == KeyEvent.VK_RIGHT) {
			transX += faktor;
		}
		if (k.getKeyCode() == KeyEvent.VK_UP) {
			transY -= faktor;
		}
		if (k.getKeyCode() == KeyEvent.VK_DOWN) {
			transY += faktor;
		}
		if (k.getKeyCode() == KeyEvent.VK_PLUS) {
			transZ += faktor;
		}
		if (k.getKeyCode() == KeyEvent.VK_MINUS) {
			transZ -= faktor;
		}
	}

	private static Mesh createMesh() {
		Mesh m = new Mesh();
		m.storeDataInAttribute(0, 3, new float[] { 0, 0, 0, 0, 100, 0, 100, 100, 0, 100, 0, 0, 0, 0, 100, 0, 100, 100,
				100, 100, 100, 100, 0, 100 });
		m.storeDataInAttribute(1, 2, new float[] { 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0 });
		m.storeDataInAttribute(2, 0, new float[] {});
		m.storeIndiecies(3, new float[] { 3, 4, 7, 0, 1, 2, 0, 3, 2, 4, 5, 6, 4, 7, 6, 0, 4, 1, 1, 4, 5, 3, 2, 6, 3, 6,
				7, 1, 2, 5, 5, 2, 6, 0, 4, 3 });
		return m;
	}
}
