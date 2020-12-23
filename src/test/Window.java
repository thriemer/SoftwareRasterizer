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
import shader.Shader;

public class Window extends KeyAdapter {

    private static JFrame frame;
    public static final int WIDTH = 1080, HEIGHT = 720;
    private static int switchInterval = 10;

    private static Fbo screenFbo = new Fbo(WIDTH, HEIGHT);
    private static MeshRenderer renderer = new MeshRenderer();
    static float transX = -2;
    static float transY = 6;
    static float transZ = -16;
    static float scale = 1;
    private static int fpsCounter = 0;
    private static long nextSecond = System.currentTimeMillis();
    private static int index = 0;
    private static BasicShader shader = new BasicShader();

    private static float lightRotation = 0;
    private static float lightRadius = 10;
    private static float lightPosX = 0;
    private static float lightPosY = 0;


    public Window() {
        frame = new JFrame();
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(this);
        // frame.setUndecorated(true);
        frame.setVisible(true);
        screenFbo.setClearColor(Color.BLUE);
    }

    private static Mesh[] meshes;
    private static Texture[] textures;
    private static int lastFps = 0;
    private static long lastSwitchTime = 0;

    public static void main(String[] args) {
        Window window = new Window();
        Mesh lightCube = Loader.loadOBJ("box");
        meshes = new Mesh[]{Loader.loadOBJ("IntergalacticSpaceship"), Loader.loadOBJ("dragon"), Loader.loadOBJ("lamp")};
        textures = new Texture[]{Loader.loadTexture("IntergalacticSpaceship"), Loader.loadTexture("orange"), Loader.loadTexture("lamp")};
        screenFbo.enableDepthBuffer();
        renderer.setTargetFbo(screenFbo);
        renderer.setShader(shader);
        float[] projMatrix = Matrix.createProjectionMatrix(90, 1f, 100, (float) WIDTH / (float) HEIGHT);
        shader.loadUniform("proj", projMatrix);
        float ry = 40;
        float[] transformationMatrix = new float[16];
        float[] viewMatrix = new float[16];
        float[] projView = new float[16];
        Matrix.multiply4x4Matrix(projMatrix, viewMatrix, projView);
        shader.setTexture(textures[index]);
        while (true) {
            ry += 5;
            ry = ry % 360;
            rotateLight();
            //render the model
            long renderStartTime = System.currentTimeMillis();
            float[] lightVec = new float[]{lightPosX, lightPosY, 0};
            shader.loadUniform("lightPos", lightVec);
            shader.loadUniform("transformation",
                    Matrix.createTransformationMatrix(0, 0, 0, 0, ry, 0, scale, transformationMatrix));
            shader.loadUniform("viewMatrix",
                    Matrix.createTransformationMatrix(-transX, -transY, -transZ, 0, 0, 0, 1, viewMatrix));
            shader.loadUniform("pv", projView);
            shader.loadUniform("camPos", new float[]{transX, transY, transZ});
            renderer.renderMesh(meshes[index]);
            shader.loadUniform("transformation", Matrix.createTransformationMatrix(lightVec[0], lightVec[1], lightVec[2], 0, ry, 0, 0.2f, transformationMatrix));
            renderer.renderMesh(lightCube);
            Graphics g = frame.getGraphics();
            g.clearRect(0, screenFbo.getHeight(), frame.getWidth(), frame.getHeight());
            screenFbo.renderFboOnScreen(g);
            screenFbo.clear();
            g.drawString("Pfeiltasten benutzen, um Kamera nach oben/unten, links/rechts zu bewegen", 100, screenFbo.getHeight() + 20);
            g.drawString("+/- benutzen, um Kamera nach vorne/hinten zu bewegen", 100, screenFbo.getHeight() + 40);
            g.drawString("N benutzen, um zum nächsten Modell zu schalten", 100, screenFbo.getHeight() + 60);
            g.drawString("FPS: " + lastFps, 100, screenFbo.getHeight() + 80);
            g.dispose();
            Toolkit.getDefaultToolkit().sync();
            countFrames();
            if (lastSwitchTime + switchInterval * 1000 < System.currentTimeMillis()) {
                nextModel();

            }
            //calculate wait time to get 30fps
            int waitTime = (int) Math.max(1000 / 30 - System.currentTimeMillis() + renderStartTime, 1);
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void rotateLight() {
        lightRotation += 5;
        lightPosX = (float) Math.cos(Math.toRadians(lightRotation)) * lightRadius;
        lightPosY = (float) Math.sin(Math.toRadians(lightRotation)) * lightRadius;
    }

    private static void countFrames() {
        fpsCounter++;
        if (System.currentTimeMillis() > nextSecond) {
            lastFps = fpsCounter;
            frame.setTitle("FPS: " + fpsCounter);
            fpsCounter = 0;
            nextSecond = System.currentTimeMillis() + 1000;
        }
    }

    @Override
    public void keyReleased(KeyEvent k) {
        if (k.getKeyCode() == KeyEvent.VK_N) {
            nextModel();
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

    private static void nextModel() {
        index++;
        index = index % meshes.length;
        shader.setTexture(textures[index]);
        lastSwitchTime = System.currentTimeMillis();
    }
}
