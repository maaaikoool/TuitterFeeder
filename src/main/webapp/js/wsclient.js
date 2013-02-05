var wsclient = (function() {

	var ws = null;

	function connect() {

		var options = { 
				fallbackSendURL : 'http://' + location.host + '/TuitterFeeder/tuitfallback',
				fallbackPollURL : 'http://' + location.host + '/TuitterFeeder/tuitfallback',
				fallbackPollParams : {}
		};
		
		options.fallbackPollParams.user=$("#username").val();
		var wsURI = 'ws://' + location.host + '/TuitterFeeder/tuit?filter=' + $("#filter").val()
				+ '&user='+ $("#username").val()+"'";
		ws = $.gracefulWebSocket(wsURI,options);
		

		ws.onopen = function() {
			
			var opt = {}
			opt.user = $("#username").val();
			opt.filter =  $("#filter").val();
			ws.send(opt);
		};
		ws.onmessage = function(evt) {
			if(evt.data.length > 0)
			{
				writeToScreen(evt.data);
			}
		};

		ws.onclose = function() {

			var opt = {}
			opt.user = $("#username").val();
			opt.stop = "true";
			if(ws != null)
			{
				ws.send(opt);
			}
		};
		
		ws.onerror = function(evt) {
			$(document.createElement('div')).addClass('text-error').text(evt.data).prependTo('#content');
		};

		function writeToScreen(message) {
			
			var jsonResp = $.parseJSON(message);
			
			if(jsonResp.id_str != undefined) // Es un tuit (1 json)
			{
				printTuit(jsonResp);
			}
			
			else // es un array de tuits
			{
				$.each(jsonResp, function(key, val) {
					var aux = $.parseJSON(jsonResp[key]);
					printTuit(aux);
				});
			}
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