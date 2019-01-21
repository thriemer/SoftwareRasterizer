package graphicPipeline;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import maths.Maths;
import maths.Vec2;
import maths.Vec3;
import shader.Shader;
import shader.ShaderParameter;
import test.Window;

public class Rasterizer {

	private Fbo targetFbo;
	private Shader shader;
	private boolean cullBackFaces = true;
	private boolean useDepthBuffer = true;

	public void rasterize(ShaderParameter v1, ShaderParameter v2, ShaderParameter v3) {
		if (!cullBackFaces || !Triangle.isBackfacing(v1, v2, v3)) {
			fillTriangle(v1, v2, v3);
		}
	}

	private void fillTriangle(ShaderParameter v1, ShaderParameter v2, ShaderParameter v3) {
		Vec2 topleft = Triangle.findTopLeft(v1, v2, v3);
		Vec2 botright = Triangle.findBotRight(v1, v2, v3);

		int yMin = (int) Math.floor(Maths.clamp(topleft.y, 0, Window.HEIGHT));
		int yMax = (int) Math.ceil(Maths.clamp(botright.y, 0, Window.HEIGHT));
		int xMin = (int) Math.floor(Maths.clamp(topleft.x, 0, Window.WIDTH));
		int xMax = (int) Math.ceil(Maths.clamp(botright.x, 0, Window.WIDTH));
		for (int y = yMin; y <= yMax; y++) {
			for (int x = xMin; x <= xMax; x++) {
				Vec3 barycentric = calcBarycentricCoords(x, y, v1,v2,v3);
				if (!(barycentric.x < 0 || barycentric.y < 0 || barycentric.z < 0)) {
					ShaderParameter pxl = Triangle.getFSParameterWithBarycentricCoords(barycentric, v1, v2, v3);
					if (!useDepthBuffer || targetFbo.getDepthValueAt(x, y) < pxl.zValue) {
						targetFbo.putDepthIntoBuffer(x, y, pxl.zValue);
						int color = shader.shadeFragment(pxl);
						targetFbo.drawPointOnFbo(x, y, color);
					}

				}
			}
		}
	}

	public void setTargetFbo(Fbo targetFbo) {
		this.targetFbo = targetFbo;
	}

	private Vec3 barycentricRT = new Vec3(0, 0, 0);

	private Vec3 calcBarycentricCoords(float x, float y, ShaderParameter v1, ShaderParameter v2, ShaderParameter v3) {

		return calcBarycentricCoords(x, y, v1.windowPosition.x, v1.windowPosition.y, v2.windowPosition.x,
				v2.windowPosition.y, v3.windowPosition.x, v3.windowPosition.y);
	}

	private Vec3 calcBarycentricCoords(float x, float y, float tx1, float ty1, float tx2, float ty2, float tx3,
			float ty3) {
		float w1top = (ty2 - ty3) * (x - tx3) + (tx3 - tx2) * (y - ty3);
		float w1bottem = (ty2 - ty3) * (tx1 - tx3) + (tx3 - tx2) * (ty1 - ty3);
		barycentricRT.x = w1top / w1bottem;
		float w2top = (ty3 - ty1) * (x - tx3) + (tx1 - tx3) * (y - ty3);
		float w2bottem = (ty2 - ty3) * (tx1 - tx3) + (tx3 - tx2) * (ty1 - ty3);
		barycentricRT.y = w2top / w2bottem;
		barycentricRT.z = 1f - barycentricRT.x - barycentricRT.y;
		// [0] ist der oberste vertex, [1] der links im dreieck und [2] der rechts unten
		return barycentricRT;
	}

	public void setFragmentShader(Shader shader2) {
		this.shader = shader2;
	}

}
