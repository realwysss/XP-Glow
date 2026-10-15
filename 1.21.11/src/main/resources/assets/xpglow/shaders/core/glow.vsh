#version 330

layout(std140) uniform GlowInfo {
    mat4 Proj;
    mat4 Rot;
    vec4 P;
    vec4 Q;
    vec4 E[512];
};

out vec2 uv;
out vec3 col;
out float vz;
out float rad;

void main() {
    int i = gl_VertexID / 6;
    int k = gl_VertexID - i * 6;
    vec2 q = vec2((k == 1 || k == 2 || k == 4) ? 1.0 : -1.0, (k == 2 || k == 4 || k == 5) ? 1.0 : -1.0);
    vec4 a = E[i * 2];
    vec4 b = E[i * 2 + 1];
    vec3 v = (Rot * vec4(a.xyz, 1.0)).xyz;
    vz = -v.z;
    v.xy += q * a.w;
    gl_Position = Proj * vec4(v, 1.0);
    uv = q;
    col = b.rgb;
    rad = a.w;
}
