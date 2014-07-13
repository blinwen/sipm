package com.douziapp.exam.data;

public class BankItem {

	private		String		name;
	private 	int			sizeNet;
	private 	int			versionNet;
	
	private 	int			versionLocal;

	private		String		cnName;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		
		name = name.replaceFirst("\\.db", "");

		this.name = name;
	}

	public int getVersionNet() {
		return versionNet;
	}

	public void setVersionNet(int versionNet) {
		this.versionNet = versionNet;
	}

	public int getVersionLocal() {
		return versionLocal;
	}

	public void setVersionLocal(int versionLocal) {
		this.versionLocal = versionLocal;
	}

	public int getSizeNet() {
		return sizeNet;
	}

	public void setSizeNet(int sizeNet) {
		this.sizeNet = sizeNet;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
}
