$(document).ready(function() {

	// jQuery methods go here...

	$("#createSetBtn").click(function() {
		
		var weight = $("#weightTxt").html();
		console.info("weight " + weight)
		createSet(weight,100)
	});

	

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

$(document).ajaxSend(function(e, xhr, options) {
	var email = sessionStorage.getItem("email");
	var accessToken = sessionStorage.getItem("accessToken");

	if (email) {
		if (accessToken) {
			xhr.setRequestHeader("username", email);
			xhr.setRequestHeader("api_key", accessToken);
		} else {
			console.error("accessToken is not set. Login first")
		}
	} else {
		console.error("email is not set. Login first")
	}

});

function createSet(bodyWeight, weight) {
	var set = {
		"bodyWeight" : bodyWeight,
		"weight" : weight,
		"targetReps" : 0,
		"restingHeartRate" : 0,
		"mood" : "string",
		"actualReps" : 0,
		"trainingMax" : 0,
		"injured" : false,
		"creatine" : false,
		"competition" : false,
		"excercise" : "string",

	};

	console.info("Creating set")
	$.ajax({
		type : "POST",
		url : "/training/webapi/set",
		// The key needs to match your method's input parameter
		// (case-sensitive).
		data : JSON.stringify(set),
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {
			console.info("post result" + data)
			alert(data);
			$('#results').html(data)
		},
		failure : function(errMsg) {
			console.info("post result" + errMsg)
			alert(errMsg);
			$('#results').html(errMsg)
		}
	});
	console.info("Created set")
}
