
#include <cuda.h>
#include <cuda_runtime.h>
#include <device_launch_parameters.h>

#include "glm/glm.hpp"
#include <thrust/sort.h>
#include <thrust/system/cuda/execution_policy.h>

struct vecCompare : public thrust::binary_function < const glm::vec3, const glm::vec3, bool >
{
	__host__ __device__ bool operator()(const glm::vec3 &lhs, const glm::vec3 &rhs) const {
		if (lhs.x == rhs.x)
		{
			if (lhs.y == rhs.y)
			{
				return (lhs.z < rhs.z);
			}
			else
			{
				return (lhs.y < rhs.y);
			}
		}
		else
		{
			return (lhs.x < rhs.x);
		}
	}
};