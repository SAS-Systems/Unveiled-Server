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
package sas.systems.unveiled.server.fileio;

import java.util.Date;

/**
 * File POJO class. Represents a database entity.
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
public class FilePOJO {
	
	private int ownerId;
	private String caption;
	private String filename;
	private String fileUrl;
	private String thumbnailUrl;
	private String mediatype;
	private Date uploadedAt;
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
	 * @param ownerId foreign key
	 * @param caption
	 * @param filename with suffix
	 * @param fileUrl absolute
	 * @param thumbnailUrl absolute
	 * @param mediatype MIME type
	 * @param uploadedAt Date object -> will be converted in UNIX timestamp (in seconds)
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
	public FilePOJO(int ownerId, String caption, String filename, String fileUrl, String thumbnailUrl,
			String mediatype, Date uploadedAt, long size, double lat, double lng, boolean isPublic, boolean isVerified,
			int length, int heigth, int width, String resolution) {
		super();
		this.ownerId = ownerId;
		this.caption = caption;
		this.filename = filename;
		this.fileUrl = fileUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.mediatype = mediatype;
		this.uploadedAt = uploadedAt;
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
	
	public int getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
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
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getMediatype() {
		return mediatype;
	}
	public void setMediatype(String mediatype) {
		this.mediatype = mediatype;
	}
	public Date getUploadedAt() {
		return uploadedAt;
	}
	public void setUploadedAt(Date uploadedAt) {
		this.uploadedAt = uploadedAt;
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
