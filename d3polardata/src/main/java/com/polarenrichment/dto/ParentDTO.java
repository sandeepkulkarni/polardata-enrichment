package com.polarenrichment.dto;

import java.util.List;

public class ParentDTO {
	private String name;
	private List<ChildDTO> children;
	public ParentDTO(){
	}
	public ParentDTO(String name, List<ChildDTO> children) {
		this.name = name;
		this.children = children;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ChildDTO> getChildren() {
		return children;
	}
	public void setChildren(List<ChildDTO> children) {
		this.children = children;
	}
	
}
