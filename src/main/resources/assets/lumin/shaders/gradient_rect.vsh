#version 460 core

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color1;
layout(location = 2) in vec4 Color2;
layout(location = 3) in vec2 GradientStart;
layout(location = 4) in vec2 GradientEnd;

out vec2 v_Position;
out vec4 v_Color1;
out vec4 v_Color2;
flat out vec2 v_GradientStart;
flat out vec2 v_GradientEnd;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    v_Position = Position.xy;
    v_Color1 = Color1;
    v_Color2 = Color2;
    v_GradientStart = GradientStart;
    v_GradientEnd = GradientEnd;
}
