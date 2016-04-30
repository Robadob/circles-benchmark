REM run me via: "Benchmark Problem Scale.bat" > problemScaleResults.txt
cd executables
cd problem_scale
FOR /L %%A IN (300,-10,50) DO (
CirclesPartitioning_%%A.exe "../../init/%%A.xml" 1000
)
cd ..
cd ..