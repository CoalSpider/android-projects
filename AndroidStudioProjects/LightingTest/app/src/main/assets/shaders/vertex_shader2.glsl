
uniform mat4 u_MVP;
uniform mat4 u_MV;
uniform mat4 u_DepthMVP;

attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec3 a_Normal;

varying vec4 v_Color;
varying vec3 v_Normal;
varying vec4 v_ShadowCoord;

void main() {
    const mat4 biasMat = mat4(0.5,0.0,0.0,0.0,
                              0.0,0.5,0.0,0.0,
                              0.0,0.0,1.0,0.0,
                              0.5,0.5,0.5,1.0);
    mat4 depthBiasMPV = biasMat * u_DepthMVP;

    v_ShadowCoord = depthBiasMPV * a_Position;

    v_Color = a_Color;
    v_Normal = a_Normal;
    mat4 mvCopy = u_MV;

    gl_Position = u_MVP * a_Position;
}