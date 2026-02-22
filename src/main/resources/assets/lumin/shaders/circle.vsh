#version 460 core

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 Center;
layout(location = 3) in float Radius;
layout(location = 4) in float Thickness;

out vec2 v_Position;
out vec4 v_Color;
flat out vec2 v_Center;
flat out float v_Radius;
flat out float v_Thickness;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    v_Position = Position.xy;
    v_Color = Color;
    v_Center = Center;
    v_Radius = Radius;
    v_Thickness = Thickness;
}
