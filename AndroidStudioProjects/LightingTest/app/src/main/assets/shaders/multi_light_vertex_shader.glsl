// model matricies
uniform mat4 u_MVP;
uniform mat4 u_MV;

// model attributes
attribute vec3 a_Position;
attribute vec4 a_Color;
attribute vec3 a_Normal;

// base data
varying vec4 v_Color;
varying vec3 v_Normal;

// base lighting data
varying vec4 v_EyeSpacePos;

// basic shadow data
uniform mat4 u_DepthMVP;
varying vec4 shadowCoord;


mat3 transposeOfInverse(mat3 m){
    float determinant = +m[0][0]*(m[1][1]*m[2][2]-m[2][1]*m[1][2])
                        -m[0][1]*(m[1][0]*m[2][2]-m[1][2]*m[2][0])
                        +m[0][2]*(m[1][0]*m[2][1]-m[1][1]*m[2][0]);
    float invdet = 1.0/determinant;
    mat3 result = mat3(1.0,0.0,0.0,
                       0.0,1.0,0.0,
                       0.0,0.0,1.0);
    result[0][0] = (m[1][1]*m[2][2]-m[2][1]*m[1][2])*invdet;
    result[1][0] = (m[0][1]*m[2][2]-m[0][2]*m[2][1])*invdet;
    result[2][0] = (m[0][1]*m[1][2]-m[0][2]*m[1][1])*invdet;
    result[0][1] = (m[1][0]*m[2][2]-m[1][2]*m[2][0])*invdet;
    result[1][1] = (m[0][0]*m[2][2]-m[0][2]*m[2][0])*invdet;
    result[2][1] = (m[0][0]*m[1][2]-m[1][0]*m[0][2])*invdet;
    result[0][2] = (m[1][0]*m[2][1]-m[2][0]*m[1][1])*invdet;
    result[1][2] = (m[0][0]*m[2][1]-m[2][0]*m[0][1])*invdet;
    result[2][2] = (m[0][0]*m[1][1]-m[1][0]*m[0][1])*invdet;
    return result;
}

void main() {

    mat3 normalM;
    normalM = mat3(
       u_MV[0][0],u_MV[0][1],u_MV[0][2],
       u_MV[1][0],u_MV[1][1],u_MV[1][2],
       u_MV[2][0],u_MV[2][1],u_MV[2][2]
    );
    normalM = transposeOfInverse(normalM);

    v_Color = a_Color;
    // normal in eye space
    v_Normal = normalize(a_Normal * normalM);
    // compute vertex position in eye space
    v_EyeSpacePos = u_MV * vec4(a_Position.xyz,1.0);

// shadow stuff
    mat4 biasMatrix = mat4(
        0.5,0.0,0.0,0.0,
        0.0,0.5,0.0,0.0,
        0.0,0.0,0.5,0.0,
        0.5,0.5,0.5,1.0
    );
    mat4 depthBiasMVP = biasMatrix*u_DepthMVP;
    shadowCoord = depthBiasMVP*vec4(a_Position,1);

    gl_Position = u_MVP * vec4(a_Position.xyz,1.0);
}