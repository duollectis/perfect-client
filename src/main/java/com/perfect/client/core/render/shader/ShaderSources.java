package com.perfect.client.core.render.shader;


public class ShaderSources {

	public static final String BLIT_SCREEN = """
			#version 330 core
			
			uniform sampler2D Sampler0;
			
			uniform vec4 color;
			
			in vec2 texCoord;
			out vec4 fragColor;
			
			void main() {
				fragColor = texture(Sampler0, texCoord) * color;
			}
			""";

	public static final String BLIT_SCREEN_LIGHT_MAP = """
			#version 330 core
			
			uniform vec4 color;

			out vec4 fragColor;
			
			void main() {
				fragColor = color;
			}
			""";

	public static final String POSITION = """
			#version 330 core
			
			uniform vec4 color;
			
			out vec4 fragColor;
			
			void main() {
				fragColor = color;
			}
			""";

	public static final String POSITION_TEXTURE = """
			#version 330 core
			
			uniform sampler2D Sampler0;
			uniform vec4 color;
			
			in vec2 texCoord;
			
			out vec4 fragColor;
			
			void main() {
				vec4 col = texture(Sampler0, texCoord);
			    if (col.a < 0.001) {
			        discard;
			    }
				fragColor = col * color;
			}
			""";

	public static final String POSITION_COLOR = """
			#version 330 core
			
			uniform vec4 color;
			
			in vec4 vertexColor;
			
			out vec4 fragColor;
			
			void main() {
				vec4 col = vertexColor;
			    if (col.a < 0.001) {
			        discard;
			    }
				fragColor = col * color;
			}
			""";

	public static final String POSITION_TEXTURE_COLOR = """
			#version 330 core
			
			uniform sampler2D Sampler0;
			uniform vec4 color;
			
			in vec2 texCoord;
			in vec4 vertexColor;
			
			out vec4 fragColor;
			
			void main() {
				vec4 col = texture(Sampler0, texCoord) * vertexColor;
				fragColor = col * color;
			}
			""";

	public static final String BRIGHT_POSITION_TEXTURE_COLOR = """
			#version 330 core
			
			uniform sampler2D Sampler0;
			uniform vec4 color;
			
			in vec2 texCoord;
			in vec4 vertexColor;
			
			out vec4 fragColor;
			
			void main() {
				vec4 col = texture(Sampler0, texCoord) * vertexColor;
			
				vec4 bright = col + mix(col, vec4(1.0), 0.3);
	
				fragColor = color * bright * col * 2.0;
			}
			""";
}
