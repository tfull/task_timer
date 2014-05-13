@echo off

call :week
exit /b

:normal
java -cp .;bin pack.Timers
exit /b

:week
for /f %%t in ('date /t') do set nt=%%t
if %nt% == "0" (
java -cp .;bin pack.Timers "data/weekend.txt"
) else if %nt% == "6" (
java -cp .;bin pack.Timers "data/weekend.txt"
) else (
java -cp .;bin pack.Timers "data/title.txt"
)
exit /b
