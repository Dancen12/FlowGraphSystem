//
// Created by dcend on 29.03.2025.
//

#ifndef RESIDUALGRAPH_H
#define RESIDUALGRAPH_H

#include "ResidualEdge.h"
#include <vector>

class ResidualGraph {
    int numberOfVertices;
    std::vector<std::vector<ResidualEdge> > adjacencyList;
    std::vector<int> levelOfVertex;
    std::vector<int> edgePointer;
    static constexpr int UNASSIGNED_LEVEL = -1;

    bool setLevelsByBFS(int source, int sink);

    int sendFlowByDFS(int currentVertex, int sink, int flow);

    int dinicMaxFlow(int source, int sink);

public:
    explicit ResidualGraph(int numberOfVertices);

    int findMaxFlow(int source, int sink);

    void addResidualEdgePair(int v1, int v2, int forwardResidualValue, int backwardResidualValue);

    std::vector<std::vector<ResidualEdge> > &getAdjacencyList();
};


#endif //RESIDUALGRAPH_H
