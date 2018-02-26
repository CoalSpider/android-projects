// Vertex shader to generate the Depth Map
precision highp float;

uniform mat4 uMVPMatrix;

// position of the vertices
attribute vec4 aShadowPosition; 

varying vec4 vPosition;

void main() {
	vPosition = uMVPMatrix * aShadowPosition;
	gl_Position = uMVPMatrix * aShadowPosition; 
}