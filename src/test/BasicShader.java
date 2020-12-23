package test;

import graphicPipeline.Texture;
import maths.Maths;
import maths.Matrix;
import shader.Shader;
import shader.ShaderParameter;

import java.util.Map;

public class BasicShader extends Shader {

    public BasicShader() {
        super("pos", "uv", "normal");
    }

    @Override
    public ShaderParameter processVertex(Map<String, float[]> namedModelInputs, ShaderParameter target) {
        float[] model4d = swizzle(namedModelInputs.get("pos"), 1f);
        float[] normal4d = swizzle(namedModelInputs.get("normal"), 0f);
        float[] world = Matrix.transformVektorWithMatrix(this.getUniformData("transformation"), model4d);
        float[] view = Matrix.transformVektorWithMatrix(this.getUniformData("viewMatrix"), world);
        float[] clip = Matrix.transformVektorWithMatrix(this.getUniformData("proj"), view);
        float[] transformedNormal = Matrix.transformVektorWithMatrix(this.getUniformData("transformation"),
                normal4d);
        target.setViewSpacePos(view);
        target.clipCoords = clip;
        target.scaleToWindowCoords(Window.WIDTH, Window.HEIGHT);
        target.setOutput("uv", namedModelInputs.get("uv"));
        target.setOutput("normal", swizzle(transformedNormal, X, Y, Z));
        target.setOutput("worldPos", swizzle(world, X, Y, Z));
        return target;
    }

    @Override
    public int shadeFragment(ShaderParameter param) {
        float[] lightPos = this.getUniformData("lightPos");
        float[] camPos = this.getUniformData("camPos");
        float[] vecPos = param.getParameterByName("worldPos");
        float[] normal = param.getParameterByName("normal");
        //diffuse
        float[] toLightVec = Maths.vecSub(lightPos, vecPos, null);
        Maths.normalize(toLightVec);
        float diffuseFactor = Maths.clamp((Maths.dot(toLightVec, normal)+1f)/2f, 0, 1);
//specular
        float specularStrength = 1f;
        float[] viewDir = Maths.normalize(Maths.vecSub(camPos, vecPos, null));
        float[] reflectDir = Maths.reflect(toLightVec, normal);
        float specularFactor = 0;
        if (diffuseFactor > 0.5f) {
            specularFactor = (float) Math.pow(Math.max(Maths.dot(viewDir, reflectDir), 0.0), 32) * specularStrength;
        }
//		texture
        float[] uv = param.getParameterByName("uv");
        int color = currentTexture.getBilinearFiltertColorAtUV(uv[0], uv[1]);
        float[] colorVec = Texture.colorToFloatArray(color);
//		combine
        Maths.vecScale(diffuseFactor + specularFactor, colorVec);
        return Texture.floatArrayToColor(colorVec);
    }

}
