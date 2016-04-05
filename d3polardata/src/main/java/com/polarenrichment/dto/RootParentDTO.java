package com.polarenrichment.dto;

import java.util.List;

public class RootParentDTO {
	private String name;
	private List<ParentDTO> children;
	
	public RootParentDTO(){
	}
	public RootParentDTO(String name, List<ParentDTO> children) {
		this.name = name;
		this.children = children;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ParentDTO> getChildren() {
		return children;
	}

	public void setChildren(List<ParentDTO> children) {
		this.children = children;
	}	
}
