package com.moments.Dao;

import java.util.List;

import com.moments.model.Album;
import com.moments.model.Photo;
import com.moments.model.User;

public interface PhotoDao {
	
	public void save(Photo photo);
	
	public void update();
	
	public void delete();
	
	public void details();
	
	public int getTotalPhotos(User user);
	
	public boolean isRegistered(int album_id);
	
	List<Photo> getPhotos(Album album, User user, int call);

	List<Photo> getPhotos(Album album, int call);
}