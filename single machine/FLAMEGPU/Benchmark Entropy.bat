REM run me via: "Benchmark Entropy.bat" > entropyResults.txt
setlocal enableDelayedExpansion
cd executables
cd entropy
SET  B=1
FOR /L %%A IN (1,1,9) DO (
CirclesPartitioning_!B!.exe "../../init/neighbourhood_scale.xml" 100
SET /A B*=2
)
cd ..
cd ..