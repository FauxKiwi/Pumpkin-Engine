#type vertex
#version 330 core

layout (location = 0) in vec3 a_Position;
layout (location = 1) in vec2 a_Size;
layout (location = 2) in float a_Rotation;
layout (location = 3) in vec4 a_Color;
layout (location = 4) in vec4 a_TexCoord;
layout (location = 5) in float a_TexIndex;
layout (location = 6) in float a_TilingFactor;

out vec2 g_Size;
out float g_Rotation;
out vec4 g_Color;
out vec4 g_TexCoord;
out float g_TexIndex;
out float g_TilingFactor;

uniform mat4 u_ViewProjection;

void main() {
    gl_Position = u_ViewProjection * vec4(a_Position, 1.0);
    g_Size = a_Size;
    g_Rotation = a_Rotation;
    g_TexCoord = a_TexCoord;
    g_Color = a_Color;
    g_TexIndex = a_TexIndex;
    g_TilingFactor = a_TilingFactor;
}

#type geometry
#version 330 core

layout (points) in;
layout (triangle_strip, max_vertices = 5) out;

in vec2 g_Size[];
in float g_Rotation[];
in vec4 g_TexCoord[];
in vec4 g_Color[];
in float g_TexIndex[];
in float g_TilingFactor[];

out vec2 f_TexCoord;
out vec4 f_Color;
out float f_TexIndex;
out float f_TilingFactor;

uniform mat4 u_ViewProjection;

void main() {
    f_Color = g_Color[0];
    f_TexIndex = g_TexIndex[0];
    f_TilingFactor = g_TilingFactor[0];

    vec4 position = gl_in[0].gl_Position;
    vec2 size = g_Size[0];
    float rotation = g_Rotation[0];
    vec4 texCoord = g_TexCoord[0];

    mat4 transform = u_ViewProjection *
    mat4(
        size.x, 0.0, 0.0, 0.0,
        0.0, size.y, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 1.0
    ) *
    mat4(
        cos(rotation), -sin(rotation), 0.0, 0.0,
        sin(rotation), cos(rotation), 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 1.0
    );

    f_TexCoord = texCoord.xy;
    gl_Position = position + transform * vec4(-0.5,-0.5, 0.0, 0.0);
    EmitVertex();
    f_TexCoord = texCoord.zy;
    gl_Position = position + transform * vec4( 0.5,-0.5, 0.0, 0.0);
    EmitVertex();
    f_TexCoord = texCoord.xw;
    gl_Position = position + transform * vec4(-0.5, 0.5, 0.0, 0.0);
    EmitVertex();
    f_TexCoord = texCoord.zw;
    gl_Position = position + transform * vec4( 0.5, 0.5, 0.0, 0.0);
    EmitVertex();
    EndPrimitive();
}

#type fragment
#version 330 core

in vec4 f_Color;
in vec2 f_TexCoord;
in float f_TexIndex;
in float f_TilingFactor;

layout (location = 0) out vec4 color;

uniform sampler2D u_Textures[32];

void main() {
    color = texture(u_Textures[int(f_TexIndex)], f_TexCoord * f_TilingFactor) * f_Color;
    //color = vec4(0.0, 1.0, 0.0, 1.0);
    //color = f_Color;
}
