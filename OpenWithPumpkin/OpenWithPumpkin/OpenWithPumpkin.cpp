#include <iostream>

int main(int argc, char** argv)
{
    if (argc < 2) return -1;

    char command[256];
    char* cc = *(argv + 1);
    sprintf_s(command, "runEditor.bat %s", cc);

    system(command);

    return 0;
}
