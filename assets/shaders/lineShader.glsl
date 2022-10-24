#type vertex
#version 460 core

layout (location=0) in vec3 a_Position;
layout (location=1) in vec4 a_Color;

uniform mat4 uView;
uniform mat4 uProjection;
out vec4 fColor;

void main()
{
    gl_Position = uProjection * uView * vec4(a_Position, 1.0);

    fColor = a_Color;
}

#type fragment
#version 460 core

in vec4 fColor;

layout(location = 0) out vec4 fragColor;

void main()
{
    fragColor = fColor;
}