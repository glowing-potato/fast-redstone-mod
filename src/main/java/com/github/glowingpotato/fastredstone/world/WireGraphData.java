package com.github.glowingpotato.fastredstone.world;

import java.util.HashMap;

import com.github.glowingpotato.fastredstone.FastRedstoneMod;
import com.github.glowingpotato.fastredstone.graph.DAG;
import com.github.glowingpotato.fastredstone.graph.Edge;
import com.github.glowingpotato.fastredstone.graph.Vertex;
import com.github.glowingpotato.fastredstone.util.KeyValuePair;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class WireGraphData extends WorldSavedData {

	private static final String DATA_NAME = FastRedstoneMod.MODID + "_WireGraphData";
	private DAG graph;

	/**
	 * Boolean isSource
	 */
	private HashMap<Vertex, KeyValuePair<BlockPos, Boolean>> vertexMapping;

	public WireGraphData() {
		super(DATA_NAME);
	}

	public HashMap<Vertex, KeyValuePair<BlockPos, Boolean>> getVertexMapping() {
		return vertexMapping;
	}

	public DAG getGraph() {
		return graph;
	}

	@Override
	public void readFromNBT(NBTTagCompound arg0) {

		DAG dag = new DAG();

		int vertexCount = arg0.getInteger("vertexCount");
		int[] edges = arg0.getIntArray("edgeMap");
		Vertex[] vertices = new Vertex[vertexCount];

		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = dag.createVertex();

		}

		for (int i = 0; i < edges.length; i += 2) {
			dag.createEdge(vertices[edges[i]], vertices[edges[i + 1]]);
		}

		graph = dag;

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound arg0) {
		NBTTagCompound root = new NBTTagCompound();
		arg0.setTag("graph", root);

		arg0.setInteger("vertexCount", graph.getVertices().size());

		int[] edges = new int[graph.getEdges().size() * 2];
		HashMap<Vertex, Integer> map = new HashMap<Vertex, Integer>();

		int index = 0;
		for (Vertex v : graph.getVertices()) {
			map.put(v, index++);
		}

		index = 0;
		for (Edge e : graph.getEdges()) {
			edges[index++] = map.get(e.getSource());
			edges[index++] = map.get(e.getSink());
		}

		arg0.setIntArray("edgeMap", edges);

		return root;
	}

	public static WireGraphData get(World world) {
		MapStorage storage = world.getMapStorage();
		WireGraphData instance = (WireGraphData) storage.getOrLoadData(WireGraphData.class, DATA_NAME);

		if (instance == null) {
			instance = new WireGraphData();
			storage.setData(DATA_NAME, instance);
		}
		return instance;
	}

}
