//
// Created by dcend on 05.04.2025.
//

#include <ResidualGraph.h>
#include <gtest/gtest.h>

#include "gtest/gtest.h"
#include "ResidualGraph.h"

// Test prostego grafu 0 -> 1 -> 2
TEST(ResidualGraphTest, SimpleChain) {
  ResidualGraph g(3);
  g.addResidualEdgePair(0, 1, 5, 0);
  g.addResidualEdgePair(1, 2, 3, 0);

  int maxFlow = g.findMaxFlow(0, 2);
  EXPECT_EQ(maxFlow, 3);

  std::vector<std::vector<ResidualEdge> > &adj = g.getAdjacencyList();
  EXPECT_EQ(adj[0][0].residualValue, 2);
  EXPECT_EQ(adj[1][adj[0][0].reverseEdgeId].residualValue, 3);
  EXPECT_EQ(adj[1][1].residualValue, 0);
  EXPECT_EQ(adj[2][adj[1][1].reverseEdgeId].residualValue, 3);
}

// Test rozgałęzienia: 0 -> 1, 0 -> 2, 1 -> 3, 2 -> 3
TEST(ResidualGraphTest, ForkJoin) {
  ResidualGraph g(4);
  g.addResidualEdgePair(0, 1, 5, 0);
  g.addResidualEdgePair(0, 2, 7, 0);
  g.addResidualEdgePair(1, 3, 4, 0);
  g.addResidualEdgePair(2, 3, 3, 0);

  int maxFlow = g.findMaxFlow(0, 3);
  EXPECT_EQ(maxFlow, 7);

  std::vector<std::vector<ResidualEdge> > &adj = g.getAdjacencyList();
  EXPECT_EQ(adj[0][0].residualValue, 1);
  EXPECT_EQ(adj[1][adj[0][0].reverseEdgeId].residualValue, 4);
  EXPECT_EQ(adj[0][1].residualValue, 4);
  EXPECT_EQ(adj[2][adj[0][1].reverseEdgeId].residualValue, 3);
  EXPECT_EQ(adj[1][1].residualValue, 0);
  EXPECT_EQ(adj[3][adj[1][1].reverseEdgeId].residualValue, 4);
  EXPECT_EQ(adj[2][1].residualValue, 0);
  EXPECT_EQ(adj[3][adj[2][1].reverseEdgeId].residualValue, 3);
}


// Test z istniejącym przepływem
TEST(ResidualGraphTest, WithInitialFlow) {
  ResidualGraph g(4);
  g.addResidualEdgePair(0, 1, 5, 2);
  g.addResidualEdgePair(0, 2, 7, 1);
  g.addResidualEdgePair(1, 3, 4, 2);
  g.addResidualEdgePair(2, 3, 3, 1);

  int additionalMaxFlow = g.findMaxFlow(0, 3);
  EXPECT_EQ(additionalMaxFlow, 7);
  // w przypadku gdy jakis przeplyw juz istenije, to wynikiem jest przeplyw ktory mozna jeszcze puscic

  std::vector<std::vector<ResidualEdge> > &adj = g.getAdjacencyList();
  EXPECT_EQ(adj[0][0].residualValue, 1);
  EXPECT_EQ(adj[1][adj[0][0].reverseEdgeId].residualValue, 6);
  EXPECT_EQ(adj[0][1].residualValue, 4);
  EXPECT_EQ(adj[2][adj[0][1].reverseEdgeId].residualValue, 4);
  EXPECT_EQ(adj[1][1].residualValue, 0);
  EXPECT_EQ(adj[3][adj[1][1].reverseEdgeId].residualValue, 6);
  EXPECT_EQ(adj[2][1].residualValue, 0);
  EXPECT_EQ(adj[3][adj[2][1].reverseEdgeId].residualValue, 4);
}

// Test cyklu: 0 -> 1 -> 2 -> 0 i 0 -> 3
TEST(ResidualGraphTest, GraphWithCycle) {
  ResidualGraph g(4);
  g.addResidualEdgePair(0, 1, 4, 0);
  g.addResidualEdgePair(1, 2, 4, 0);
  g.addResidualEdgePair(2, 0, 4, 0);
  g.addResidualEdgePair(0, 3, 5, 0);
  int maxFlow = g.findMaxFlow(0, 3);
  EXPECT_EQ(maxFlow, 5);

  std::vector<std::vector<ResidualEdge> > &adj = g.getAdjacencyList();
  EXPECT_EQ(adj[0][0].residualValue, 4);
  EXPECT_EQ(adj[1][adj[0][0].reverseEdgeId].residualValue, 0);
  EXPECT_EQ(adj[1][1].residualValue, 4);
  EXPECT_EQ(adj[2][adj[1][1].reverseEdgeId].residualValue, 0);
  EXPECT_EQ(adj[2][1].residualValue, 4);
  EXPECT_EQ(adj[0][adj[2][1].reverseEdgeId].residualValue, 0);
  EXPECT_EQ(adj[0][2].residualValue, 0);
  EXPECT_EQ(adj[3][adj[0][2].reverseEdgeId].residualValue, 5);
}

// Test bez ścieżki między źródłem a ujściem
TEST(ResidualGraphTest, NoPath) {
  ResidualGraph g(4);
  g.addResidualEdgePair(0, 1, 10, 0);
  g.addResidualEdgePair(2, 3, 5, 0);

  EXPECT_EQ(g.findMaxFlow(0, 3), 0);
}

//test sprawdzajacy liczenie przeplywu w druga strone
TEST(ResidualGraphTest, ReverseFlow) {
  ResidualGraph g(4);
  g.addResidualEdgePair(0, 1, 5, 3);
  g.addResidualEdgePair(0, 2, 7, 5);
  g.addResidualEdgePair(1, 3, 4, 2);
  g.addResidualEdgePair(2, 3, 3, 3);

  int reverseMaxFlow = g.findMaxFlow(3, 0);
  EXPECT_EQ(reverseMaxFlow, 5);

  std::vector<std::vector<ResidualEdge> > &adj = g.getAdjacencyList();
  EXPECT_EQ(adj[0][0].residualValue, 7);
  EXPECT_EQ(adj[1][adj[0][0].reverseEdgeId].residualValue, 1);
  EXPECT_EQ(adj[0][1].residualValue, 10);
  EXPECT_EQ(adj[2][adj[0][1].reverseEdgeId].residualValue, 2);
  EXPECT_EQ(adj[1][1].residualValue, 6);
  EXPECT_EQ(adj[3][adj[1][1].reverseEdgeId].residualValue, 0);
  EXPECT_EQ(adj[2][1].residualValue, 6);
  EXPECT_EQ(adj[3][adj[2][1].reverseEdgeId].residualValue, 0);
}
