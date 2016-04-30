@echo off
cd model
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.000001 -repel 0.000001 -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.000002 -repel 0.000002 -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.000004 -repel 0.000004 -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.000008 -repel 0.000008 -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.000016 -repel 0.000016 -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.000032 -repel 0.000032 -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.000064 -repel 0.000064 -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.000128 -repel 0.000128 -for 100 -quiet
java sim.Circles3D -width 100 -density 0.01 -radius 5 -attract 0.000256 -repel 0.000256 -for 100 -quiet
cd ..