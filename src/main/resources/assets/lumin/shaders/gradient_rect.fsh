#version 460 core

in vec2 v_Position;
in vec4 v_Color1;
in vec4 v_Color2;
flat in vec2 v_GradientStart;
flat in vec2 v_GradientEnd;

layout(location = 0) out vec4 f_Color;

void main() {
    vec2 gradientDir = v_GradientEnd - v_GradientStart;
    float gradientLen = length(gradientDir);
    
    if (gradientLen < 0.001) {
        f_Color = v_Color1;
        return;
    }
    
    vec2 gradientNorm = gradientDir / gradientLen;
    vec2 toPoint = v_Position - v_GradientStart;
    float t = dot(toPoint, gradientNorm) / gradientLen;
    t = clamp(t, 0.0, 1.0);
    
    f_Color = mix(v_Color1, v_Color2, t);
}
