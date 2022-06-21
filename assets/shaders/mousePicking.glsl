#type vertex
#version 460 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec2 aTexCoord;
layout (location=2) in vec3 aVertexNormal;

uniform mat4 uView;
uniform mat4 uWorld;
uniform mat4 uProjection;
uniform float uTime;
uniform int uEntityID;

out flat int fEntityID;

void main()
{
    gl_Position = uProjection * uView * uWorld * vec4(aPos, 1.0);

    fEntityID = uEntityID;
}

#type fragment
#version 460 core

in flat int fEntityID;

out vec4 color;

void main()
{
    color = vec4(1, 0, fEntityID, 1);
}