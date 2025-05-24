#include "FlowNetwork.h"

enum Mode {
    MinFlow,
    MaxFlow,
    FeasibleFLow
};

int runApp(char *filename, int mode, int source, int sink) {
    FlowNetwork network = FlowNetwork::createFromFile(filename);

    switch (mode) {
        case MaxFlow:
            network.sendMaximumFlow(source, sink);
            break;
        case MinFlow:
            network.sendMinimumFlow(source, sink);
            break;
        case FeasibleFLow:
            network.establishFeasibleFlow(source, sink);
            break;
    }
    network.saveToFile(filename, source, sink);
    return 0;
}

int main(int argc, char *argv[]) {
    int arg2 = std::stoi(argv[2]);
    int arg3 = std::stoi(argv[3]);
    int arg4 = std::stoi(argv[4]);

    return runApp(argv[1], arg2, arg3, arg4);
}