package com.ben.testapp.common;

public class RenderConstants {
	/** Names of uniforms and attributes in shader */
	public static final String U_MVP_MATRIX = "uMVPMatrix";
	public static final String U_MV_MATRIX = "uMVMatrix";
	public static final String U_NORMAL_MATRIX = "uNormalMatrix";
	public static final String U_LIGHT_POSITION = "uLightPos";
	public static final String A_POSITION = "aPosition";
	public static final String A_NORMAL = "aNormal";
	public static final String A_COLOR = "aColor";
    public static final String A_TEX_COORD = "aTexCoord";
	
	public static final String U_SHADOW_TEXTURE = "uShadowTexture";
	public static final String U_SHADOW_PROJ_MATRIX = "uShadowProjMatrix";
	public static final String U_SHADOW_X_PIXEL_OFFSET = "uxPixelOffset";
	public static final String U_SHADOW_Y_PIXEL_OFFSET = "uyPixelOffset";
	public static final String A_SHADOW_POSITION = "aShadowPosition";

	public static final String U_MODEL_TEXTURE = "uTexture";

    public static final String U_HAS_TEXTURE = "hasTexture";
}