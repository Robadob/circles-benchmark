REM run me via: "Benchmark Neighbourhood Scale.bat" > neighbourhoodResults.txt
cd executables
cd neighbourhood_scale
FOR /L %%A IN (15,-1,1) DO (
CirclesPartitioning_%%A.exe "../../../init/100.xml" 1000 1
)
cd ..
cd ..