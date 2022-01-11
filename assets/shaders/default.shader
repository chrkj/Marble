#type vertex
#version 460 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec2 aTexCoord;

uniform mat4 uView;
uniform mat4 uProjection;
uniform mat4 uWorld;

out vec2 fTexCoord;

void main()
{
    fTexCoord = aTexCoord;
    gl_Position = uProjection * uView * uWorld * vec4(aPos, 1.0);
}

#type fragment
#version 460 core

in vec2 fTexCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;

void main()
{
    fragColor = texture(texture_sampler, fTexCoord);
}