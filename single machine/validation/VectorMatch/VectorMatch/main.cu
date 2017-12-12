
#include <stdio.h>
#include <iostream>
#include <fstream>
#include "kernel.cu"
#include <glm/gtc/epsilon.hpp>
#include <random>

bool readFile(char *fileName, glm::vec3 **vecs, int *length);
int main(int argc, char** argv)
{
	if (argc==2)
	{
		int len = 1000;
		std::ofstream oFile;
		oFile.open(argv[1]);
		if (oFile.is_open())
		{
			oFile << len << "\n"; 
			std::default_random_engine rng;
			std::uniform_real_distribution<float> distribution(-100.0f, 100.0f);
			for (int i = 0; i < len;i++)
			{
				oFile << distribution(rng) << "," << distribution(rng) << "," << distribution(rng) << "\n";
			}
			oFile.close();
		}
		else
		{
			printf("Failed to open file: %s\n",argv[1]);
		}
		return 0;
	}
	if (argc < 3)
	{
		printf("Min 2 args, the two files to be compared, optional 3rd arg device id.");
		return 1;
	}

	int length1 = 0, length2 = 0;
	glm::vec3 *vecs1 = 0, *vecs2 = 0;
	std::ifstream file1;
	if (readFile(argv[1], &vecs1, &length1) && readFile(argv[2], &vecs2, &length2))
	{//Files are loaded
		if (length1 != length2)
		{
			printf("File lengths do not match: %d vs %d\n", length1, length2);
			return 1;
		}
        {
            //Sort both using GPU
            if (argc>=4)
            {
            	printf("Using CUDA device %d\n", atoi(argv[3]));
            	cudaSetDevice(atoi(argv[3]));
            }
            glm::vec3 *d_vecs = nullptr;
            cudaMalloc(&d_vecs, length1*sizeof(glm::vec3));
            cudaMemcpy(d_vecs, vecs1, length1*sizeof(glm::vec3), cudaMemcpyHostToDevice);
            thrust::sort(thrust::cuda::par, d_vecs, d_vecs + length1, vecCompare());
            cudaMemcpy(vecs1, d_vecs, length1*sizeof(glm::vec3), cudaMemcpyDeviceToHost);
            cudaMemcpy(d_vecs, vecs2, length1*sizeof(glm::vec3), cudaMemcpyHostToDevice);
            thrust::sort(thrust::cuda::par, d_vecs, d_vecs + length1, vecCompare());
            cudaMemcpy(vecs2, d_vecs, length1*sizeof(glm::vec3), cudaMemcpyDeviceToHost);
            cudaFree(d_vecs); 
		}
        //Allocate storage for mismatches
        glm::vec3 *mismatchA = (glm::vec3 *)malloc(length1*sizeof(glm::vec3));
        glm::vec3 *mismatchB = (glm::vec3 *)malloc(length1*sizeof(glm::vec3));
        unsigned int mismatchCt = 0;
		//Compare both on CPU
		int j = 0;
		float minLength = FLT_MAX;
		float maxLength = 0;
		float meanLength = 0;
		int maxId = -1;
		for (int i = 0; i < length1;i++)
		{
			//printf("(%.9g,%.9g,%.9g)==(%.9g,%.9g,%.9g)\n", vecs1[i].x, vecs1[i].y, vecs1[i].z, vecs2[i].x, vecs2[i].y, vecs2[i].z);

			auto ret = glm::epsilonEqual(vecs1[i], vecs2[i], 1.0f);
			if (ret.x&&ret.y&&ret.z)
			{

				j++;
			}
            else
            {
                //Reorder components
                mismatchA[mismatchCt] = glm::vec3(vecs1[i].y, vecs1[i].x, vecs1[i].z);
                mismatchB[mismatchCt] = glm::vec3(vecs2[i].y, vecs2[i].x, vecs2[i].z);
                mismatchCt++;
            }
			float len = glm::length(vecs1[i] - vecs2[i]);
			minLength = len < minLength ? len : minLength;
			maxId = maxLength < len ? i : maxId;
			maxLength = maxLength < len ? len : maxLength;
			meanLength += len / length1;
		}
		//printf("%d/%d Successful matches!\n", j, length1);
		//printf("Min diff: %f\nMax diff: %f (%d)\nMean diff:%f\n", minLength, maxLength, maxId, meanLength);
		//printf("(%.9g,%.9g,%.9g)==(%.9g,%.9g,%.9g)\n", vecs1[maxId].x, vecs1[maxId].y, vecs1[maxId].z, vecs2[maxId].x, vecs2[maxId].y, vecs2[maxId].z);

        if (j!=length1)
        {//If first sort failed, resort the mismatched vectors
            glm::vec3 *d_vecs = nullptr;
            cudaMalloc(&d_vecs, mismatchCt*sizeof(glm::vec3));
            cudaMemcpy(d_vecs, mismatchA, mismatchCt*sizeof(glm::vec3), cudaMemcpyHostToDevice);
            thrust::sort(thrust::cuda::par, d_vecs, d_vecs + mismatchCt, vecCompare());
            cudaMemcpy(mismatchA, d_vecs, mismatchCt*sizeof(glm::vec3), cudaMemcpyDeviceToHost);
            cudaMemcpy(d_vecs, mismatchB, mismatchCt*sizeof(glm::vec3), cudaMemcpyHostToDevice);
            thrust::sort(thrust::cuda::par, d_vecs, d_vecs + mismatchCt, vecCompare());
            cudaMemcpy(mismatchB, d_vecs, mismatchCt*sizeof(glm::vec3), cudaMemcpyDeviceToHost);
            cudaFree(d_vecs);
            //Re match remainders
            minLength = FLT_MAX;
            maxLength = 0;
            meanLength = 0;
            maxId = -1;
            for (int i = 0; i < mismatchCt; i++)
            {
                auto ret = glm::epsilonEqual(mismatchA[i], mismatchB[i], 1.0f);
                if (ret.x&&ret.y&&ret.z)
                {
                    j++;
                }
                float len = glm::length(mismatchA[i] - mismatchB[i]);
                minLength = len < minLength ? len : minLength;
                maxId = maxLength < len ? i : maxId;
                maxLength = maxLength < len ? len : maxLength;
                meanLength += len / length1;
            }
            //printf("----------\n");
            printf("%d/%d Successful matches!\n", j, length1);
            printf("Min diff: %f\nMax diff: %f\nMean diff:%f\n", minLength, maxLength, meanLength);
            printf("(%.9g,%.9g,%.9g)==(%.9g,%.9g,%.9g)\n", vecs1[maxId].x, vecs1[maxId].y, vecs1[maxId].z, vecs2[maxId].x, vecs2[maxId].y, vecs2[maxId].z);
        }
	}
	else
	{
		return 1;
	}
	free(vecs1);
	free(vecs2);
	return 0;
}

bool readFile(char *fileName, glm::vec3 **vecs, int *length)
{
	char line[1024];
	std::ifstream file;
	file.open(fileName);
	if (file.is_open())
	{
		file.getline(&line[0], 1024);
		sscanf(line, "%d", length);
		if (*length <= 0)
		{
			printf("File does not begin with a positive integer\n");
			return false;
		}
		*vecs = (glm::vec3*)malloc(*length*sizeof(glm::vec3));
		for (int i = 0; i < *length; i++)
		{
			file.getline(&line[0], 1024);
			if (sscanf(line, "%f,%f,%f", &(*vecs)[i].x, &(*vecs)[i].y, &(*vecs)[i].z)<3)
			{
				printf("Line %d of File was not recognised.\n", i);
				return false;
			}
		}
		file.close();
	}
	else
	{
		printf("Could not open file: %s\n", fileName);
		return false;
	}
	return true;
}