package com.polarenrichment.dto;

public class LineChartDTO {
	private String unit;
	private int count;
	
	public LineChartDTO(){}
	public LineChartDTO(String unit, int count) {
		this.unit = unit;
		this.count = count;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
