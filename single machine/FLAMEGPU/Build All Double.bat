@echo off
cd model
echo "Building Problem Scale"
FOR /L %%B IN (50,10,300) DO (
REM java FLAMEGPUBuilder -w %%B -d 0.01 -rad 5 -attract 0.001 -repel 0.001 -double -out "../executables/problem_scale/CirclesPartitioning_%%B.exe"
)
echo "Building Entropy"
REM java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.0001 -repel 0.0001 -double -out "../executables/entropy/CirclesPartitioning_1.exe"
REM java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.0005 -repel 0.0005 -double -out "../executables/entropy/CirclesPartitioning_2.exe"
REM java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.001 -repel 0.001 -double -out "../executables/entropy/CirclesPartitioning_4.exe"
REM java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.005 -repel 0.005 -double -out "../executables/entropy/CirclesPartitioning_8.exe"
REM java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.01 -repel 0.05 -double -out "../executables/entropy/CirclesPartitioning_16.exe"
REM java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.05 -repel 0.05 -double -out "../executables/entropy/CirclesPartitioning_32.exe"
REM java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.1 -repel 0.1 -double -out "../executables/entropy/CirclesPartitioning_64.exe"
REM java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 0.5 -repel 0.5 -double -out "../executables/entropy/CirclesPartitioning_128.exe"
REM java FLAMEGPUBuilder -w 100 -d 0.01 -rad 5 -attract 1 -repel 1 -double -out "../executables/entropy/CirclesPartitioning_256.exe"
echo "Building Neighbourhood Scale"
FOR /L %%A IN (15,-1,1) DO (
java FLAMEGPUBuilder -w 100 -d 0.01 -rad %%A -attract 0.001 -repel 0.001 -double -out "../executables/neighbourhood_scale/CirclesPartitioning_%%A.exe"
)
cd ..