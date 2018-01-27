
precision mediump float;

 uniform vec3 u_LightPos;
 uniform sampler2D u_Texture;

 varying vec3 v_Position;
 varying vec4 v_Color;
 varying vec3 v_Normal;
 varying vec2 v_TexCoordinate;

 void main()
 {
     // used for attenuation
     float distance = length(u_LightPos - v_Position);
     // get lighting direction vector
     vec3 lightVector = normalize(u_LightPos - v_Position);
     // calc diffuse 0.1 == min lighting
     float diffuse = max(dot(v_Normal,lightVector),0.0);
     // add attenuation deciaml * distSqrd == damper for attenuation
     diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance * distance)));
     // add ambient lighting
     diffuse += 0.3;
     gl_FragColor = v_Color * diffuse * texture2D(u_Texture,v_TexCoordinate);
 }