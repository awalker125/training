
$(document).ajaxSend(function(e, xhr, options) 
{
    xhr.setRequestHeader("username", "awalker");
    xhr.setRequestHeader("api_key", "ueh32ueb23be32e73e723e2e7h2e7h23eibhvhv");
});


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

// var excercises = new ExcerciseCollection();

// excercises.fetch().then(function() {
// console.log(excercises.length); // >> length: 1
// });

// View class for displaying each excercise list item
var ExcercisesListItemView = Backbone.View.extend({
	tagName : 'li',
	className : 'excercise',
	template : _.template($('#excercise-item-tmpl').html()),

	initialize : function() {
		this.listenTo(this.model, 'destroy', this.remove)
	},

	render : function() {
		var html = this.template(this.model.toJSON());
		this.$el.html(html);
		return this;
	},

	events : {
		'click .remove' : 'onRemove'
	},

	onRemove : function() {
		this.model.destroy();
	}
});

// View class for rendering the list of all excercises
var ExcercisesListView = Backbone.View.extend({
	el : '#excercises-app',

	initialize : function() {
		this.listenTo(this.collection, 'sync', this.render);
	},

	render : function() {
		var $list = this.$('ul.excercises-list').empty();

		this.collection.each(function(model) {
			var item = new ExcercisesListItemView({
				model : model
			});
			$list.append(item.render().$el);
		}, this);

		return this;
	},

	events : {
		'click .create' : 'onCreate'
	},

	onCreate : function() {
		var $name = this.$('#excercise-name');

		if ($name.val()) {
			this.collection.create({
				name : $name.val()
			});

			$name.val('');
		}
	}
});

// Create a new list collection, a list view, and then fetch list data:
var excerciseList = new ExcerciseCollection();
var excerciseView = new ExcercisesListView({
	collection : excerciseList
});
excerciseList.fetch();