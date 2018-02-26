
precision mediump float;

// The position of the light in eye space.
uniform vec3 uLightPos;       	
  
// Texture variables: depth texture
uniform sampler2D uShadowTexture;
  
// from vertex shader - values get interpolated
varying vec3 vPosition;
varying vec4 vColor;
varying vec3 vNormal;
  
// shadow coordinates
varying vec4 vShadowCoord;

//Simple shadow mapping
float shadowSimple() 
{ 
	vec4 shadowMapPosition = vShadowCoord / vShadowCoord.w;
	
	float distanceFromLight = texture2D(uShadowTexture, shadowMapPosition.st).z;

	//add bias to reduce shadow acne (error margin)
	// ERROR: peter panning with anything over zero
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
 		
 	// Shadow
   	float shadow = 1.0;

	//if the fragment is not behind light view frustum
	if (vShadowCoord.w > 0.0) {
				
		shadow = shadowSimple();
			
		//scale 0.0-1.0 to 0.2-1.0
		//otherways everything in shadow would be black
		shadow = (shadow * 0.8) + 0.2;
	}

	// Final output color with shadow and lighting
    gl_FragColor = (vColor * (diffuseComponent + ambientComponent) * shadow);                                  		
}                                                                     	
