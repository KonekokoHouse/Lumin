#version 460 core

in vec4 v_Color;
in vec2 v_TexCoord;

uniform sampler2D Sampler0;

layout(location = 0) out vec4 f_Color;

void main() {
    vec4 tex = texture(Sampler0, v_TexCoord);
    f_Color = tex * v_Color;
    if (f_Color.a < 0.01) {
        discard;
    }
}

