#version 110
uniform sampler2D tex;
uniform vec3 color;
void main()
{
    vec4 pixColor = texture2D(tex, gl_TexCoord[0].xy);
    vec3 targetColor = color * pixColor.r * (1.0 - pixColor.g) + vec3(1,1,1) * pixColor.g;
    gl_FragColor = vec4( targetColor.r , targetColor.g , targetColor.b , pixColor.a ) * gl_Color;
}