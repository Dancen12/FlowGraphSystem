//
// Created by dcend on 05.04.2025.
//
#include <FlowNetwork.h>
#include <gtest/gtest.h>
TEST(FlowNetworkTest, EstablishingFeasibleFlow_SimpleChain) {
  FlowNetwork network = FlowNetwork(3);
  network.addEdge(0, 1, 2, 6, 0);
  network.addEdge(1, 2, 3, 7, 0);

  network.establishFeasibleFlow(0, 2);

  std::vector<std::vector<FlowBoundedEdge> > &adj = network.getAdjacencyList();

  // 0->1
  EXPECT_TRUE(adj[0][0].currentFlow>=adj[0][0].lowerBound && adj[0][0].currentFlow<=adj[0][0].upperBound);
  // 1->2
  EXPECT_TRUE(adj[1][0].currentFlow>=adj[1][0].lowerBound && adj[1][0].currentFlow<=adj[1][0].upperBound);
  //node 1
  EXPECT_EQ(adj[0][0].currentFlow, adj[1][0].currentFlow);
}

TEST(FlowNetworkTest, EstablishingFeasibleFlow_ForkJoin) {
  FlowNetwork network = FlowNetwork(4);
  network.addEdge(0, 1, 3, 5, 0);
  network.addEdge(0, 2, 1, 6, 0);
  network.addEdge(1, 3, 2, 7, 0);
  network.addEdge(2, 3, 1, 8, 0);

  network.establishFeasibleFlow(0, 3);

  std::vector<std::vector<FlowBoundedEdge> > &adj = network.getAdjacencyList();

  //0->1
  EXPECT_TRUE(adj[0][0].currentFlow>=adj[0][0].lowerBound && adj[0][0].currentFlow<=adj[0][0].upperBound);
  //0->2
  EXPECT_TRUE(adj[0][1].currentFlow>=adj[0][1].lowerBound && adj[0][1].currentFlow<=adj[0][1].upperBound);
  //1->3
  EXPECT_TRUE(adj[1][0].currentFlow>=adj[1][0].lowerBound && adj[1][0].currentFlow<=adj[1][0].upperBound);
  //2->3
  EXPECT_TRUE(adj[2][0].currentFlow>=adj[2][0].lowerBound && adj[2][0].currentFlow<=adj[2][0].upperBound);
  //node 1
  EXPECT_EQ(adj[0][0].currentFlow, adj[1][0].currentFlow);
  //node 2
  EXPECT_EQ(adj[0][1].currentFlow, adj[2][0].currentFlow);
}

TEST(FlowNetworkTest, EstablishingFeasibleFlow_WithInitialFlow) {
  FlowNetwork network = FlowNetwork(4);
  network.addEdge(0, 1, 3, 5, 4);
  network.addEdge(0, 2, 1, 6, 0);
  network.addEdge(1, 3, 2, 7, 4);
  network.addEdge(2, 3, 1, 8, 0);

  network.establishFeasibleFlow(0, 3);

  std::vector<std::vector<FlowBoundedEdge> > &adj = network.getAdjacencyList();

  //0->1
  EXPECT_TRUE(adj[0][0].currentFlow>=adj[0][0].lowerBound && adj[0][0].currentFlow<=adj[0][0].upperBound);
  //0->2
  EXPECT_TRUE(adj[0][1].currentFlow>=adj[0][1].lowerBound && adj[0][1].currentFlow<=adj[0][1].upperBound);
  //1->3
  EXPECT_TRUE(adj[1][0].currentFlow>=adj[1][0].lowerBound && adj[1][0].currentFlow<=adj[1][0].upperBound);
  //2->3
  EXPECT_TRUE(adj[2][0].currentFlow>=adj[2][0].lowerBound && adj[2][0].currentFlow<=adj[2][0].upperBound);
  //node 1
  EXPECT_EQ(adj[0][0].currentFlow, adj[1][0].currentFlow);
  //node 2
  EXPECT_EQ(adj[0][1].currentFlow, adj[2][0].currentFlow);
}

TEST(FlowNetworkTest, MinFlow_SimpleChain) {
  FlowNetwork network = FlowNetwork(3);
  network.addEdge(0, 1, 2, 6, 0);
  network.addEdge(1, 2, 3, 7, 0);

  EXPECT_EQ(network.sendMinimumFlow(0, 2), 3);

  std::vector<std::vector<FlowBoundedEdge> > &adj = network.getAdjacencyList();

  // 0->1
  EXPECT_EQ(adj[0][0].currentFlow, 3);
  // 1->2
  EXPECT_EQ(adj[1][0].currentFlow, 3);
}

