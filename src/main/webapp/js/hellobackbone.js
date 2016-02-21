
var ExcerciseModel = Backbone.Model.extend({
	idAttribute : 'name',
	defaults : {
		name : null,
		tags : null
	}
});

var ExcerciseCollection = Backbone.Collection.extend({
	url : 'https://localhost:10443/training/webapi/excercise/',
	model : ExcerciseModel
});

var excercises = new ExcerciseCollection();

excercises.fetch().then(function() {
	console.log(excercises.length); // >> length: 1
});