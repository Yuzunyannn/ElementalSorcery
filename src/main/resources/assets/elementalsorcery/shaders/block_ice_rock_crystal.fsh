#version 110

uniform sampler2D texA;
uniform sampler2D texB;
uniform sampler2D mask;
uniform float r;
uniform float yoffset;

void main()
{
    vec2 coord = gl_TexCoord[0].xy;
    vec4 pixColorA = texture2D(texA, coord) * gl_Color;
    vec4 pixColorB = texture2D(texB, coord) * gl_Color;
    vec4 maskColor = texture2D(mask, vec2( coord.x , coord.y + yoffset ));
    float ratio = min(r , 1.0) * 2.0;
    if ( ratio > maskColor.r ) {
        float a = min(ratio -  maskColor.r , 1.0);
        gl_FragColor = pixColorA * ( 1.0 - a ) + pixColorB * a;
    } else gl_FragColor = pixColorA;
}