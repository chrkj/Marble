#type vertex
#version 460 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec2 aTexCoord;
layout (location=2) in vec3 aVertexNormal;

uniform mat4 uView;
uniform mat4 uWorld;
uniform mat4 uProjection;
uniform float uTime;

out vec2 fTexCoord;
out vec3 fVertexPos;
out vec3 fVertexNormal;

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
in vec3 fVertexPos;
in vec3 fVertexNormal;

layout(location = 0) out vec4 fragColor;
layout(location = 1) out int redColor;
layout(location = 2) out vec4 depthColor;

const int MAX_DIR_LIGHTS = 10;
struct DirectionalLight
{
    vec4 color;
    vec3 direction;
    float intensity;
};

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

const int MAX_POINT_LIGHTS = 10;
struct PointLight
{
    vec4 color;
    vec3 position;
    float intensity;
    Attenuation att;
};

const int MAX_SPOT_LIGHTS = 10;
struct SpotLight
{
    PointLight pl;
    vec3 conedir;
    float cutoff;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

uniform Material uMaterial;
uniform SpotLight uSpotLight[MAX_SPOT_LIGHTS];
uniform PointLight uPointLight[MAX_POINT_LIGHTS];
uniform DirectionalLight uDirectionalLight[MAX_DIR_LIGHTS];
uniform vec3 uAmbientLight;
uniform float uSpecularPower;
uniform sampler2D uTextureSampler;
uniform int uEntityID;

void setupColors(Material uMaterial, vec2 textCoord)
{
    if (uMaterial.hasTexture == 1)
    {
        ambientC = texture(uTextureSampler, textCoord);
        diffuseC = ambientC;
        speculrC = ambientC;
    }
    else
    {
        ambientC = uMaterial.ambient;
        diffuseC = uMaterial.diffuse;
        speculrC = uMaterial.specular;
    }
}

vec4 calcLightColor(vec4 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specColor = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColor = diffuseC * light_color * light_intensity * diffuseFactor;

    // Specular Light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir, normal));
    float specularFactor = max( dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, uSpecularPower);
    specColor = speculrC * light_intensity * specularFactor * uMaterial.reflectance * light_color;

    return (diffuseColor + specColor);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.position - position;
    vec3 to_light_dir = normalize(light_direction);
    vec4 light_color = calcLightColor(light.color, light.intensity, position, to_light_dir, normal);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
    light.att.exponent * distance * distance;
    return light_color / attenuationInv;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.pl.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec3 from_light_dir  = -to_light_dir;
    float spot_alfa = dot(from_light_dir, normalize(light.conedir));

    vec4 color = vec4(0, 0, 0, 0);

    if (spot_alfa > light.cutoff)
    {
        color = calcPointLight(light.pl, position, normal);
        color *= (1.0 - (1.0 - spot_alfa)/(1.0 - light.cutoff));
    }
    return color;
}

void main()
{
    setupColors(uMaterial, fTexCoord);

    vec4 diffuseSpecularComp;
    for (int i = 0; i < MAX_DIR_LIGHTS; i++)
    {
        if (uDirectionalLight[i].intensity > 0)
        {
            diffuseSpecularComp += calcDirectionalLight(uDirectionalLight[i], fVertexPos, fVertexNormal);
        }
    }
    for (int i = 0; i < MAX_POINT_LIGHTS; i++)
    {
        if (uPointLight[i].intensity > 0)
        {
            diffuseSpecularComp += calcPointLight(uPointLight[i], fVertexPos, fVertexNormal);
        }
    }
    for (int i = 0; i < MAX_SPOT_LIGHTS; i++)
    {
        if (uSpotLight[i].pl.intensity > 0)
        {
            diffuseSpecularComp += calcSpotLight(uSpotLight[i], fVertexPos, fVertexNormal);
        }
    }

    fragColor = ambientC * vec4(uAmbientLight, 1) + diffuseSpecularComp;

    redColor = uEntityID;
}