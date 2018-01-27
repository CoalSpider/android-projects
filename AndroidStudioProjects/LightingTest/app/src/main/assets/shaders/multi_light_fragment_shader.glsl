
precision mediump float;

// base data
varying vec4 v_Color;
varying vec3 v_Normal;
// light data
varying vec4 v_EyeSpacePos;

// shadow stuff
varying vec4 shadowCoord;
uniform sampler2D u_ShadowMap;

struct DirLightInfo
{
    // base light info
    vec3 lightPos;
    vec4 diffuseColor;
    vec4 ambientColor;
    vec4 specularColor;
};

struct PointLightInfo
{
    // base light info
    vec3 lightPos;
    vec4 diffuseColor;
    vec4 ambientColor;
    vec4 specularColor;
    // point light info
    float constantAttenuation;
    float linearAttenuation;
    float quadraticAttenuation;
};

struct SpotLightInfo
{
    // base light info
    vec3 lightPos;
    vec4 diffuseColor;
    vec4 ambientColor;
    vec4 specularColor;
    // point light info
    float constantAttenuation;
    float linearAttenuation;
    float quadraticAttenuation;
    // spot light info
    vec3 spotDirection;
    float spotCosCutoff;
    float spotExponent;
};

const int NUM_LIGHTS = 3;

uniform DirLightInfo dirLights[NUM_LIGHTS];
uniform PointLightInfo pointLights[NUM_LIGHTS];
uniform SpotLightInfo spotLights[NUM_LIGHTS];

float calculateSpecularBlinnPhong(vec3 lightDir,vec3 normal, float shininess){
    vec3 viewDir = normalize(-v_EyeSpacePos.xyz);
    vec3 halfDir = normalize(lightDir+viewDir);
    float specAngle = max(dot(halfDir,normal),0.0);
    return pow(specAngle,shininess);
}

/** TODO: pass in material parameters **/
/* returns the base light color in linear space (no gamma correction) */
vec4 computeBaseLightColor(vec3 lightPos,
    vec4 lightAmbientColor, vec4 lightDiffuseColor, vec4 lightSpecularColor)
{
    vec3 normal = normalize(v_Normal);
    // direction of light to surface
    vec3 lightDir = normalize(lightPos - v_EyeSpacePos.xyz);
    float lambertian = max(dot(normal,lightDir),0.0);
    float specular = 0.0;
    // if vertex is lit compute the specular term
    if(lambertian > 0.0){
        specular = calculateSpecularBlinnPhong(lightDir,normal,32.0);

    }
    vec3 ambientColor = lightAmbientColor.xyz;
    vec3 diffuseColor = lightDiffuseColor.xyz*lambertian;
    vec3 specularColor = lightSpecularColor.xyz*specular;
    return vec4(ambientColor+diffuseColor+specularColor,1.0);
}

float computeAttenuation(float dist, float numerator,
    float constAtten, float linearAtten, float quadAtten)
{
    return numerator / (constAtten + linearAtten*dist + quadAtten*dist*dist);
}

/** calculates the color of a fragment lit by the given directional light **/
vec4 computeFragmentDirectionalLighting(DirLightInfo dirLight)
{
    return computeBaseLightColor(
               dirLight.lightPos,
               dirLight.ambientColor,
               dirLight.diffuseColor,
               dirLight.specularColor
           );
}

/** calculates the color of a fragment lit by the given point light **/
vec4 computeFragmentPointLighting(PointLightInfo pointLight)
{
    vec4 color = computeBaseLightColor(
                    pointLight.lightPos,
                    pointLight.ambientColor,
                    pointLight.diffuseColor,
                    pointLight.specularColor
                 );
    // direction from light to fragment
    vec3 lightDir = vec3(pointLight.lightPos - v_EyeSpacePos.xyz);
    // distance from light to fragment
    float dist = length(lightDir);
    float attenuation = computeAttenuation(
                            dist,
                            1.0,
                            pointLight.constantAttenuation,
                            pointLight.linearAttenuation,
                            pointLight.quadraticAttenuation
                         );
    color = vec4((color*attenuation).xyz,1.0);
    return color;
}

float computeSpotEffect(vec3 spotDirection, vec3 lightDir)
{
    return dot(normalize(-spotDirection),normalize(lightDir));
}

