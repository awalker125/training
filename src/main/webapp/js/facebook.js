function statusChangeCallback(response) {
	console.log('statusChangeCallback');
	console.log(response);
	// The response object is returned with a status field that lets the
	// app know the current login status of the person.
	// Full docs on the response object can be found in the documentation
	// for FB.getLoginStatus().
	if (response.status === 'connected') {
		// Logged into your app and Facebook.
		// testAPI();
		console.log('connected');
		console.log(response.authResponse.accessToken);

	} else if (response.status === 'not_authorized') {
		console.log('not_authorized');
		// The person is logged into Facebook, but not your app.
		document.getElementById('status').innerHTML = 'Please log '
				+ 'into this app.';
	} else {
		console.log('other');
		// The person is not logged into Facebook, so we're not sure if
		// they are logged into this app or not.
		document.getElementById('status').innerHTML = 'Please log '
				+ 'into Facebook.';
	}
}

function customLoginHandler() {
	alert("logged in")
	console.info("doing login")
	FB.getLoginStatus(function(response) {
		statusChangeCallback(response);
	});
	console.info("done login")
}

function getUserDetails() {

	FB.api('/me', function(response) {
		console.log(JSON.stringify(response));
	});

}

(function(d, s, id) {
	var js, fjs = d.getElementsByTagName(s)[0];
	if (d.getElementById(id))
		return;
	js = d.createElement(s);
	js.id = id;
	js.src = "//connect.facebook.net/en_GB/sdk.js#xfbml=1&version=v2.5&appId=224674001213541";
	fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));


console.info("loaded facebook.js")

