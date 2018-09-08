package com.github.glowingpotato.fastredstone.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Path {
	private DAG dag;
	private Vertex from;
	private Vertex to;
	private Collection<Edge> edges;

	public DAG getDag() {
		return dag;
	}

	public Vertex getFromVertex() {
		return from;
	}

	public Vertex getToVertex() {
		return to;
	}

	public Collection<Edge> getEdges() {
		return Collections.unmodifiableCollection(edges);
	}

	static boolean tryCreate(Vertex from, Vertex to, List<Edge> edges, Set<Vertex> triedVertices) {
		for (Edge edge : from.getSourcedEdges()) {
			edges.add(edge);
			Vertex sink = edge.getSink();
			if (edge.getSink() == sink) {
				return true;
			} else if (triedVertices.contains(sink)) {
				return false;
			} else if (tryCreate(edge.getSink(), to, edges, triedVertices)) {
				return true;
			}
			edges.remove(edge);
		}
		return false;
	}

	public Path(Vertex from, Vertex to) {
		dag = from.getDag();
		if (to.getDag() != dag) {
			throw new IllegalArgumentException("A path cannot connect two vertices on different graphs.");
		}
		List<Edge> edges = new ArrayList<>();
		if (!tryCreate(from, to, edges, new HashSet<>())) {
			throw new GraphSolveException("A path between the two vertices does not exist.");
		}
		this.from = from;
		this.to = to;
		this.edges = edges;
	}
}