
precision mediump float;

// used for lighting
varying vec4 v_Diffuse,v_Ambient,v_Color,v_CameraSpacePos;
varying vec3 v_Normal,v_HalfVector;
// used for point lighting
varying float v_ConstantAttenuation,v_LinearAttenuation,v_QuadraticAttenuation;
// used for spot lighting
varying vec3 v_SpotDirection;
varying float v_SpotCosCutoff,v_SpotExponent;

// shadow stuff
uniform sampler2D u_ShadowMap;
varying vec4 v_ShadowCoord;

// unpack colour to depth value
float unpack(vec4 color)
{
    const vec4 bitShifts = vec4(1.0 / (256.0 * 256.0 * 256.0),
                                1.0 / (256.0 * 256.0),
                                1.0 / 256.0,
                                1);
    return dot(color , bitShifts);
}

float lookup(){
    vec4 shadowMapPos = v_ShadowCoord / v_ShadowCoord.w;
    shadowMapPos = (shadowMapPos + 1.0) /2.0;
    // shadow stuff start
    vec4 packedValue = texture2D(u_ShadowMap,shadowMapPos.st);
    float distanceFromLight = unpack(packedValue);

    float visibility = 1.0;
    if(distanceFromLight > shadowMapPos.z){
       visibility = 0.1;
    }
    // shadow stuff end
    return visibility;
}

void main()
{
   float visibility = lookup();

   vec3 n,halfV,viewV,lightDir;
   float NdotL,NdotHV;
   vec4 color = v_Ambient;
   // POINT LIGHT
   float att,dist;
   n = normalize(v_Normal);
   // POINT LIGHT (-cameraSpacePos.xyz)
   lightDir = vec3(v_HalfVector*2.0 - v_CameraSpacePos.xyz);
   // dist POINT LIGHT
   dist = length(lightDir);
   NdotL = max(dot(n,normalize(lightDir)),0.0);
   if(NdotL > 0.0){
       float spotEffect;
       // SPOT LIGHT
       spotEffect = dot(normalize(v_SpotDirection),normalize(-lightDir));
       if(spotEffect > v_SpotCosCutoff){
           spotEffect = pow(spotEffect,v_SpotExponent);
           att = spotEffect / (v_ConstantAttenuation+v_LinearAttenuation*dist+
               v_QuadraticAttenuation*dist*dist);
           color += att*(v_Diffuse*NdotL);

           halfV = normalize(v_HalfVector);
           NdotHV = max(dot(n,halfV),0.0);
           float shininess = 2.0;
           color += att * vec4(1.0,0.0,0.0,1.0) * vec4(1.0,0.0,0.0,1.0) *
           pow(NdotHV,shininess);
       }
       // POINT LIGHT
       // att = 1.0 / (constantAttenuation + linearAttenuation*dist +
       // quadraticAttenuation*dist*dist);
       // attn * = POINT LIGHT
       // color += att * (diffuse * NdotL);
       // halfV = normalize(halfVector);
       // NdotHV = max(dot(n,halfV),0.0);
      //  float shininess = 2.0;
       // attn * = POINT LIGHT
      //  color += att * vec4(1.0,0.0,0.0,1.0) * vec4(1.0,0.0,0.0,1.0) *
      // pow(NdotHV,shininess);
   }

    // gl_FragColor = vec4(color);
   // generates depth map
   // gl_FragColor = vec4(gl_FragCoord.z,0.0,0.0,1.0);
   // shadow stuff
   gl_FragColor = vec4(vec3(0.5,0.5,0.5) * visibility,color.a);
}