package org.rit.classmap.directions;

public class Vertex implements Comparable<Vertex> {

	final private String id; 
	public Vertex previous;
	public double minDistance = Double.POSITIVE_INFINITY;
	public double lat; 
	public double lng;
	public Vertex(String id)
	{
		this.id = id;
	}
	
	@Override
	public int compareTo(Vertex other) {
		return Double.compare(minDistance, other.minDistance);
	}
	@Override
	public String toString() {
		return id;
	}
	

}
