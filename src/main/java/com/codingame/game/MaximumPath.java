package com.codingame.game;

import java.util.*;

public class MaximumPath {

    // Class to represent a node in the graph
    static class Node {
        int value;
        int maxPathValue;
        List<Node> neighbors;

        public Node(int value) {
            this.value = value;
            this.maxPathValue = Integer.MIN_VALUE;
            this.neighbors = new ArrayList<>();
        }
    }

    public static List<Node> getMaxPath(Node start, Node end) {
        // Initialize sets of visited and unvisited nodes
        Set<Node> visited = new HashSet<>();
        Set<Node> unvisited = new HashSet<>();
        unvisited.add(start);

        // Initialize maximum path-value for each node
        start.maxPathValue = start.value;
        Map<Node, Node> parents = new HashMap<>();
        for (Node node : start.neighbors) {
            node.maxPathValue = Math.min(start.maxPathValue, node.value);
            parents.put(node, start);
            unvisited.add(node);
        }

        // Dijkstra's algorithm to find maximum path-value and path
        while (!unvisited.isEmpty()) {
            Node current = null;
            int maxPathValue = Integer.MIN_VALUE;
            for (Node node : unvisited) {
                if (node.maxPathValue > maxPathValue) {
                    current = node;
                    maxPathValue = node.maxPathValue;
                }
            }
            if (current == null) {
                // No path from start to end
                return null;
            }
            unvisited.remove(current);
            visited.add(current);
            if (current == end) {
                // Found path from start to end
                List<Node> path = new ArrayList<>();
                while (current != start) {
                    path.add(current);
                    current = parents.get(current);
                }
                path.add(start);
                Collections.reverse(path);
                return path;
            }
            for (Node neighbor : current.neighbors) {
                if (!visited.contains(neighbor)) {
                    int potentialMaxPathValue = Math.min(current.maxPathValue, neighbor.value);
                    if (potentialMaxPathValue > neighbor.maxPathValue) {
                        neighbor.maxPathValue = potentialMaxPathValue;
                        parents.put(neighbor, current);
                        unvisited.add(neighbor);
                    }
                }
            }
        }
        // No path from start to end
        return null;
    }

    public static void main(String[] args) {
        // Example graph:
        //   2
        //  / \
        // 1   3
        //  \ /
        //   4
        Node A = new Node(1);
        Node B = new Node(2);
        Node C = new Node(3);
        Node D = new Node(4);
        A.neighbors.add(B);
        A.neighbors.add(D);
        B.neighbors.add(A);
        B.neighbors.add(C);
        C.neighbors.add(B);
        C.neighbors.add(D);
        D.neighbors.add(A);
        D.neighbors.add(C);

        List<Node> maxPath = getMaxPath(A, D);
        if (maxPath == null) {
            System.out.println("No path from A to D");
        } else {
            System.out.print("Path with maximum path-value from A to D: ");
            for (Node node : maxPath) {
                System.out.print(node.value + " ");
            }
            System.out.println(); // Expected output: 1 4
        }

        maxPath = getMaxPath(B, D);
        if (maxPath == null) {
            System.out.println("No path from B to D");
        } else {
            System.out.print("Path with maximum path-value from B to D: ");
            for (Node node : maxPath) {
                System.out.print(node.value + " ");
            }
            System.out.println(); // Expected output: 2 3 4
        }
    }
}
