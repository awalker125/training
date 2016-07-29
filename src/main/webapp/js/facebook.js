function statusChangeCallback(response) {
	console.info('statusChangeCallback');
	console.info(response);
	// The response object is returned with a status field that lets the
	// app know the current login status of the person.
	// Full docs on the response object can be found in the documentation
	// for FB.getLoginStatus().
	if (response.status === 'connected') {
		// Logged into your app and Facebook.

		console.info('connected');
		console.info(response.authResponse.accessToken);
		getUserDetails();

	} else if (response.status === 'not_authorized') {
		console.info('not_authorized');
		// The person is logged into Facebook, but not your app.
		document.getElementById('status').innerHTML = 'Please log '
				+ 'into this app.';
	} else {
		console.info('other');
		// The person is not logged into Facebook, so we're not sure if
		// they are logged into this app or not.
		document.getElementById('status').innerHTML = 'Please log '
				+ 'into Facebook.';
	}
}

function customLoginHandler() {
	alert("customLoginHandler")
	console.info("doing login")
	FB.getLoginStatus(function(response) {
		statusChangeCallback(response);
	});
	console.info("done login")
}

function getUserDetails() {

	var credentials = {};

	FB.api('/me?fields=email', function(response) {
		console.info("userDetails")
		console.info(JSON.stringify(response));

		credentials.username = response.email;
		credentials.accesscode = response.id;
	});

	$(document).ajaxSend(function(e, xhr, options) {
		xhr.setRequestHeader("username", credentials.username);
		xhr.setRequestHeader("api_key", credentials.accesscode);
	});

	Cookies.set('username', credentials.username, {
		secure : true
	});
	Cookies.set('api_key', credentials.accesscode, {
		secure : true
	});

	return credentials;
}

window.fbAsyncInit = function() {
	FB.init({
		appId : '224674001213541',
		cookie : true, // enable cookies to allow the server to access
		// the session
		xfbml : true, // parse social plugins on this page
		version : 'v2.5' // use graph api version 2.5
	});

	// Now that we've initialized the JavaScript SDK, we call
	// FB.getLoginStatus(). This function gets the state of the
	// person visiting this page and can return one of three states to
	// the callback you provide. They can be:
	//
	// 1. Logged into your app ('connected')
	// 2. Logged into Facebook, but not your app ('not_authorized')
	// 3. Not logged into Facebook and can't tell if they are logged into
	// your app or not.
	//
	// These three cases are handled in the callback function.

	// Lets check if we are logged in and then do something depending on if we
	// are.
	FB.getLoginStatus(function(response) {
		statusChangeCallback(response);
	});

};

(function(d, s, id) {
	var js, fjs = d.getElementsByTagName(s)[0];
	if (d.getElementById(id))
		return;
	js = d.createElement(s);
	js.id = id;
	js.src = "//connect.facebook.net/en_GB/sdk.js";
	fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

console.info("loaded facebook.js")
