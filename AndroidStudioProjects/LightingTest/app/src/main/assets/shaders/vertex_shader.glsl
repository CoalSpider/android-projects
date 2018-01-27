
uniform mat4 u_MVP;
uniform mat4 u_MV;

attribute vec3 a_Position;
attribute vec4 a_Color;
attribute vec3 a_Normal;

// used for lighting
varying vec4 v_Diffuse,v_Ambient,v_Color,v_CameraSpacePos;
varying vec3 v_Normal,v_HalfVector;
// used for point lighting
varying float v_ConstantAttenuation,v_LinearAttenuation,v_QuadraticAttenuation;
// used for spot lighting
varying vec3 v_SpotDirection;
varying float v_SpotCosCutoff,v_SpotExponent;

// shadows
uniform mat4 u_DepthMVP;
varying vec4 v_ShadowCoord;

struct LightInfo{
   vec3 lightPos;
   vec4 diffuseColor;
   vec4 ambientColor;
   vec4 specularColor;
   float ambientIntensity;
   float specularIntensity;
   float constantAttenuation;
   float linearAttenuation;
   float quadraticAttenuation;
   vec3 spotDirection;
   float spotCosCutoff;
   float spotExponent;
};

uniform LightInfo lightInfo;

// shader start 198 // now 250
void main()
{
    // shadow stuff start
    const mat4 biasMat = mat4(0.5,0.0,0.0,0.0,
                              0.0,0.5,0.0,0.0,
                              0.0,0.0,1.0,0.0,
                              0.5,0.5,0.5,1.0);
    mat4 depthBiasMPV = biasMat * u_DepthMVP;

    v_ShadowCoord = depthBiasMPV * vec4(a_Position.xyz,1.0);
    // shadow stuff end

   mat3 normalM;
   normalM = mat3(
       u_MV[0][0],
       u_MV[0][1],
       u_MV[0][2],
       u_MV[1][0],
       u_MV[1][1],
       u_MV[1][2],
       u_MV[2][0],
       u_MV[2][1],
       u_MV[2][2]
   );
   v_Color = a_Color;
   // normal in eye space
   v_Normal = normalize(a_Normal * normalM);
   // compute vertex position in camera space
   v_CameraSpacePos = u_MV * vec4(a_Position.xyz,1.0);
   // normalize half vector
   v_HalfVector = lightInfo.lightPos / 2.0;
   // material diffuse * light diffuse
   v_Diffuse = vec4(0.5,0.5,0.5,1.0) * lightInfo.diffuseColor;
   // material ambient * light ambient
   v_Ambient = vec4(0.1,0.1,0.1,1.0) * lightInfo.ambientColor;
   v_ConstantAttenuation = lightInfo.constantAttenuation;
   v_LinearAttenuation = lightInfo.linearAttenuation;
   v_QuadraticAttenuation = lightInfo.quadraticAttenuation;
   v_SpotDirection = lightInfo.spotDirection;
   v_SpotCosCutoff = lightInfo.spotCosCutoff;
   v_SpotExponent = lightInfo.spotExponent;

   gl_Position = u_MVP * vec4(a_Position.xyz,1.0);
}