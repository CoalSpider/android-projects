// fragment shader to generate the Depth Map
precision highp float;

varying vec4 vPosition; 


vec4 pack(float depth)
{
	const vec4 bitSh = vec4(256.0 * 256.0 * 256.0,
							256.0 * 256.0,
							256.0,
							1.0);
	const vec4 bitMsk = vec4(0,
							 1.0 / 256.0,
							 1.0 / 256.0,
							 1.0 / 256.0);
	vec4 comp = fract(depth * bitSh);
	comp -= comp.xxyz * bitMsk;
	return comp;
}

void main() {
    // Modelspace (think blender)
    //      multiply by view matrix (glLookAt / glOrtho)
    // Camera/Eye/View Space
    //      multiply by projection matrix (glFustrum)
    // Clip Space
    //      / by w compoent (x/w, y/w, z/w) this is called perspective divide
    // Normaized Device Coord Space (NDC) range is -1 to 1
    //      shift to range 0 1
    // Screen Space

	// the depth (vPosition is in clip space we want to get NDC space by diving by w)
	float normalizedDistance  = vPosition.z / vPosition.w;
	// scale -1.0;1.0 to 0.0;1.0  (convert to screen space)
	normalizedDistance = (normalizedDistance + 1.0) / 2.0;

	// pack value into 32-bit RGBA texture
	gl_FragColor = pack(normalizedDistance);
}