
precision mediump float;

// The position of the light in eye space.
uniform vec3 uLightPos;       	
  
// Texture variables: depth texture
uniform sampler2D uShadowTexture;
// Second texture
uniform sampler2D uTexture;

uniform bool hasTexture;
  
// from vertex shader - values get interpolated
varying vec3 vPosition;
varying vec4 vColor;
varying vec3 vNormal;
varying vec2 vTexCoord;
  
// shadow coordinates
varying vec4 vShadowCoord;

// unpack colour to depth value
float unpack (vec4 colour)
{
    const vec4 bitShifts = vec4(1.0 / (256.0 * 256.0 * 256.0),
                                1.0 / (256.0 * 256.0),
                                1.0 / 256.0,
                                1);
    return dot(colour , bitShifts);
}

// returns 1.0 if not in shadow, 0.0 if in shadow
float getShadow()
{ 
	vec4 shadowMapPosition = vShadowCoord / vShadowCoord.w;
   		
	shadowMapPosition = (shadowMapPosition + 1.0) /2.0;
	
	vec4 packedZValue = texture2D(uShadowTexture, shadowMapPosition.st);

	float distanceFromLight = unpack(packedZValue);

	//add bias to reduce shadow acne (error margin)
	// default test = 0.0005
	float bias = 0.0005;

	//1.0 = not in shadow (fragmant is closer to light than the value stored in shadow map)
	//0.0 = in shadow
	return float(distanceFromLight > shadowMapPosition.z - bias);
}
  
void main()                    		
{        
	vec3 lightVec = uLightPos - vPosition;
	lightVec = normalize(lightVec);
   	
   	// Phong shading with diffuse and ambient component
	float diffuseComponent = max(0.0,dot(lightVec, vNormal) );
	float ambientComponent = 0.3;
 		
 	// if the fragment is not behind light view frustum, check if its in shadow
   	float shadow = (vShadowCoord.w > 0.0) ? getShadow() : 1.0;

	vec4 textureColor =
	(hasTexture) ? texture2D(uTexture,vTexCoord.st) : vec4(0.0,0.0,0.0,0.0);

    gl_FragColor = (vColor+textureColor) * max(ambientComponent,diffuseComponent*shadow);
}                                                                     	
