#version 330

uniform sampler2D DepthSampler;

layout(std140) uniform GlowInfo {
    mat4 Proj;
    mat4 Rot;
    vec4 P;
    vec4 Q;
    vec4 E[512];
};

in vec2 uv;
in vec3 col;
in float vz;
in float rad;
out vec4 fragColor;

void main() {
    float d = length(uv);
    if (d >= 1.0) discard;
    float f = pow(1.0 - d, P.z);
    vec2 sp = gl_FragCoord.xy / P.xy;
    float dp = texture(DepthSampler, sp).r;
    float nz = Q.z > 0.5 ? dp : dp * 2.0 - 1.0;
    float sz = Q.y / (nz + Q.x);
    float vis = clamp((sz - vz) / (rad * 0.6) + 0.75, 0.0, 1.0);
    fragColor = vec4(col * f * vis, 0.0);
}
