/*
 * Copyright 2016 Sebastian Schmidl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sas_systems.unveiled.server;

import java.util.Date;

/**
 * File POJO class. Represents a database entity.
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
public class FilePOJO {
	
	private int owner_id;
	private String caption;
	private String filename;
	private String file_url;
	private String thumbnail_url;
	private String mediatype;
	private Date uploaded_at;
	private long size;
	private double lat;
	private double lng;
	private boolean isPublic;
	private boolean isVerified;
	private int length;
	private int heigth;
	private int width;
	private String resolution;
	
	/**
	 * 
	 * @param owner_id foreign key
	 * @param caption
	 * @param filename with suffix
	 * @param file_url absolute
	 * @param thumbnail_url absolute
	 * @param mediatype MIME type
	 * @param uploaded_at Date object -> will be converted in UNIX timestamp (in seconds)
	 * @param size byte
	 * @param lat
	 * @param lng
	 * @param isPublic
	 * @param isVerified
	 * @param length in seconds
	 * @param heigth
	 * @param width
	 * @param resolution eg. 1080, 1440, 2160, 4320
	 */
	public FilePOJO(int owner_id, String caption, String filename, String file_url, String thumbnail_url,
			String mediatype, Date uploaded_at, long size, double lat, double lng, boolean isPublic, boolean isVerified,
			int length, int heigth, int width, String resolution) {
		super();
		this.owner_id = owner_id;
		this.caption = caption;
		this.filename = filename;
		this.file_url = file_url;
		this.thumbnail_url = thumbnail_url;
		this.mediatype = mediatype;
		this.uploaded_at = uploaded_at;
		this.size = size;
		this.lat = lat;
		this.lng = lng;
		this.isPublic = isPublic;
		this.isVerified = isVerified;
		this.length = length;
		this.heigth = heigth;
		this.width = width;
		this.resolution = resolution;
	}
	
	public int getOwner_id() {
		return owner_id;
	}
	public void setOwner_id(int owner_id) {
		this.owner_id = owner_id;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFile_url() {
		return file_url;
	}
	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}
	public String getThumbnail_url() {
		return thumbnail_url;
	}
	public void setThumbnail_url(String thumbnail_url) {
		this.thumbnail_url = thumbnail_url;
	}
	public String getMediatype() {
		return mediatype;
	}
	public void setMediatype(String mediatype) {
		this.mediatype = mediatype;
	}
	public Date getUploaded_at() {
		return uploaded_at;
	}
	public void setUploaded_at(Date uploaded_at) {
		this.uploaded_at = uploaded_at;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public boolean isVerified() {
		return isVerified;
	}
	public void setVerified(boolean isVerfied) {
		this.isVerified = isVerfied;
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
