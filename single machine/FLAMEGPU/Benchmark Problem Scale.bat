REM run me via: "Benchmark Problem Scale.bat" > problemScaleResults.txt
cd executables
cd problem_scale
FOR /L %%A IN (50,10,300) DO (
CirclesPartitioning_%%A.exe "../../../init/%%A.xml" 1000 1
)
cd ..
cd ..