package graphicPipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import shader.Shader;
import shader.ShaderParameter;

public class MeshRenderer {

	private Shader shader;
	private Rasterizer rasterizer = new Rasterizer();

	public void renderMesh(Mesh m) {
		shader.setMesh(m);
		ShaderParameter[] vec = shader.processMesh();
		//for (int i = 0; i < m.indices.length; i = i + 3) {
		IntStream.range(0,m.getIndicesCount()/3).parallel().forEach(tri->{
			int i = tri *3;
			if(isOnScreen(vec[m.indices[i]], vec[m.indices[i + 1]], vec[m.indices[i + 2]]))
			rasterizer.rasterize(vec[m.indices[i]], vec[m.indices[i + 1]], vec[m.indices[i + 2]]);
		});
	}

	public void setTargetFbo(Fbo target) {
		rasterizer.setTargetFbo(target);
	}

	public void setShader(Shader shader) {
		this.shader = shader;
		rasterizer.setFragmentShader(shader);
	}

	private boolean isOnScreen(ShaderParameter... toTest){
		for(ShaderParameter p:toTest){
			for(float clip :p.ndc){
				if(Math.abs(clip)<1.0f)return true;
			}
		}
		return false;
	}

}
