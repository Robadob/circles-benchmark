@echo off
cd model
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.0001 -repel 0.0001 -load "../../init/100.txt" -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.0005 -repel 0.0005 -load "../../init/100.txt" -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.001 -repel 0.001 -load "../../init/100.txt" -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.005 -repel 0.005 -load "../../init/100.txt" -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.01 -repel 0.01 -load "../../init/100.txt" -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.05 -repel 0.05 -load "../../init/100.txt" -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.1 -repel 0.1 -load "../../init/100.txt" -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.5 -repel 0.5 -load "../../init/100.txt" -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 1 -repel 1 -load "../../init/100.txt" -for 100 -quiet
cd ..