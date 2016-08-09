$(document).ready(function() {

	// jQuery methods go here...

	var accessToken = sessionStorage.getItem("accessToken");

	if (accessToken) {
		console.info("accessToken: " + accessToken)
	} else {
		console.info("accessToken: not set")
	}

	var email = sessionStorage.getItem("email");
	if (email) {
		console.info("email: " + email)
	} else {
		console.info("email: not set")
	}
	
	
	var name = sessionStorage.getItem("name");
	if (name) {
		console.info("name: " + name)
	} else {
		console.info("name: not set")
	}

});
