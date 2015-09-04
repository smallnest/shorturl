
(function(){
	$("#btn_gen").click(function(){
		generate_shorturl();
	})
	
	$("#input_url").keyup(function(){
		$("#result_panel").fadeOut();		
	})
	
	function generate_shorturl() {
		var input_url = $("#input_url").val();
		if (input_url !== null && input_url !== undefined && input_url !== '') {
			if (input_url.indexOf("http://") == 0 || input_url.indexOf("https://") == 0 ) {
				$.ajax({
					  method: "POST",
					  url: "/",
					  data: input_url
					})
					  .done(function( msg ) {
						$("#shorturl_result").attr("href", msg)
						$("#shorturl_result").text(msg)
						$("#result_panel").fadeIn();						
					  });
			}
				
		}
	}
})();