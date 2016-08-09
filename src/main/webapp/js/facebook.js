$(document).ready(function() {

	// jQuery methods go here...
	// event handlers go here
	console.info("jquery document ready")

	// Check we have local storage.

	if (lsTest() === true) {
		// available
		console.info("We have local storage")

	} else {
		console.error("We need web storage and its disabled...")
		// Sorry! No Web Storage support..
	}

	updateLoggedInUser()

	$.ajaxSetup({
		cache : true
	});
	$.getScript('//connect.facebook.net/en_GB/sdk.js', function() {
		FB.init({
			appId : '224674001213541',
			version : 'v2.7', // or v2.1, v2.2, v2.3, ...
			cookie : true,
			xfbml : true
		});
		// $('#loginbutton,#feedbutton').removeAttr('disabled');
		FB.getLoginStatus(updateStatusCallback);
	});

});

function lsTest() {
	var test = 'test';
	try {
		localStorage.setItem(test, test);
		localStorage.removeItem(test);
		return true;
	} catch (e) {
		return false;
	}
}

function updateLoggedInUser(credentials) {

	var accessToken = sessionStorage.getItem("accessToken");

	if (accessToken) {
		$('#accessTokenTxt').html(accessToken)
	} else {
		$('#accessTokenTxt').html("not set")
	}

	var email = sessionStorage.getItem("email");

	if (email) {
		$('#emailTxt').html(email)
	} else {
		$('#emailTxt').html("not set")
	}

	var name = sessionStorage.getItem("name");

	if (name) {
		$('#nameTxt').html(name)
	} else {
		$('#nameTxt').html("not set")
	}

}

function updateStatusCallback(response) {
	console.info('updateStatusCallback');
	console.info(response);
	// The response object is returned with a status field that lets the
	// app know the current login status of the person.
	// Full docs on the response object can be found in the documentation
	// for FB.getLoginStatus().
	if (response.status === 'connected') {
		// Logged into your app and Facebook.

		console.info('connected');
		console.info(response.authResponse.accessToken);
		// Update our credentials in session storage
		sessionStorage.setItem("accessToken", response.authResponse.accessToken);

		updateLoggedInUser();

		// This is an async call so might take a while to finish
		FB.api('/me?fields=email,name', function(response) {
			console.info("userDetails")
			console.info(JSON.stringify(response));

			sessionStorage.setItem("email", response.email);
			sessionStorage.setItem("name", response.name);
			updateLoggedInUser();

		});

		// return credentials

	} else if (response.status === 'not_authorized') {
		console.info('not_authorized');
		// The person is logged into Facebook, but not your app.
		//document.getElementById('status').innerHTML = 'Please log '
		//		+ 'into this app.';
	} else {
		console.info('other');
		// The person is not logged into Facebook, so we're not sure if
		// they are logged into this app or not.
		//document.getElementById('status').innerHTML = 'Please log '
		//		+ 'into Facebook.';
	}
}

function facebookLoginHandler() {
	// alert("customLoginHandler")
	console.info("doing login")
	FB.getLoginStatus(function(response) {
		updateStatusCallback(response);
	});
	console.info("done login")
	// update_userdetails()
	
	//displaySuccess("login successful")
}


function displaySuccess(message)
{
	$('#successDialog').html(message)
	$('#successDialog').show()
}


//function getUserDetails() {
//
//	FB.api('/me?fields=email', function(response) {
//		console.info("userDetails")
//		console.info(JSON.stringify(response));
//
//		credentials.username = response.email;
//		credentials.accesscode = response.id;
//
//		console.info("credentials")
//		console.info(JSON.stringify(credentials));
//	});
//
//	$('#usernameTxt').html(credentials.username)
//	$('#apiKeyTxt').html(credentials.accesscode)
//
//	return credentials
//
//}

// window.fbAsyncInit = function() {
// FB.init({
// appId : '224674001213541',
// cookie : true, // enable cookies to allow the server to access
// the session
// xfbml : true, // parse social plugins on this page
// version : 'v2.5' // use graph api version 2.5
// });

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
// FB.getLoginStatus(function(response) {
// statusChangeCallback(response);
// });

// };

// (function(d, s, id) {
// var js, fjs = d.getElementsByTagName(s)[0];
// if (d.getElementById(id))
// return;
// js = d.createElement(s);
// js.id = id;
// js.src = "//connect.facebook.net/en_GB/sdk.js";
// fjs.parentNode.insertBefore(js, fjs);
// console.info("facebook inserted")
// }(document, 'script', 'facebook-jssdk'));

console.info("loaded facebook.js")
