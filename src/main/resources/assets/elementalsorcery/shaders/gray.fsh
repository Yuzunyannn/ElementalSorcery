uniform sampler2D tex;
void main()
{
    vec4 pixColor = texture2D(tex, gl_TexCoord[0].xy) * gl_Color;
    float c = min(pixColor.r * 0.299 + pixColor.g * 0.587 + pixColor.b * 0.114,1.0);
    gl_FragColor = vec4( c , c , c , pixColor.a);
}