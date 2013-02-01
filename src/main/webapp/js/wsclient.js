var wsclient = (function() {

	var ws = null;

	function connect() {

		var wsURI = 'ws://' + location.host + '/TuitterFeeder/tuit?filter=' + document.getElementById('filter').value;
		ws = $.gracefulWebSocket(wsURI);
		

		ws.onopen = function() {
			$("#buttonSC").attr('onclick', 'wsclient.disconnect()');
			$("#buttonSC").html('Stop');
		};
		ws.onmessage = function(evt) {
			writeToScreen(evt.data);
		};

		ws.onclose = function() {
			
		    $("#buttonSC").attr('onclick', 'wsclient.connect()');
			$("#buttonSC").html('Start');
		};
		
		ws.onerror = function(evt) {
			$(document.createElement('div')).addClass('text-error').text(evt.data).prependTo('#content');
		};

		function writeToScreen(message) {
			var n=message.search('"id_str":"')+10;
			var id = message.substring(n,n+18);
			var jsonResp = $.parseJSON(message);
			printTuit(jsonResp,false);
		}

	}

	function disconnect() {
		
		if (ws != null) {
			ws.close();
			ws = null;
		}
	}

	// metodos publicos
	return {
		connect : connect,
		disconnect : disconnect
	};
})();