$(document).ready(function() {

	// jQuery methods go here...

	update_userdetails()

});

function update_userdetails() {

	var username = Cookies.get('username', {
		secure : true
	});
	var api_key = Cookies.get('api_key', {
		secure : true
	});

	$('#usernameTxt').html(username)
	$('#apiKeyTxt').html(api_key)

}
