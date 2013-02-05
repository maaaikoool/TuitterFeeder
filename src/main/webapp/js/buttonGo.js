var buttonGo = (function() {

	var start = true;

	function go()
	{
		if(start == true)
		{
			connect();
		}
		else
		{
			disconnect();
		}
	}
	
	function connect() {
		
		if( $('#filter').val() != "" && start)
		{
			start = false;
			$('#content').empty();
			wsclient.connect();
			$("#buttonSC").html('Stop');
			$("#buttonSC").toggleClass("btn-danger btn-success");
			$("#filter").prop('disabled', true);
		}
		
	}

	function disconnect() {
		
		start = true;
		wsclient.disconnect();
		$("#buttonSC").html('Start');
		$("#buttonSC").toggleClass("btn-success btn-danger");
		$("#filter").prop('disabled', false);
	}

	// metodos publicos
	return {
		go : go
	};
})();