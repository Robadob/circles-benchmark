@echo off
cd model
echo "Building Problem Scale"
FOR /L %%B IN (50,10,300) DO (
java FLAMEGPUBuilder -w %%B -d 0.01 -rad 5 -attract 0.00001 -repel 0.00001 -in . -out "../executables/problem_scale/CirclesPartitioning_%%B.exe"
)
echo "Building Entropy"
java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.00001 -repel 0.00001 -in . -out "../executables/entropy/CirclesPartitioning_1.exe"
java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.00002 -repel 0.00002 -in . -out "../executables/entropy/CirclesPartitioning_2.exe"
java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.00004 -repel 0.00004 -in . -out "../executables/entropy/CirclesPartitioning_4.exe"
java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.00008 -repel 0.00008 -in . -out "../executables/entropy/CirclesPartitioning_8.exe"
java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.00016 -repel 0.00016 -in . -out "../executables/entropy/CirclesPartitioning_16.exe"
java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.00032 -repel 0.00032 -in . -out "../executables/entropy/CirclesPartitioning_32.exe"
java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.00064 -repel 0.0006 -in . -out "../executables/entropy/CirclesPartitioning_64.exe"
java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.00128 -repel 0.00128 -in . -out "../executables/entropy/CirclesPartitioning_128.exe"
java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.00256 -repel 0.00256 -in . -out "../executables/entropy/CirclesPartitioning_256.exe"
echo "Building Neighbourhood Scale"
FOR /L %%A IN (15,-1,1) DO (
java FLAMEGPUBuilder -w 100 -d 0.01 -rad %%A -attract 0.00001 -repel 0.00001 -in . -out "../executables/neighbourhood_scale/CirclesPartitioning_%%A.exe"
)
cd ..