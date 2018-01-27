
uniform mat4 u_LightMVP;

attribute vec3 a_Position;
varying vec4 v_Position;

void main() {
    v_Position = u_LightMVP * vec4(a_Position.xyz,1);
    gl_Position = u_LightMVP * vec4(a_Position.xyz,1);
}
