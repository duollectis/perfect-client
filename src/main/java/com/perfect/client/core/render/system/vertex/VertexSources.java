package com.perfect.client.core.render.system.vertex;


public class VertexSources {

	public static final String BLIT_SCREEN = """
			#version 330 core
			
			layout(location = 0) in vec2 position;
			layout(location = 1) in vec2 UV;
			
			out vec2 texCoord;
			
			void main() {
				gl_Position = vec4(position, 0.0, 1.0);
			    texCoord = UV;
			}
			""";

	public static final String BLIT_SCREEN_LIGHT_MAP = """
			#version 330 core
			
			layout(location = 0) in vec2 position;
			
			void main() {
				gl_Position = vec4(position, 0.0, 1.0);
			}
			""";

	public static final String POSITION = """
			#version 330 core
			
			layout(location = 0) in vec3 position;
			
			uniform mat4 ModelViewMat;
			
			layout(std140) uniform Projection {
			    mat4 ProjMat;
			};
			
			void main() {
				gl_Position = ProjMat * ModelViewMat * vec4(position, 1.0);
			}
			""";

	public static final String POSITION_TEXTURE = """
			#version 330 core
			
			layout(location = 0) in vec3 position;
			layout(location = 1) in vec2 UV;
			
			uniform mat4 ModelViewMat;
			
			layout(std140) uniform Projection {
			    mat4 ProjMat;
			};
			
			out vec2 texCoord;
			
			void main() {
				gl_Position = ProjMat * ModelViewMat * vec4(position, 1.0);
				texCoord = UV;
			}
			""";

	public static final String POSITION_COLOR = """
			#version 330 core
			
			layout(location = 0) in vec3 position;
			layout(location = 1) in vec4 color;
			
			uniform mat4 ModelViewMat;
			
			layout(std140) uniform Projection {
			    mat4 ProjMat;
			};
			
			out vec4 vertexColor;
			
			void main() {
				gl_Position = ProjMat * ModelViewMat * vec4(position, 1.0);
				vertexColor = color;
			}
			""";

	public static final String POSITION_TEXTURE_COLOR = """
			#version 330 core
			
			layout(location = 0) in vec3 position;
			layout(location = 1) in vec2 UV;
			layout(location = 2) in vec4 color;
			
			uniform mat4 ModelViewMat;
			
			layout(std140) uniform Projection {
			    mat4 ProjMat;
			};
			
			out vec2 texCoord;
			out vec4 vertexColor;
			
			void main() {
				gl_Position = ProjMat * ModelViewMat * vec4(position, 1.0);
				texCoord = UV;
				vertexColor = color;
			}
			""";
}
