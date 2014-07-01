package com.douziapp.exam.data;

public class SingleChoice {

	private String	selItemA;
	private String	selItemB;
	private String	selItemC;
	private String	selItemD;
	
	private long	vID;
	
	private String	vContent;
	
	private String	aContent;
	
	public String getaContent() {
		return aContent;
	}

	public void setaContent(String aContent) {
		this.aContent = aContent;
	}

	private long	rightItem;
	
	private long	id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSelItemA() {
		return selItemA;
	}

	public void setSelItemA(String selItemA) {
		this.selItemA = selItemA;
	}

	public String getSelItemB() {
		return selItemB;
	}

	public void setSelItemB(String selItemB) {
		this.selItemB = selItemB;
	}

	public String getSelItemC() {
		return selItemC;
	}

	public void setSelItemC(String selItemC) {
		this.selItemC = selItemC;
	}

	public String getSelItemD() {
		return selItemD;
	}

	public void setSelItemD(String selItemD) {
		this.selItemD = selItemD;
	}

	public long getvID() {
		return vID;
	}

	public void setvID(long vID) {
		this.vID = vID;
	}

	public String getvContent() {
		return vContent;
	}

	public void setvContent(String vContent) {
		this.vContent = vContent;
	}

	public long getRightItem() {
		return rightItem;
	}

	public void setRightItem(long rightItem) {
		this.rightItem = rightItem;
	}
	
	
}
