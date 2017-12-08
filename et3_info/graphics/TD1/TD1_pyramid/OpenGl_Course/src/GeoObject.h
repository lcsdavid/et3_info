#pragma once

#include <GL/glew.h> // include GLEW and new version of GL on Windows
#include <GLFW/glfw3.h> // GLFW helper library


class GeoObject
{
public:
	GeoObject();
	virtual ~GeoObject();
	void buildPolyeder(int size, float r, float g, float b);
	void buildPolygon(int size);
	void InitVBO(void);
	void updateVBO(void);
	void setColors(float* colors);
	void setVertices(float* vertices);
	float* getVertices();

	unsigned int getSize() { return _size; }

	unsigned int _vao;

protected:
	float * _vertices;
	float * _normals;
	float * _colors;
	float * _texcoors;
	unsigned int _vboVertices;
	unsigned int _vboColors;
	unsigned int _vboNormals;
	unsigned int _size; //Number of Vertices / Normals / Texture Coordonates
};
