package shader;

import java.util.HashMap;
import java.util.Map;

import graphicPipeline.Mesh;
import graphicPipeline.Texture;

public abstract class Shader {

	public Mesh currentMesh;
	public Texture currentTexture;
	private ShaderParameter[] rtForProcessMesh;
	private float[] vertexData;
	private Map<String, float[]> vertexUniforms = new HashMap<>();
	private Map<String, float[]> fragmentUniforms = new HashMap<>();

	public ShaderParameter[] processMesh() {
		for (int i = 0; i < currentMesh.getData(currentMesh.indiciesIndex).length; i++) {
			int currentVertexPosition = (int) currentMesh.getData(currentMesh.indiciesIndex)[i];
			int vertexDataCounter = 0;
			for (int j = 0; j < currentMesh.getAttributeCountWithoutIndicies(); j++) {
				int attributeSize = currentMesh.getAttributeSizeOfId(j);
				for (int k = 0; k < attributeSize; k++) {
					vertexData[vertexDataCounter++] = currentMesh.getData(j)[currentVertexPosition * attributeSize + k];
				}
			}
			rtForProcessMesh[currentVertexPosition] = processVertex(vertexData,
					rtForProcessMesh[currentVertexPosition]);
		}
		return rtForProcessMesh;
	}

	public abstract ShaderParameter processVertex(float[] data, ShaderParameter input);

	public void setMesh(Mesh mesh) {
		currentMesh = mesh;
		rtForProcessMesh = new ShaderParameter[mesh.getData(mesh.indiciesIndex).length];
		for (int i = 0; i < mesh.getData(mesh.indiciesIndex).length; i++) {
			rtForProcessMesh[i] = new ShaderParameter();
		}
		int requieredVertexDataLength = 0;
		for (int i = 0; i < mesh.getAttributeCountWithoutIndicies(); i++) {
			requieredVertexDataLength += mesh.getAttributeSizeOfId(i);
		}
		vertexData = new float[requieredVertexDataLength];
	}

	public abstract int shadeFragment(ShaderParameter param);

	public static int convertColorToInt(int red, int green, int blue) {
		return (red << 16) | (green << 8) | blue;
	}



	public float[] getVertexUniformData(String name) {
		return vertexUniforms.get(name);
	}

	public void loadVertexUniform(String name, float[] uniform) {
		if(!vertexUniforms.containsKey(name)){
		vertexUniforms.put(name, uniform);
		}else{
			vertexUniforms.remove(name);
			vertexUniforms.put(name, uniform);
		}
	}

	public float[] getFragmentUniformData(String name) {
		return fragmentUniforms.get(name);
	}

	public void loadFragmentUniform(String name, float[] uniform) {
		fragmentUniforms.put(name, uniform);
	}
	
	public void setTexture(Texture texture) {
		this.currentTexture=texture;
	}
	
}