TEST(FlowNetworkTest, MinFlow_ForkJoin) {
  FlowNetwork network = FlowNetwork(4);
  network.addEdge(0, 1, 3, 5, 0);
  network.addEdge(0, 2, 1, 6, 0);
  network.addEdge(1, 3, 2, 7, 0);
  network.addEdge(2, 3, 1, 8, 0);

  EXPECT_EQ(network.establishFeasibleFlow(0, 3), 4);

  std::vector<std::vector<FlowBoundedEdge> > &adj = network.getAdjacencyList();

  //0->1
  EXPECT_EQ(adj[0][0].currentFlow, 3);
  //0->2
  EXPECT_EQ(adj[0][1].currentFlow, 1);
  //1->3
  EXPECT_EQ(adj[1][0].currentFlow, 3);
  //2->3
  EXPECT_EQ(adj[2][0].currentFlow, 1);
}

TEST(FlowNetworkTest, MinFlow_WithInitialFlow) {
  FlowNetwork network = FlowNetwork(4);
  network.addEdge(0, 1, 3, 5, 4);
  network.addEdge(0, 2, 1, 6, 0);
  network.addEdge(1, 3, 2, 7, 4);
  network.addEdge(2, 3, 1, 8, 0);

  EXPECT_EQ(network.sendMinimumFlow(0, 3), 4);

  std::vector<std::vector<FlowBoundedEdge> > &adj = network.getAdjacencyList();

  //0->1
  EXPECT_EQ(adj[0][0].currentFlow, 3);
  //0->2
  EXPECT_EQ(adj[0][1].currentFlow, 1);
  //1->3
  EXPECT_EQ(adj[1][0].currentFlow, 3);
  //2->3
  EXPECT_EQ(adj[2][0].currentFlow, 1);
}

TEST(FlowNetworkTest, MinFlow_ReverseFlow) {
  FlowNetwork network = FlowNetwork(3);
  network.addEdge(0, 1, 2, 6, 0);
  network.addEdge(1, 2, 3, 7, 0);

  EXPECT_EQ(network.sendMinimumFlow(2,0), 0);
}

TEST(FlowNetworkTest, MaxFlow_SimpleChain) {
  FlowNetwork network = FlowNetwork(3);
  network.addEdge(0, 1, 2, 6, 0);
  network.addEdge(1, 2, 3, 7, 0);

  EXPECT_EQ(network.sendMaximumFlow(0, 2), 6);

  std::vector<std::vector<FlowBoundedEdge> > &adj = network.getAdjacencyList();

  // 0->1
  EXPECT_EQ(adj[0][0].currentFlow, 6);
  // 1->2
  EXPECT_EQ(adj[1][0].currentFlow, 6);
}

TEST(FlowNetworkTest, MaxFlow_ForkJoin) {
  FlowNetwork network = FlowNetwork(4);
  network.addEdge(0, 1, 3, 5, 0);
  network.addEdge(0, 2, 1, 6, 0);
  network.addEdge(1, 3, 2, 7, 0);
  network.addEdge(2, 3, 1, 8, 0);

  EXPECT_EQ(network.sendMaximumFlow(0, 3), 11);

  std::vector<std::vector<FlowBoundedEdge> > &adj = network.getAdjacencyList();

  //0->1
  EXPECT_EQ(adj[0][0].currentFlow, 5);
  //0->2
  EXPECT_EQ(adj[0][1].currentFlow, 6);
  //1->3
  EXPECT_EQ(adj[1][0].currentFlow, 5);
  //2->3
  EXPECT_EQ(adj[2][0].currentFlow, 6);
}

TEST(FlowNetworkTest, MaxFlow_WithInitialFlow) {
  FlowNetwork network = FlowNetwork(4);
  network.addEdge(0, 1, 3, 5, 4);
  network.addEdge(0, 2, 1, 6, 0);
  network.addEdge(1, 3, 2, 7, 4);
  network.addEdge(2, 3, 1, 8, 0);

  EXPECT_EQ(network.sendMaximumFlow(0, 3), 11);

  std::vector<std::vector<FlowBoundedEdge> > &adj = network.getAdjacencyList();

  //0->1
  EXPECT_EQ(adj[0][0].currentFlow, 5);
  //0->2
  EXPECT_EQ(adj[0][1].currentFlow, 6);
  //1->3
  EXPECT_EQ(adj[1][0].currentFlow, 5);
  //2->3
  EXPECT_EQ(adj[2][0].currentFlow, 6);
}

TEST(FlowNetworkTest, MaxFlow_ReverseFlow) {
  FlowNetwork network = FlowNetwork(3);
  network.addEdge(0, 1, 2, 6, 0);
  network.addEdge(1, 2, 3, 7, 0);

  EXPECT_EQ(network.sendMaximumFlow(2,0), 0);
}