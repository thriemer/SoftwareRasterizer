package graphicPipeline;

import maths.Vec2;
import maths.Vec3;
import shader.ShaderParameter;

public class Triangle {

	public static boolean isBackfacing(ShaderParameter v1, ShaderParameter v2, ShaderParameter v3) {
		return Vec3.dot(Vec3.mul(v1.viewSpacePosition, -1f, null), new Vec3(v1.getParameterByName("normal"))) < 0;
	}


	public static Vec2 findTopLeft(ShaderParameter v1, ShaderParameter v2, ShaderParameter v3) {
		Vec2 topleft = new Vec2(0, 0);

		topleft.x = Math.min(v2.windowPosition.x, Math.min(v1.windowPosition.x, v3.windowPosition.x));
		topleft.y = Math.min(v2.windowPosition.y, Math.min(v1.windowPosition.y, v3.windowPosition.y));
		return topleft;
	}

	public static Vec2 findBotRight(ShaderParameter v1, ShaderParameter v2, ShaderParameter v3) {
		Vec2 botright = new Vec2(0, 0);
		botright.x = Math.max(v2.windowPosition.x, Math.max(v1.windowPosition.x, v3.windowPosition.x));
		botright.y = Math.max(v2.windowPosition.y, Math.max(v1.windowPosition.y, v3.windowPosition.y));
		return botright;
	}

	public static ShaderParameter getFSParameterWithBarycentricCoords(Vec3 barycentricCoords, ShaderParameter v1,
			ShaderParameter v2, ShaderParameter v3) {
		ShaderParameter interpolated = new ShaderParameter();
		v1.addUnderInfluenceOfBarycentricCoord(barycentricCoords.x, interpolated);
		v3.addUnderInfluenceOfBarycentricCoord(barycentricCoords.y, interpolated);
		v2.addUnderInfluenceOfBarycentricCoord(barycentricCoords.z, interpolated);
		return interpolated;
	}

}
