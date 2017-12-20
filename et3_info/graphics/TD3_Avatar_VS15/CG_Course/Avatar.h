#pragma once

#include "Cylinder.h"
#include "Spheres.h"

#define ANGULAR_SPEED_SCALE  1.5f
#define LINEAR_SPEED_SCALE   1.f

class Avatar
{
public:
	Avatar();
	~Avatar();

	void update(double);

	
	enum part { LEFT = 0, RIGHT = 1};

	// avatar components and animation
	Spheres *_head;
	Cylinder *_torso, *_arm[2], *_forearm[2], *_thigh[2], *_leg[2];

	float _Yposition;

	float _velocity[3];
	float _armsAngle, _forearmsAngle, _thighsAngle, _legsAngle;
};
