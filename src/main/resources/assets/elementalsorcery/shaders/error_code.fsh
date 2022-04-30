																	
uniform sampler2D tex;

uniform float u_n;
uniform float u_a;
void main()
{
    vec2 v_texCoord = gl_TexCoord[0].xy;
    vec2 coord;
    if (cos((v_texCoord.x + u_n) * 17) < -0.8 || cos((v_texCoord.y + u_n) * 28) < -0.8) coord = v_texCoord;
    else coord = vec2( 
        v_texCoord.x
            + sin((v_texCoord.y - u_n) * 3701.0) * sin((v_texCoord.y + u_n) * 3323.0) * u_a 
            + sin((v_texCoord.y - u_n) * 64.0) * cos((v_texCoord.y - u_n) * 64.0) * u_a * 3 
        , v_texCoord.y );
        
    gl_FragColor = texture2D(tex, coord) * gl_Color;
}