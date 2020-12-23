package shader;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import graphicPipeline.Mesh;
import graphicPipeline.Texture;

public abstract class Shader {

    public Mesh currentMesh;
    public Texture currentTexture;
    private String[] inputNames;
    private ShaderParameter[] rtForProcessMesh;
    private Map<String, float[]> uniforms = new HashMap<>();

    public Shader(String... inputNames) {
        this.inputNames = inputNames;
    }

    public ShaderParameter[] processMesh() {
        IntStream.range(0,currentMesh.getVectorCount()).parallel().forEach(i->{
      //  for (int i = 0; i < currentMesh.getVectorCount(); i++) {
            //create input map
            Map<String, float[]> nameDataMap = new HashMap<>();
            for (int j = 0; j < currentMesh.getAttributeCount(); j++) {
                nameDataMap.put(inputNames[j], currentMesh.getVector(j, i, null));
            }
            //execute vertex shader stage
            rtForProcessMesh[i] = processVertex(nameDataMap, rtForProcessMesh[i]);
        });
        return rtForProcessMesh;
    }

    public abstract ShaderParameter processVertex(Map<String, float[]> namedModelInputs, ShaderParameter target);

    public void setMesh(Mesh mesh) {
        currentMesh = mesh;
        rtForProcessMesh = new ShaderParameter[mesh.getVectorCount()];
        for (int i = 0; i < rtForProcessMesh.length; i++) {
            rtForProcessMesh[i] = new ShaderParameter();
        }
    }

    public abstract int shadeFragment(ShaderParameter param);

    public static int convertColorToInt(int red, int green, int blue) {
        return (red << 16) | (green << 8) | blue;
    }

    public float[] getUniformData(String name) {
        return uniforms.get(name);
    }

    public void loadUniform(String name, float[] uniform) {
        uniforms.put(name, uniform);
    }

    public void setTexture(Texture texture) {
        this.currentTexture = texture;
    }

    public static float[] swizzle(float[] in, float... anhang){
        float[] rt =new float[in.length+anhang.length];
        System.arraycopy(in,0,rt,0,in.length);
        System.arraycopy(anhang,0,rt,in.length,anhang.length);
        return rt;
    }

    public final static int X=0,R=0;
    public final static int Y=1,G=1;
    public final static int Z=2,B=2;
    public final static int W=3,A=3;

    public static float[] swizzle(float[] in, int... indexes){
        float[] rt =new float[indexes.length];
        for(int i=0;i<indexes.length;i++){
            rt[i]=in[indexes[i]];
        }
        return rt;
    }
}
