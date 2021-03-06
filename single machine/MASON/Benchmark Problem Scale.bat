@echo off
cd model
FOR /L %%A IN (50,10,300) DO (
echo %%A
java sim.Circles3D -width %%A -density 0.01 -radius 5 -attract 0.001 -repel 0.001 -load "../../init/%%A.txt" -for 1000 -quiet
)
cd ..
echo Results have been saved to "model/results.txt"