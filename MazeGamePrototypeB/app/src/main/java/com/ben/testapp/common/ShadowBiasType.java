package com.ben.testapp.common;

/**
 * The type of bias for shadow mapping.
 *
 * Bias is used to reduce shadow acne.
 *
 * Constant bias is some set value such as 0.0005
 * Dynamic bias varies according to the slope
 **/
public enum ShadowBiasType{
    NONE, CONSTANT, DYNAMIC
}