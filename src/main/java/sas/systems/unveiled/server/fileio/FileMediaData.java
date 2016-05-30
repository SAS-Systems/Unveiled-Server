package sas.systems.unveiled.server.fileio;

public class FileMediaData {

	private int length;
	private int heigth;
	private int width;
	private String resolution;
	
	public FileMediaData(int length, int heigth, int width, String resolution) {
		super();
		this.length = length;
		this.heigth = heigth;
		this.width = width;
		this.resolution = resolution;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getHeigth() {
		return heigth;
	}

	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	
}
