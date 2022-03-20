#version 110

uniform sampler2D tex;
uniform sampler2D mask;

void main()
{
    vec2 coord = gl_TexCoord[0].xy;
    vec4 pixColor = texture2D(tex, coord) * gl_Color;
    float c = min(pixColor.r * 0.299 + pixColor.g * 0.587 + pixColor.b * 0.114,1.0);
    vec4 maskColor = texture2D(mask, coord) * gl_Color;
    float r = 1.0 + (c - 0.5) * 1.5 ;
    maskColor = maskColor * ( r );
    gl_FragColor = vec4( 
        maskColor.r, 
        maskColor.g, 
        maskColor.b, 
        pixColor.a);
}