package graphicPipeline;

import java.util.ArrayList;
import java.util.List;

import maths.Vec2;
import shader.Shader;
import shader.ShaderParameter;

public class MeshRenderer {

	private Shader shader;
	private Rasterizer rasterizer = new Rasterizer();

	public void renderMesh(Mesh m, int cap) {
		shader.setMesh(m);
		ShaderParameter[] vec = shader.processMesh();
		float[] indiciesData = m.getData(m.indiciesIndex);
		List<IndiciesTriangle> it = new ArrayList<>();
		for (int i = 0; i < indiciesData.length; i = i + 3) {
			it.add(new IndiciesTriangle((int) indiciesData[i], (int) indiciesData[i + 1], (int) indiciesData[i + 2]));
		}
		it.forEach(item -> item.render(vec, rasterizer));
//		 it.parallelStream().forEach(item -> item.render(vec, rasterizer));

	}

	public void setTargetFbo(Fbo target) {
		rasterizer.setTargetFbo(target);
	}

	public void setShader(Shader shader) {
		this.shader = shader;
		rasterizer.setFragmentShader(shader);
	}

}

class IndiciesTriangle {
	public int[] indicies = new int[3];

	public IndiciesTriangle(int i1, int i2, int i3) {
		indicies[0] = i1;
		indicies[1] = i2;
		indicies[2] = i3;
	}

	public void render(ShaderParameter[] vec, Rasterizer r) {
		r.rasterize(vec[indicies[0]], vec[indicies[1]], vec[indicies[2]]);

	}

	public void render(ShaderParameter[] vec, Rasterizer r, int cap) {
		float centery = +vec[indicies[0]].windowPosition.y + vec[indicies[1]].windowPosition.y
				+ vec[indicies[2]].windowPosition.y;
		centery = centery / 3f;
		if (centery > cap) {
			render(vec, r);
		}
	}
}
