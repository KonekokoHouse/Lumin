#version 460 core

in vec2 v_Position;
in vec4 v_Color;
flat in vec2 v_Center;
flat in float v_Radius;
flat in float v_Thickness;

layout(location = 0) out vec4 f_Color;

float aastep(float threshold, float value) {
    float afwidth = fwidth(value) * 0.5;
    return smoothstep(threshold - afwidth, threshold + afwidth, value);
}

void main() {
    float dist = length(v_Position - v_Center);
    float innerRadius = v_Radius - v_Thickness * 0.5;
    float outerRadius = v_Radius + v_Thickness * 0.5;
    
    float outerAlpha = 1.0 - aastep(0.5, dist - outerRadius);
    float innerAlpha = aastep(0.5, dist - innerRadius);
    
    float alpha = outerAlpha * innerAlpha;
    
    if (alpha < 0.001) discard;
    
    f_Color = vec4(v_Color.rgb, v_Color.a * alpha);
}
