package maths;

public class Maths {
	
	public static float[] reflect(float[] vec, float[] normal) {
		float[] rt = new float[vec.length];
		float dot = dot(vec,normal);
		if(dot>0) {
			vecScale(-1f, vec);
		}
		dot = dot(vec,normal);
		for(int i=0; i<rt.length;i++) {
		rt[i]=vec[i]-2*dot*normal[i];
		}
		return rt;
	}
	
	public static float[] mix(float[] a, float[] b,float factor) {
		float[] nf = new float[a.length];
		for(int i=0; i<nf.length;i++) {
			nf[i]=a[i]*factor+b[i]*(1f-factor);
		}
		return nf;
	}

	public static int clamp(int x, int min, int max) {
		return x < min ? min : (Math.min(x, max));
	}

	public static float clamp(float val, float min, float max) {
		return val < min ? min : (Math.min(val, max));
	}

	public static float[] normalize(float[] inVec) {
		float length = 0;
		for (float v : inVec) {
			length += Math.pow(v, 2);
		}
		length = (float) Math.sqrt(length);
		for (int i = 0; i < inVec.length; i++) {
			inVec[i] = inVec[i] / length;
		}

		return inVec;
	}

	public static float[] vecScale(float f, float[] toScale) {
		for (int i = 0; i < toScale.length; i++) {
			toScale[i] = toScale[i] * f;
		}
		return toScale;
	}

	public static float[] vecAdd(float[] left, float[] right, float[] in) {
		if (in == null) {
			in = new float[left.length];
		}
		for (int i = 0; i < left.length; i++) {
			in[i] = left[i] + right[i];
		}
		return in;
	}

	public static float[] vecSub(float[] left, float[] right, float[] in) {
		if (in == null) {
			in = new float[left.length];
		}
		for (int i = 0; i < left.length; i++) {
			in[i] = left[i] - right[i];
		}
		return in;
	}

	public static float dot(float[] vec1, float[] vec2) {
		float rt = 0;
		for (int i = 0; i < vec1.length; i++) {
			rt += vec1[i] * vec2[i];
		}
		return rt;
	}
}
