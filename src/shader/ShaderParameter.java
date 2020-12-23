package shader;

import maths.Vec2;
import maths.Vec3;

import java.util.HashMap;
import java.util.Map;

public class ShaderParameter {

    public Vec2 windowPosition = new Vec2(0, 0);
    public Vec3 viewSpacePosition = new Vec3(0, 0, 0);
    public float zValue;
    public float[] clipCoords;
    public float[] ndc = new float[3];
    public Map<String, float[]> namedParameters = new HashMap<>();

    public void scaleToWindowCoords(float width, float height) {
        ndc[0] = clipCoords[0] / clipCoords[3];
        ndc[1] = clipCoords[1] / clipCoords[3];
        ndc[2] = clipCoords[2] / clipCoords[3];
        windowPosition.x = ((ndc[0] + 1f) * width / 2f);
        windowPosition.y = ((ndc[1] + 1f) * height / 2f);
        zValue = ndc[2];
    }

    public void setViewSpacePos(float[] viewSpacePosVec) {
        viewSpacePosition.x = viewSpacePosVec[0];
        viewSpacePosition.y = viewSpacePosVec[1];
        viewSpacePosition.z = viewSpacePosVec[2];
    }

    public void setOutput(String name, float[] data) {
        namedParameters.put(name, data);
    }


    public void addUnderInfluenceOfBarycentricCoord(double coord, ShaderParameter target) {
        target.windowPosition.x += (float) (windowPosition.x * coord);
        target.windowPosition.y += (float) (windowPosition.y * coord);
        target.zValue += (float) (coord * zValue);
        for (String key : namedParameters.keySet()) {
            float[] ownData = getParameterByName(key);
            target.namedParameters.putIfAbsent(key,new float[ownData.length]);
            float[] targetData = target.getParameterByName(key);
            for (int j = 0; j < targetData.length; j++) {
                targetData[j] += ownData[j] * coord;
            }
        }
    }

    public float[] getParameterByName(String name) {
        return namedParameters.get(name);
    }

}
