function addFavoritos(tuitID) {

	$.ajax({
		type : 'POST',
		contentType : 'application/json;',
		url : 'http://' + location.host + '/TuitterFeeder/rest/favoritos/add/' 
				 + $("#username").val() +'/' + tuitID ,
		success : function(msg) {
			console.log('success');
		},
		error : function(xhr, ajaxOptions, thrownError) {
			console.log(xhr.status);
			console.log(thrownError);
		}
	});
}

function listFavoritos() {

	var usr = $("#username").val();
	$.getJSON(
			'http://' + location.host + '/TuitterFeeder/rest/favoritos/list/' + usr,
			function() {
			}).success(function(data) {

				$('#content').empty();
				
				var items = [];
				$.each(data, function(key, val) {
					
					var jsonResp = $.parseJSON(data[key]);
					printTuit(jsonResp,true);
					
				});

		
			}).error(function(jqXHR, textStatus, errorThrown) {
				console.log("error " + textStatus);
				console.log("incoming Text " + jqXHR.responseText);
			})

}