/** calculates the color of a fragment lit by the given spotlight **/
vec4 computeFragmentSpotLighting(SpotLightInfo spotLight)
{
    // set base color to 0, no ambience outside the spot effect
    vec4 color = vec4(0.0,0.0,0.0,0.0);
    // direction from light to fragment
    vec3 lightDir = vec3(spotLight.lightPos - v_EyeSpacePos.xyz);
    float spotEffect = computeSpotEffect(spotLight.spotDirection,lightDir);
    // check if fragment is in spot light
    if(spotEffect > spotLight.spotCosCutoff)
    {
        spotEffect = pow(spotEffect,spotLight.spotExponent);
        color = computeBaseLightColor(
                    spotLight.lightPos,
                    spotLight.ambientColor,
                    spotLight.diffuseColor,
                    spotLight.specularColor
                );
        // distance from light to fragment
        float dist = length(lightDir);
        float attenuation = computeAttenuation(
                                dist,
                                spotEffect,
                                spotLight.constantAttenuation,
                                spotLight.linearAttenuation,
                                spotLight.quadraticAttenuation
                             );
        color *= vec4((color*attenuation).xyz,1.0);
    };
    return color;
}

bool dirLightExists(DirLightInfo dirLight)
{
    bool nullLight = dirLight.ambientColor.w != 0.0 ||
                     dirLight.diffuseColor.w != 0.0 ||
                     dirLight.specularColor.w != 0.0;
    return nullLight;
}

bool pointLightExists(PointLightInfo pointLight)
{
    bool nullLight = pointLight.constantAttenuation != 0.0 ||
                     pointLight.linearAttenuation != 0.0 ||
                     pointLight.quadraticAttenuation != 0.0;
    return nullLight;
}

bool spotLightExists(SpotLightInfo spotLight)
{
    return
        spotLight.spotCosCutoff != 0.0 ||
        spotLight.spotExponent != 0.0;
}

vec4 getDirLightColor(int index)
{
    DirLightInfo dirLight = dirLights[index];
    vec4 color = vec4(0.0,0.0,0.0,0.0);
    if(dirLightExists(dirLight)){
        color += computeFragmentDirectionalLighting(dirLight);
    }
    return color;
}

/** returns the color of a fragment lit by the given pointLight.
    if the pointLight is uninitalized (it wasnt passed in from the program)
    a blank color of vec4(0.0,0.0,0.0,0.0) is returned **/
vec4 getPointLightColor(int index)
{
    PointLightInfo pointLight = pointLights[index];
    vec4 color = vec4(0.0,0.0,0.0,0.0);
    if(pointLightExists(pointLight)){
        color += computeFragmentPointLighting(pointLight);
    }
    return color;
}

/** returns the color of a fragment lit by the given spotlight.
    if the spotLight is uninitalized (it wasnt passed in from the program)
    a blank color of vec4(0.0,0.0,0.0,0.0) is returned **/
vec4 getSpotLightColor(int index)
{
    SpotLightInfo spotLight = spotLights[index];
    vec4 color = vec4(0.0,0.0,0.0,0.0);
    if(spotLightExists(spotLight)){
        color += computeFragmentSpotLighting(spotLight);
    }
    return color;
}

// unpack colour to depth value
float unpack (vec4 colour)
{
    const vec4 bitShifts = vec4(1.0 / (256.0 * 256.0 * 256.0),
                                1.0 / (256.0 * 256.0),
                                1.0 / 256.0,
                                1.0);
    return dot(colour , bitShifts);
}
/*
float shadowSimple(){
    vec4 shadowMapPosition = shadowCoord;

	shadowMapPosition = (shadowMapPosition + 1.0) /2.0;

	vec4 packedZValue = texture2D(u_ShadowMap, shadowMapPosition.st);

	float distanceFromLight = unpack(packedZValue);

	//add bias to reduce shadow acne (error margin)
	float bias = 0.0005;

	//1.0 = not in shadow (fragmant is closer to light than the value stored in shadow map)
	//0.0 = in shadow
	 return float(distanceFromLight > shadowCoord.z);
} */
float shadowSimple(){
    float bias = 0.0005;
    vec4 shadowMapPosition = shadowCoord.xyzw;
   // shadowMapPosition = (shadowMapPosition+1.0)/2.0;
    vec4 packedDistance = texture2D(u_ShadowMap,shadowMapPosition.st);
    float lightDist = unpack(packedDistance);

    return float(lightDist > shadowCoord.z-bias);
}
/** LOOPS NOT SUPPORTED IN FRAGMENT SHADER
        tested on SAMSUNG-SM-T337A Android 5.1.1 API 22 **/
void main()
{
    vec4 dirColor = getDirLightColor(0)+getDirLightColor(1)+getDirLightColor(2);
    vec4 pointColor = getPointLightColor(0)+getPointLightColor(1)+getPointLightColor(2);
    vec4 spotColor = getSpotLightColor(0)+getSpotLightColor(1)+getSpotLightColor(2);
    float visibility = shadowSimple();
    gl_FragColor = (dirColor+pointColor+spotColor) * visibility;

 //   vec4 packedDistance = texture2D(u_ShadowMap,shadowCoord.st);
//   gl_FragColor = vec4(packedDistance.x,packedDistance.z,gl_FragCoord.z,1.0);
}
