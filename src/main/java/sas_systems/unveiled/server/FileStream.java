package sas_systems.unveiled.server;

import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;

@Stateful
public class FileStream {
	
	private final int id = new Random().nextInt(100);
	private long ssrc;
	private String filename;
	private String filetype;
	private String author;
	private DataToFileWriter dataHandler;
	
	@EJB
	private SessionManager sm;

	public FileStream() {
		
	}
	
	public void init(long ssrc) {
		this.ssrc = ssrc;
		
		dataHandler = new DataToFileWriter(ssrc, sm.getPayloadType(), sm.getMediaLocation());
		sm.addDataListener(this.dataHandler);
	}
	
	@Remove
	public void remove() {
		sm.removeDataListener(dataHandler);
		if(!dataHandler.closeFile()) {
			System.err.println("File was not completely written.");
		}
	}

	public int getId() {
		return id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
