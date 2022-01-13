#type vertex
#version 460 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec2 aTexCoord;
layout (location=2) in vec3 vertexNormal;

uniform float uTime;
uniform mat4 uView;
uniform mat4 uWorld;
uniform mat4 uProjection;

out vec2 fTexCoord;

void main()
{
    vec3 transformed;
    transformed.x = aPos.x + sin(aPos.y * 10.0 + uTime * 10.0) * 0.1;
    transformed.y = aPos.y + sin(aPos.z * 10.0 + uTime * 10.0) * 0.1;
    transformed.z = aPos.z + sin(aPos.x * 10.0 + uTime * 10.0) * 0.1;


    fTexCoord = aTexCoord;
    gl_Position = uProjection * uView * uWorld * vec4(transformed, 1.0);
}

#type fragment
#version 460 core

in vec2 fTexCoord;
out vec4 fragColor;

uniform vec3 uColor;
uniform int useColor;
uniform sampler2D uTextureSampler;

void main()
{
    if ( useColor == 1 )
    {
        fragColor = vec4(uColor, 1);
    }
    else
    {
        fragColor = texture(uTextureSampler, fTexCoord);
    }
}