
uniform mat4 uMVPMatrix;
uniform mat4 uMVMatrix;
uniform mat4 uNormalMatrix;

// the shadow projection matrix
uniform mat4 uShadowProjMatrix;	

// position and normal of the vertices
attribute vec4 aPosition;
attribute vec4 aColor;
attribute vec3 aNormal;
attribute vec2 aTexCoord;

// to pass on
varying vec3 vPosition;
varying vec4 vColor;          		
varying vec3 vNormal;
varying vec4 vShadowCoord;
varying vec2 vTexCoord;


void main() {
	// the vertex position in camera space
	vPosition = vec3(uMVMatrix * aPosition);

	vColor = aColor;

	vTexCoord = aTexCoord;

	// the vertex normal coordinate in camera space
	vNormal = normalize(vec3(uNormalMatrix * vec4(normalize(aNormal),1.0)));

	vShadowCoord = uShadowProjMatrix * aPosition;
	
	gl_Position = uMVPMatrix * aPosition,1.0;
}