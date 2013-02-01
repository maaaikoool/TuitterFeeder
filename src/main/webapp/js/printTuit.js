function printTuit(json, favorito) {
			
	var row = $(document.createElement('div')).addClass('row-fluid');
	var pic =  $(document.createElement('img')).attr('src',json.user.profile_image_url);
	var div0 = $(document.createElement('div')).addClass('span1').addClass('thumbnail').append(pic);
	var div1 = $(document.createElement('div')).addClass('span4').text('@' + json.user.screen_name);
	var div2= $(document.createElement('div')).addClass('span6').text(json.created_at);
	var div3= $(document.createElement('div')).addClass('span1');
	
	var filter = document.getElementById('filter').value;
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
	
	div3.append(icon);
	
	var row2 = $(document.createElement('div')).addClass('row-fluid');
	
	var newtext = json.text.replace(filter, '<strong>'+filter+'</strong>');
	var divtext = $(document.createElement('div')).addClass('span12').html(newtext);
	
	row.append(div0).append(div1).append(div2).append(div3).append(row2).append(divtext);
	$('#content').prepend(row).fadeIn('slow');
			
}