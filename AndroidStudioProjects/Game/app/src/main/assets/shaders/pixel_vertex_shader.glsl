
uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
uniform vec3 u_LightPos;

attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec3 a_Normal;
attribute vec2 a_TexCoordinate;

varying vec3 v_Position;
varying vec4 v_Color;
varying vec3 v_Normal;
varying vec2 v_TexCoordinate;

void main()
{
    // transform vertex into eye space
    v_Position = vec3(u_MVMatrix * a_Position);
    // pass through color
    v_Color = a_Color;
    // pass through texture coordinate
    v_TexCoordinate = a_TexCoordinate;
    // transform normal to eye space
    v_Normal = vec3(u_MVMatrix * vec4(a_Normal,0.0));
	// gl_position is a special variable used to store the final positon
	gl_Position = u_MVPMatrix * a_Position;
}