/*
* MIT License
*
* Copyright (c) 2021 Robert Hutter
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*
* SimulationPhysicsFaculty.cpp - This program conducts a physics simulation of
*   freefall with drag.
*
*/

#include <iostream>
#include <stdio.h>
#include <stdlib.h>

#define FDRAG(v) 0.008*v*v
#define GRAVITY 10
#define RUNTIME 10
#define RESOLUTION 0.000001
#define MASS 0.4

#define FNAME "data.csv"

int main()
{
    printf("Freefall physics simulation with drag.\nCopyright (c) 2021 Robert Hutter\n\n");

    FILE* file;
    fopen_s(&file, FNAME, "w");

    if (file == NULL)
    {
        printf("Error opening output file...\n");
        return EXIT_FAILURE;
    }
    fprintf_s(file, "time,a,v,s\n");

    double runtime = 0, v = 0, a = GRAVITY, s = 0;

    printf("Starting simulation...\n");
    unsigned long i = 0;
    for (; runtime < RUNTIME; runtime += RESOLUTION)
    {
        a = GRAVITY - FDRAG(v) / MASS;
        v += RESOLUTION * a;
        s += RESOLUTION * v;

        if (i % 100 == 0)
            fprintf_s(file, "%F,%F,%F,%F\n", runtime, a, v, s);

        i++;
    }
    fclose(file);
    
    printf("Simulation complete. End v = %F", v);

    return EXIT_SUCCESS;
}