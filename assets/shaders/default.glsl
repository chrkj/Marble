#type vertex
#version 460 core

layout (location=0) in vec3 aPos;
layout (location=1) in vec2 aTexCoord;
layout (location=2) in vec3 aVertexNormal;

uniform mat4 uView;
uniform mat4 uWorld;
uniform mat4 uProjection;
uniform float uTime;
uniform mat4 uLightSpaceMatrix;

out vec2 fTexCoord;
out vec3 fVertexPos;
out vec3 fVertexNormal;
out vec4 fFragPosLightSpace;
out vec3 fFragPos;

void main()
{
    gl_Position = uProjection * uView * uWorld * vec4(aPos, 1.0);

    fFragPos = vec3(uWorld * vec4(aPos, 1.0));
    fTexCoord = aTexCoord;
    fVertexNormal = normalize(uView * uWorld * vec4(aVertexNormal, 0.0)).xyz;
    fVertexPos = (uView * uWorld * vec4(aPos, 1.0)).xyz;
    fFragPosLightSpace = uLightSpaceMatrix * vec4(fFragPos, 1.0);
}

#type fragment
#version 460 core

in vec2 fTexCoord;
in vec3 fVertexPos;
in vec3 fVertexNormal;
in vec4 fFragPosLightSpace;
in vec3 fFragPos;

layout(location = 0) out vec4 fragColor;
layout(location = 1) out int redColor;
layout(location = 2) out vec4 depthColor;

const int MAX_DIR_LIGHTS = 10;
struct DirectionalLight
{
    vec3 position;
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
uniform int uEntityID;
uniform sampler2D uTextureSampler;
uniform sampler2D shadowMap;

vec2 poissonDisk[64] = vec2[](
vec2(-0.613392, 0.617481),
vec2(0.170019, -0.040254),
vec2(-0.299417, 0.791925),
vec2(0.645680, 0.493210),
vec2(-0.651784, 0.717887),
vec2(0.421003, 0.027070),
vec2(-0.817194, -0.271096),
vec2(-0.705374, -0.668203),
vec2(0.977050, -0.108615),
vec2(0.063326, 0.142369),
vec2(0.203528, 0.214331),
vec2(-0.667531, 0.326090),
vec2(-0.098422, -0.295755),
vec2(-0.885922, 0.215369),
vec2(0.566637, 0.605213),
vec2(0.039766, -0.396100),
vec2(0.751946, 0.453352),
vec2(0.078707, -0.715323),
vec2(-0.075838, -0.529344),
vec2(0.724479, -0.580798),
vec2(0.222999, -0.215125),
vec2(-0.467574, -0.405438),
vec2(-0.248268, -0.814753),
vec2(0.354411, -0.887570),
vec2(0.175817, 0.382366),
vec2(0.487472, -0.063082),
vec2(-0.084078, 0.898312),
vec2(0.488876, -0.783441),
vec2(0.470016, 0.217933),
vec2(-0.696890, -0.549791),
vec2(-0.149693, 0.605762),
vec2(0.034211, 0.979980),
vec2(0.503098, -0.308878),
vec2(-0.016205, -0.872921),
vec2(0.385784, -0.393902),
vec2(-0.146886, -0.859249),
vec2(0.643361, 0.164098),
vec2(0.634388, -0.049471),
vec2(-0.688894, 0.007843),
vec2(0.464034, -0.188818),
vec2(-0.440840, 0.137486),
vec2(0.364483, 0.511704),
vec2(0.034028, 0.325968),
vec2(0.099094, -0.308023),
vec2(0.693960, -0.366253),
vec2(0.678884, -0.204688),
vec2(0.001801, 0.780328),
vec2(0.145177, -0.898984),
vec2(0.062655, -0.611866),
vec2(0.315226, -0.604297),
vec2(-0.780145, 0.486251),
vec2(-0.371868, 0.882138),
vec2(0.200476, 0.494430),
vec2(-0.494552, -0.711051),
vec2(0.612476, 0.705252),
vec2(-0.578845, -0.768792),
vec2(-0.772454, -0.090976),
vec2(0.504440, 0.372295),
vec2(0.155736, 0.065157),
vec2(0.391522, 0.849605),
vec2(-0.620106, -0.328104),
vec2(0.789239, -0.419965),
vec2(-0.545396, 0.538133),
vec2(-0.178564, -0.596057)
);

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

float ShadowCalculation(vec4 fragPosLightSpace, vec3 lightPos)
{
    // perform perspective divide
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;

    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;

    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    float closestDepth = texture(shadowMap, projCoords.xy).r;

    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;

    // calculate bias (based on depth map resolution and slope)
    vec3 normal = normalize(fVertexNormal);
    vec3 lightDir = normalize(lightPos - fFragPos);
    //float bias = max(0.0009 * (1.0 - dot(normal, lightDir)), 0.00005);

    // PCF
    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for(int x = 0; x < 64; ++x)
    {
        float bias = 0;
        float pcfDepth = texture(shadowMap, projCoords.xy + poissonDisk[x] * texelSize).r;
        shadow += currentDepth - bias > pcfDepth  ? 1.0 : 0.0;
    }
    shadow /= 64.0;

    // keep the shadow at 0.0 when outside the far_plane region of the light's frustum.
    if(projCoords.z > 1.0)
    shadow = 0.0;

    return shadow;
}

void main()
{
    setupColors(uMaterial, fTexCoord);

    vec4 diffuseSpecularComp;
    for (int i = 0; i < MAX_DIR_LIGHTS; i++)
    {
        if (uDirectionalLight[i].intensity > 0)
        {
            float shadow = ShadowCalculation(fFragPosLightSpace, uDirectionalLight[i].position);
            diffuseSpecularComp += (1 - shadow) * calcDirectionalLight(uDirectionalLight[i], fVertexPos, fVertexNormal);
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