package com.polarenrichment.dto;

public class WordCloudDTO {
	private String text;
	private int freq;
	
	public WordCloudDTO(){
	}
	
	public WordCloudDTO(String text, int freq) {
		this.text = text;
		this.freq = freq;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}	
}
