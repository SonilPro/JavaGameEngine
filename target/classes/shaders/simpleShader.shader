//VERTEX SHADER
#version 400 core

in vec3 position;
in vec2 textureCoords;

out vec2 fragTextureCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(){
    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
    fragTextureCoords = textureCoords;
}
//FRAGMENT SHADER
#version 400 core

in vec2 fragTextureCoords;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main(){
    fragColor = texture(textureSampler, fragTextureCoords);
}