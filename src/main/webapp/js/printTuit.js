function printTuit(json) {
	
	var favorito = json.favorito != undefined;
	
	var cnt = $(document.createElement('div')).addClass('container');
	var rw = $(document.createElement('div')).addClass('row-fluid');
	var body = $(document.createElement('div')).addClass('span11');
	
	var img =  $(document.createElement('img')).attr('src',json.user.profile_image_url);
	var profilepic = $(document.createElement('div')).addClass('span1').addClass('thumbnail').append(img);
	
	var divInfo = $(document.createElement('div')).addClass('span4').text('@' + json.user.screen_name +" at "+ json.created_at.substring(0,19));
	var divFav = $(document.createElement('div')).addClass('span1 offset5');
	
	var filter = $("#filter").val();
	var icon = $(document.createElement('i'));
	
	if(favorito==false)
	{
		icon.addClass('icon-star-empty').on('click',function(){
		    $(this).toggleClass("icon-star-empty icon-star");
		    addFavoritos(json.id_str);	
		});
	}
	else
	{
		filter=json.filter;
		icon.addClass('icon-star');	
	}
	
	divFav.append(icon);
	
	var newrow = $(document.createElement('div')).addClass('row-fluid'); 
	var newtext = highlight(json.text, filter);
	var divText = $(document.createElement('div')).addClass('span12').html(newtext);
	
	body.append(divInfo).append(divFav).append(newrow).append(divText);
	
	cnt.append(rw);
	rw.append(profilepic);
	rw.append(body);
	
	$('#content').prepend(cnt).fadeIn('slow');
	

	
			
}
function preg_quote( str ) {
    // http://kevin.vanzonneveld.net
    // +   original by: booeyOH
    // +   improved by: Ates Goral (http://magnetiq.com)
    // +   improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +   bugfixed by: Onno Marsman
    // *     example 1: preg_quote("$40");
    // *     returns 1: '\$40'
    // *     example 2: preg_quote("*RRRING* Hello?");
    // *     returns 2: '\*RRRING\* Hello\?'
    // *     example 3: preg_quote("\\.+*?[^]$(){}=!<>|:");
    // *     returns 3: '\\\.\+\*\?\[\^\]\$\(\)\{\}\=\!\<\>\|\:'

    return (str+'').replace(/([\\\.\+\*\?\[\^\]\$\(\)\{\}\=\!\<\>\|\:])/g, "\\$1");
}
function highlight( data, search )
{
    return data.replace( new RegExp( "(" + preg_quote( search ) + ")" , 'gi' ), "<b>$1</b>" );
}