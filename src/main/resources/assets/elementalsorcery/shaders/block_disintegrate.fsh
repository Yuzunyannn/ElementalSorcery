#version 130

uniform sampler2D texA;
uniform sampler2D maskA;
uniform sampler2D maskB;
uniform sampler2D texB;
uniform float r;
uniform vec3 color;
uniform bool gray;
uniform bool rc;

void main()
{
    vec2 coord = gl_TexCoord[0].xy;
    ivec2 texSize = textureSize(texA,0);
    vec4 pixColor = texture2D(texA, coord) * gl_Color;
    
    float x = coord.x * 64.0 * texSize.x / 1024.0;
    float y = coord.y * 32.0 * texSize.y / 512.0;
    vec4 maskAColor = texture2D(maskA, vec2( x , y ));
    vec4 maskBColor = texture2D(maskB, vec2( x , y ));
    vec4 texBColor  = texture2D(texB , vec2( x , y )) * gl_Color;
    
    float a = maskAColor.a * ( 1.0 - r ) + maskBColor.a * r;
    if (rc) texBColor = vec4( 1 - texBColor.r , 1 - texBColor.g , 1 - texBColor.b , texBColor.a );
    float r = pixColor.r * a + texBColor.r * color.r * ( 1.0 - a );
    float g = pixColor.g * a + texBColor.g * color.g * ( 1.0 - a );
    float b = pixColor.b * a + texBColor.b * color.b * ( 1.0 - a );
    
    if (gray) {
        float c = min(pixColor.r * 0.299 + pixColor.g * 0.587 + pixColor.b * 0.114,1.0);
        gl_FragColor = vec4( c , c , c , a * a * pixColor.a );
    } else gl_FragColor = vec4( r , g , b , a * a * pixColor.a );
}