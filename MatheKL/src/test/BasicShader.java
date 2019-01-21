package test;

import graphicPipeline.Texture;
import maths.Maths;
import maths.Matrix;
import shader.FSParameter;
import shader.Shader;
import shader.ShaderParameter;

public class BasicShader extends Shader {

	@Override
	public ShaderParameter processVertex(float[] modelData, ShaderParameter input) {
		float[] pos4d = new float[] { modelData[0], modelData[1], modelData[2], 1f };
		float[] normal4d = new float[] { modelData[5], modelData[6], modelData[7], 0f };
		float[] model = Matrix.transformVektorWithMatrix(this.getVertexUniformData("transformation"), pos4d);
		float[] view = Matrix.transformVektorWithMatrix(this.getVertexUniformData("viewMatrix"), model);
		float[] clip = Matrix.transformVektorWithMatrix(this.getVertexUniformData("proj"), view);
		float[] transformedNormal = Matrix.transformVektorWithMatrix(this.getVertexUniformData("transformation"),
				normal4d);
		input.setViewSpacePos(view);
		input.clipCoords = clip;
		input.scaleToWindowCoords(Window.WIDTH, Window.HEIGHT);
		input.parameterList = new FSParameter[3];
		input.parameterList[0] = new FSParameter("uv", 2);
		input.parameterList[0].data[0] = modelData[3];
		input.parameterList[0].data[1] = modelData[4];
		input.parameterList[1] = new FSParameter("normal", 4);
		input.parameterList[1].data = transformedNormal;
		input.parameterList[2] = new FSParameter("worldPos", 3);
		input.parameterList[2].data[0] = model[0];
		input.parameterList[2].data[1] = model[1];
		input.parameterList[2].data[2] = model[2];

		return input;
	}

	@Override
	public int shadeFragment(ShaderParameter param) {
		float[] lightPos = this.getFragmentUniformData("lightPos");
		float[] camPos = this.getFragmentUniformData("camPos");
		float[] vecPos = param.getParameterByName("worldPos").data;
		float[] normal = param.getParameterByName("normal").data;
		//diffuse
		float[] toLightVec = Maths.vecSub(lightPos, vecPos, null);
		toLightVec = Maths.normalize(toLightVec);
		float diffuseFactor = Maths.clamp(Maths.dot(toLightVec, normal),0,1);
//specular
		float specularStrength = 1f;
		float[] viewDir = Maths.normalize(Maths.vecSub(camPos, vecPos, null));
		float[] reflectDir = Maths.reflect(toLightVec, normal);
		float specularFactor =(float) Math.pow(Math.max(Maths.dot(viewDir, reflectDir), 0.0), 32)*specularStrength;
		
//		texture
		float[] uv = param.getParameterByName("uv").data;
		int color = currentTexture.getBilinearFiltertColorAtUV(uv[0],uv[1]);
		float[] colorVec = Texture.colorToFloatArray(color);
//		combine
		colorVec = Maths.vecScale(diffuseFactor+specularFactor, colorVec);
		return Texture.floatArrayToColor(colorVec);
	}

}
