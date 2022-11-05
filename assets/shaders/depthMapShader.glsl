#type vertex
#version 460 core
layout (location=0) in vec3 aPos;

uniform mat4 model;
uniform mat4 lightSpaceMatrix;

void main()
{
    gl_Position = lightSpaceMatrix * model * vec4(aPos, 1.0);
}

#type fragment
#version 460 core
out vec4 FragColor;

layout(location = 0) out vec4 depthColor;

uniform sampler2D depthMap;

void main()
{
}