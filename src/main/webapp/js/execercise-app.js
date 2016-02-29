$(document).ajaxSend(function(e, xhr, options) {
	xhr.setRequestHeader("username", "awalker");
	xhr.setRequestHeader("api_key", "ueh32ueb23be32e73e723e2e7h2e7h23eibhvhv");
});

// The root URL for the RESTful services
var rootURL = "https://localhost:10443/training/webapi/excercise";

var currentExcercise;

// Retrieve excercise list when application starts
findAll();

// Nothing to delete in initial application state
$('#btnDelete').hide();

// Register listeners
$('#btnSearch').click(function() {
	search($('#searchKey').val());
	return false;
});

// Trigger search when pressing 'Return' on search key input field
$('#searchKey').keypress(function(e) {
	if (e.which == 13) {
		search($('#searchKey').val());
		e.preventDefault();
		return false;
	}
});

$('#btnAdd').click(function() {
	newExcercise();
	return false;
});

$('#btnSave').click(function() {
	if ($('#excerciseId').val() == '')
		addExcercise();
	else
		updateExcercise();
	return false;
});

$('#btnDelete').click(function() {
	deleteExcericse();
	return false;
});

//$('#excerciseList a').live('click', function() {
//	findById($(this).data('identity'));
//});

// Replace broken images with generic excercise bottle
$("img").error(function() {
	$(this).attr("src", "pics/generic.jpg");

});

function search(searchKey) {
	if (searchKey == '')
		findAll();
	else
		findByName(searchKey);
}

function newExcercise() {
	$('#btnDelete').hide();
	currentExcercise = {};
	renderDetails(currentExcercise); // Display empty form
}

function findAll() {
	console.log('findAll');
	$.ajax({
		type : 'GET',
		url : rootURL,
		dataType : "json", // data type of response
		success : renderList
	});
}

function findByName(searchKey) {
	console.log('findByName: ' + searchKey);
	$.ajax({
		type : 'GET',
		url : rootURL + '/search/name/' + searchKey,
		dataType : "json",
		success : renderList
	});
}

function findById(id) {
	console.log('findById: ' + id);
	$.ajax({
		type : 'GET',
		url : rootURL + '/' + id,
		dataType : "json",
		success : function(data) {
			$('#btnDelete').show();
			console.log('findById success: ' + data.name);
			currentExcercise = data;
			renderDetails(currentExcercise);
		}
	});
}

function addExcercise() {
	console.log('addExcercise');
	$.ajax({
		type : 'POST',
		contentType : 'application/json',
		url : rootURL,
		dataType : "json",
		data : formToJSON(),
		success : function(data, textStatus, jqXHR) {
			alert('Wine created successfully');
			$('#btnDelete').show();
			$('#excerciseId').val(data.id);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('addExcercise error: ' + textStatus);
		}
	});
}

function updateExcercise() {
	console.log('updateExcercise');
	$.ajax({
		type : 'PUT',
		contentType : 'application/json',
		url : rootURL + '/' + $('#excerciseId').val(),
		dataType : "json",
		data : formToJSON(),
		success : function(data, textStatus, jqXHR) {
			alert('Excercise updated successfully');
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('updateExcercise error: ' + textStatus);
		}
	});
}

function deleteExcericse() {
	console.log('deleteExcercise');
	$.ajax({
		type : 'DELETE',
		url : rootURL + '/' + $('#excerciseId').val(),
		success : function(data, textStatus, jqXHR) {
			alert('Excercise deleted successfully');
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('deleteExcercise error');
		}
	});
}

function renderList(data) {
	// JAX-RS serializes an empty list as null, and a 'collection of one' as an
	// object (not an 'array of one')
	var list = data == null ? [] : (data instanceof Array ? data : [ data ]);

	$('#excerciseList li').remove();
	$.each(list, function(index, excerise) {
		
		$('#excerciseList').append(
				
				'<li>' + excerise.name + '</li>'
				//'<li><a href="#" data-identity="' + excerise.id + '">'
				//		+ excerise.name + '</a></li>'
				
		);
	});
}

function renderDetails(excercise) {
	$('#excerciseId').val(excercise.id);
	$('#name').val(excercise.name);
}

// Helper function to serialize all the form fields into a JSON string
function formToJSON() {
	var excerciseId = $('#excerciseId').val();
	return JSON.stringify({
		"id" : excerciseId == "" ? null : excerciseId,
		"name" : $('#name').val()
	});
}
