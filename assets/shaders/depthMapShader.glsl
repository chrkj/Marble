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

// Depth-only pass; the depth buffer is written automatically
void main()
{
}