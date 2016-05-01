@echo off
cd model/Circles3D
FOR /L %%A IN (50,10,300) DO (
echo %%A
java  -classpath "../repast-circles.jar" circles3D.CirclesBatchRunner -width %%A -density 0.01 -radius 5 -attract 0.00001 -repel 0.00001 -for 1000 -out ../../problemScaleResults.txt
)
cd ../..