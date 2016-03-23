package com.moments.web.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.moments.model.Album;
import com.moments.model.Photo;
import com.moments.model.User;
import com.moments.service.Service;
import com.moments.service.ValidationService;

@RequestMapping("/user")
@Controller
public class UserController {

	@Autowired
	private Service service;

	HttpSession session;

	@RequestMapping(value = "/album/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<List<String>> saveAlbum(@RequestBody Album album,
			HttpSession session) {
		User user = service
				.getUser(session.getAttribute("username").toString());
		List<String> responseList = null;
		List<String> errorList = ValidationService.validateAlbumName(service,
				album, user);
		HttpStatus status = null;
		if (errorList.size() > 0) {
			responseList = errorList;
			status = HttpStatus.CONFLICT;
		} else {
			responseList = new ArrayList<String>();
			if (service.createAlbum(user, album)) {
				responseList.add("Album Created");
				status = HttpStatus.CREATED;
			} else {
				responseList.add("Error Creating Album");
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
		return new ResponseEntity<List<String>>(responseList, status);
	}

	@RequestMapping(value = "/album/delete", params = { "album_id" }, produces = "application/json")
	public ResponseEntity<Void> deleteAlbum(HttpSession session,
			HttpServletRequest req) {
		int album_id = Integer.parseInt(req.getParameter("album_id"));
		String album_to_delete = session.getAttribute("username").toString()
				+ "/" + service.getAlbum(album_id).getAlbum_name();
		User user = service
				.getUser(session.getAttribute("username").toString());
		boolean album_deleted = service.deleteAlbum(album_to_delete, album_id,
				user);
		if (album_deleted)
			return new ResponseEntity<Void>(HttpStatus.OK);
		else
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(value = "/albums", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Album>> getAlbums(HttpSession session) {

		int user_id = service.getUser(
				session.getAttribute("username").toString()).getUser_id();
		List<Album> albums = service.getAlbums(user_id);
		HttpStatus returnStatus = null;
		System.out.println(albums);
		if (albums != null)
			returnStatus = HttpStatus.OK;
		else
			returnStatus = HttpStatus.NO_CONTENT;

		return new ResponseEntity<List<Album>>(albums, returnStatus);
	}

	@RequestMapping(value = "/album", params = { "album_id", "call" }, method = RequestMethod.GET)
	public ResponseEntity<List<Photo>> getPhotos(HttpServletRequest req,
			HttpSession session) {
		try {
			User user = service.getUser(session.getAttribute("username")
					.toString());
			int album_id = Integer.parseInt(req.getParameter("album_id"));
			int call = Integer.parseInt(req.getParameter("call"));
			ResponseEntity<List<Photo>> returnEntity = new ResponseEntity<List<Photo>>(
					service.getPhotos(album_id, user, call), HttpStatus.OK);
			return returnEntity;
		} catch (NumberFormatException e) {
			return new ResponseEntity<List<Photo>>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/upload", params = { "album_id" }, method = RequestMethod.POST)
	public ResponseEntity<Void> uploadFile(MultipartHttpServletRequest request,
			HttpSession session) {
		int album_id = Integer.parseInt(request.getParameter("album_id"));
		String username = session.getAttribute("username").toString();
		User user = service.getUser(username);
		Album album = service.getAlbum(album_id);

		Iterator<String> itr = request.getFileNames();

		MultipartFile file = request.getFile(itr.next());

		if (service.uploadImage(album, user, file)) {
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/album/download/{album_name}", produces = "text/plain")
	public ResponseEntity<String> downloadAlbumZip(
			@PathVariable String album_name) {
		String download_folder = session.getAttribute("username").toString()
				+ "/" + album_name;

		String url = service.downloadAlbum(download_folder);
		if (url != null) {
			return new ResponseEntity<String>(url, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Error", HttpStatus.NOT_FOUND);
		}
	}

}