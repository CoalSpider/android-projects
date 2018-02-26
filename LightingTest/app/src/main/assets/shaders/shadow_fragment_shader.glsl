
precision mediump float;

varying vec4 v_Position;

vec4 pack(float depth){
    const vec4 bitSh = vec4(16777216.0, // 256*256*256
							65536.0, // 256*256
							256.0,
							1.0);
	const vec4 bitMsk = vec4(0,1.0/256.0,1.0/256.0,1.0/256.0);
	vec4 comp = fract(depth * bitSh);
	comp -= comp.xxyz * bitMsk;
	return comp;
}

void main() {
    // the depth
	float normalizedDistance  = v_Position.z / v_Position.w;
	// scale -1.0;1.0 to 0.0;1.0
	normalizedDistance = (normalizedDistance + 1.0) / 2.0;

	// pack value into 32-bit RGBA texture
	gl_FragColor = pack(normalizedDistance);
}
/*
void main(){
	float depth = gl_FragCoord.z;
    gl_FragColor = vec4(depth,depth,depth,1.0);
}
*/