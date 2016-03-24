var App = angular.module('myApp', []);

App.directive('ngFiles', [ '$parse', function($parse) {

	function fn_link(scope, element, attrs) {
		var onChange = $parse(attrs.ngFiles);
		element.on('change', function(event) {
			onChange(scope, {
				$files : event.target.files
			});
		});
	}
	;

	return {
		link : fn_link
	}
} ]);

App.factory('AlbumService',
		[
				'$http',
				'$q',
				function($http, $q) {
					return {
						createAlbum : function(album) {
							return $http.post("/moments/user/album/create",
									album).success(function(data, status) {
								console.log(data);
								console.log(status);
								return data;
							}).error(function(data, status) {
								console.log(data);
								console.log(status);
								console.error('Error while creating album');
								return data;
							});
						},
						getAlbum : function(album_name, call) {
							console.log("album name: " + album_name);
							console.log("album call: " + call);
							return $http.get("/moments/user/album", {
								params : {
									album_name : album_name,
									call : call
								}
							}).success(function(response) {
								return response;
							}).error(function(data, status) {
								console.error('Error while getting album');
								return data;
							});
						},
						getAlbums : function() {
							return $http.get("/moments/user/albums").success(
									function(responseData, status) {
										return responseData;
									}).error(function(data, status) {
								console.error("Error while getting albums");
								return data;
							});
						},

						uploadFile : function(album_name, formData) {
							var url = '/moments/user/upload?album_name='
									+ album_name;
							return $http.post(url, formData, {
								transformRequest : angular.identity,
								headers : {
									'Content-Type' : undefined
								}
							}).success(function(data, status) {
								console.log(status);
								return status;
							}).error(function(data, status) {
								console.log(status);
								return status;
							});
						},

						deleteAlbum : function(album_name) {
							var url = '/moments/user/album/delete?album_name='
									+ album_name;
							return $http.get(url).success(
									function(responseData, status) {
										return responseData;
									}).error(function(data, status) {
								console.error("Error while deleting album");
								return data;
							});
						},

						downloadAlbum : function(album_name) {
							var url = '/moments/user/album/download/'
									+ album_name;
							console.log(url);
							return $http.get(url).success(
									function(responseData, status) {
										return responseData;
									}).error(function(data, status) {
								console.error("Error while downloading album");
								return data;
							});
						}, 
						
						createAlbumLink : function(album_name) {
							var url = '/moments/user/album/createlink/'
									+ album_name;
							console.log(url);
							return $http.get(url).success(
									function(responseData, status) {
										return responseData;
									}).error(function(data, status) {
								console.error("Error creating link");
								return data;
							});
						}
					}
				} ]);