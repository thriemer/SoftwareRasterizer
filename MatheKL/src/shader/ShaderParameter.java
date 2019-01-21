package shader;

import maths.Vec2;
import maths.Vec3;

public class ShaderParameter {

	public Vec2 windowPosition = new Vec2(0, 0);
	public Vec3 viewSpacePosition = new Vec3(0, 0, 0);
	public float zValue;
	public float[] clipCoords;
	public float[] ndc = new float[3];
	public FSParameter[] parameterList;

	public void scaleToWindowCoords(float width, float height) {
		ndc[0] = clipCoords[0] / clipCoords[3];
		ndc[1] = clipCoords[1] / clipCoords[3];
		ndc[2] = clipCoords[2] / clipCoords[3];
		windowPosition.x = ((ndc[0]+1f) * width/2f);
		windowPosition.y = ((ndc[1]+1f)  * height/2f);
		zValue = ndc[2];
	}

	public void setViewSpacePos(float[] viewSpacePosVec) {
		viewSpacePosition.x = viewSpacePosVec[0];
		viewSpacePosition.y = viewSpacePosVec[1];
		viewSpacePosition.z = viewSpacePosVec[2];
	}

	public void reset() {
		windowPosition.x = 0;
		windowPosition.y = 0;
		zValue = 0;
	}

	public void addUnderInfluenceOfBarycentricCoord(double coord, ShaderParameter target) {
		target.windowPosition.x += (float) (windowPosition.x * coord);
		target.windowPosition.y += (float) (windowPosition.y * coord);
		target.zValue += (float) (coord * zValue);
		for (int i = 0; i < parameterList.length; i++) {
			FSParameter t = target.parameterList[i];
			for (int j = 0; j < t.data.length; j++) {
				t.data[j] += this.parameterList[i].data[j] * coord;
			}
		}
	}

	public FSParameter getParameterByName(String name) {
		for (int i = 0; i < parameterList.length; i++) {
			if (parameterList[i].name.equals(name)) {
				return parameterList[i];
			}
		}
		System.err.println("Name " + name + " wurde nicht gefunden");
		return null;
	}

	public void convertDataStructurTo(ShaderParameter mask) {
		reset();
		parameterList = new FSParameter[mask.parameterList.length];
		for (int i = 0; i < parameterList.length; i++) {
			parameterList[i] = new FSParameter(mask.parameterList[i].name, mask.parameterList[i].data.length);
		}
	}

}
