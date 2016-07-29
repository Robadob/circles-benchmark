@echo off
cd model/Circles3D

FOR /L %%A IN (15,-1,1) DO (
echo %%A
java  -classpath "../repast-circles.jar" circles3D.CirclesBatchRunner -width 100 -density 0.01 -radius %%A -attract 0.001 -repel 0.001 -load "../../../init/100.txt" -for 1000 -out ../../neighbourScaleResults.txt
)
cd ../..