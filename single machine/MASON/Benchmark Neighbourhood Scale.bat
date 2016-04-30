@echo off
cd model
FOR /L %%A IN (15,-1,1) DO (
echo %%A
java sim.Circles3D -width 100 -density 0.01 -radius %%A -attract 0.00001 -repel 0.00001 -for 1000 -quiet
)
cd ..
echo Results have been saved to "model/results.txt"