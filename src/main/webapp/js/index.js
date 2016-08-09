$(document).ready(function() {

	// jQuery methods go here...

	//update_userdetails()

	$("#updateUsernameBtn").click(function() {
		update_userdetails()
	});

	
});

function update_userdetails() {
	
	var credentials = getUserDetails();

	console.info("update_userdetails called")
	
	alert("called")

	//if (typeof (Storage) !== "undefined") {
		// Code for localStorage/sessionStorage.

	//	var credentials = localStorage.getItem("credentials");

	//	console.info("credentials")
	//	console.info(JSON.stringify(credentials));

		$('#usernameTxt').html(credentials.username)
		$('#apiKeyTxt').html(credentials.accesscode)

	//} else {
	//	alert("Sorry your broswer is too old. Try something newer")
	//}

}
