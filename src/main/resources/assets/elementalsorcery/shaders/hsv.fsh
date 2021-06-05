uniform sampler2D tex;
uniform float u_hue;
uniform float u_saturation;
uniform float u_value;
uniform float r_anime;
uniform float a_hue;
uniform float a_saturation;
uniform float a_value;
vec3 rgbtohsv(vec3 rgb)
{
    float R = rgb.x;
    float G = rgb.y;
    float B = rgb.z;
    vec3 hsv;
    float max1 = max(R, max(G, B));
    float min1 = min(R, min(G, B));
    if (R == max1) hsv.x = (G - B) / (max1 - min1);
    if (G == max1) hsv.x = 2.0 + (B - R) / (max1 - min1);
    if (B == max1)hsv.x = 4.0 + (R - G) / (max1 - min1);
    hsv.x = hsv.x * 60.0;
    if (hsv.x < 0.0)hsv.x = hsv.x + 360.0;
    hsv.z = max1;
    hsv.y = (max1 - min1) / max1;
    return hsv;
}
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
    vec4 pixColor = texture2D(tex, gl_TexCoord[0].xy) * gl_Color;
    vec3 hsv;

    float tx = floor(cos(gl_TexCoord[0].x * 6.28)  * 100.0);
    float ty = floor(sin(gl_TexCoord[0].y * 3.14) * 500.0); 
    float n = max( r_anime * 10.0 , 1.0 );
    vec3 f_color;
    if( mod(tx, n ) * mod(ty, n) <= r_anime * 10.0  )
    {
        hsv.xyz = rgbtohsv(pixColor.rgb);
        hsv.x += u_hue;
        hsv.x = mod(hsv.x, 360.0);
        hsv.y *= u_saturation;
        hsv.z *= u_value;
        f_color = hsvtorgb(hsv);
       
    }
    else
    {   
        hsv.xyz = rgbtohsv(pixColor.rgb);
        hsv.x += a_hue;
        hsv.x = mod(hsv.x, 360.0);
        hsv.y *= a_saturation;
        hsv.z *= a_value;
        f_color = hsvtorgb(hsv);
    }
     gl_FragColor = vec4(f_color, pixColor.a);
}