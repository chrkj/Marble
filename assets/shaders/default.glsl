#type vertex
#version 460 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec2 aTexCoord;
layout (location=2) in vec3 aVertexNormal;

uniform float uTime;
uniform mat4 uView;
uniform mat4 uWorld;
uniform mat4 uProjection;

out vec2 fTexCoord;
out vec3 fVertexNormal;
out vec3 fVertexPos;

void main()
{
    gl_Position = uProjection * uView * uWorld * vec4(aPos, 1.0);

    fTexCoord = aTexCoord;
    fVertexNormal = normalize(uView * uWorld * vec4(aVertexNormal, 0.0)).xyz;
    fVertexPos = (uView * uWorld * vec4(aPos, 1.0)).xyz;
}

#type fragment
#version 460 core

in vec2 fTexCoord;
in vec3 fVertexNormal;
in vec3 fVertexPos;

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