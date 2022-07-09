																	
uniform sampler2D tex;

uniform vec3 move;
uniform float ratio;
uniform float c_ratio;

vec3 hsvtorgb(vec3 hsv)
{
    vec3 color;
    if (hsv.y == 0.0) color = hsv.zzz;
    else
    {
        hsv.x = hsv.x / 60.0;
        int i = int(hsv.x);
        float f = hsv.x - float(i);
        float a = hsv.z * (1.0 - hsv.y);
        float b = hsv.z * (1.0 - hsv.y * f);
        float c = hsv.z * (1.0 - hsv.y * (1.0 - f));
        if (i == 0) color = vec3( hsv.z , c , a );
        else if (i == 1) color = vec3( b , hsv.z , a );
        else if (i == 2) color = vec3( a , hsv.z , c );
        else if (i == 3) color = vec3( a , b , hsv.z );
        else if (i == 4) color = vec3( c , a , hsv.z );
        else color = vec3( hsv.z , a , b );
    }
    return color;
}
void main()
{
    vec2 v_texCoord = gl_TexCoord[0].xy;
    float csin = cos( c_ratio + v_texCoord.y + v_texCoord.x );
    v_texCoord = vec2( v_texCoord.x + move.x, v_texCoord.y + move.y );
    vec4 oColor = texture2D(tex, v_texCoord);
    vec3 cColor = hsvtorgb(vec3( csin * 180.0 + 180.0 , 0.25 , 1.0));
    float d = (sin(ratio) + 1.0) / 2.0;
    float r = cColor.r * d;
    float g = cColor.g * d;
    float b = cColor.b * d;
    gl_FragColor = vec4( r, g, b , oColor.a ) * gl_Color;
}