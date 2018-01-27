
uniform sampler2D u_ShadowMap;

varying vec4 v_Color;
varying vec4 v_Normal;
varying vec4 v_ShadowCoord;

void main() {
// for spot light
// float depth = texture(shadowMap,shadowCoord.xy/ShadowCoord.w).z <
// ShadowCoord.z-bias/ShadowCoord.w
    float depth = texture2DProj(u_ShadowMap,v_ShadowCoord.xyzw).z;
    float visibility = 1.0;
    float bias = 0.005;
    if(depth < (v_ShadowCoord.z - bias)){
        visibility = 0.5;
    }

    gl_FragColor = vec4(v_Color.rgb * visibility, 1.0);

}
