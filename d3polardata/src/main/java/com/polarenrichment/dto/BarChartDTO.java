package com.polarenrichment.dto;

public class BarChartDTO {
	private String mimeType;
	private double score;
	private int count;
	
	public BarChartDTO(){
		
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}	
}
