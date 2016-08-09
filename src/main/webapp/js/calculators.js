$(document).ready(function() {

	// jQuery methods go here...

	// Event handlers

	// var randomBackground = getRandomInt(1, 9);
	//
	// $(".full").css(
	// "background",
	// 'url(../training/images/training' + randomBackground
	// + '.jpg) no-repeat center center fixed')

	$("#maxLiftTextBox").on("change paste keyup", function() {
		// alert($(this).val());
		update_percentages($(this).val())
	});

	$("#maxLiftResetButton").click(function() {
		reset_max()
	});

	//maxLiftResetButton

});

function reset_max() {

	$("#maxLiftTextBox").html("")

}

function update_percentages(max) {

	// alert(max);

	if ($.isNumeric(max)) {

		// make the box glow blue
		$("#maxLiftTextBox").css('border-color', "#66afe9")
		$("#maxLiftTextBox")
				.css('-webkit-box-shadow',
						'inset 0 1px 1px rgba(0,0,0,.075), 0 0 8px rgba(102, 175, 233, .6)')
		$("#maxLiftTextBox")
				.css('box-shadow',
						'inset 0 1px 1px rgba(0,0,0,.075), 0 0 8px rgba(102, 175, 233, .6)')

		var maxLift98 = Math.round(max * 0.98)
		var maxLift97 = Math.round(max * 0.97)
		var maxLift95 = Math.round(max * 0.95)
		var maxLift92 = Math.round(max * 0.92)
		var maxLift90 = Math.round(max * 0.90)
		var maxLift87 = Math.round(max * 0.87)
		var maxLift85 = Math.round(max * 0.85)
		var maxLift82 = Math.round(max * 0.82)
		var maxLift80 = Math.round(max * 0.80)
		var maxLift75 = Math.round(max * 0.75)
		var maxLift70 = Math.round(max * 0.7)
		var maxLift60 = Math.round(max * 0.6)

		$('#maxLift100').html(max + "kg")
		$('#maxLift98').html(maxLift98 + "kg")
		$('#maxLift97').html(maxLift97 + "kg")
		$('#maxLift95').html(maxLift95 + "kg")
		$('#maxLift92').html(maxLift92 + "kg")
		$('#maxLift90').html(maxLift90 + "kg")
		$('#maxLift87').html(maxLift87 + "kg")
		$('#maxLift85').html(maxLift85 + "kg")
		$('#maxLift82').html(maxLift82 + "kg")
		$('#maxLift80').html(maxLift80 + "kg")
		$('#maxLift75').html(maxLift75 + "kg")
		$('#maxLift70').html(maxLift70 + "kg")
		$('#maxLift60').html(maxLift60 + "kg")

	} else {
		// make the box glow red and dont do anything else
		$("#maxLiftTextBox").css('border-color', "#ff471a")

		$("#maxLiftTextBox")
				.css('-webkit-box-shadow',
						'inset 0 1px 1px rgba(0,0,0,.075), 0 0 8px rgba(255, 71, 26, .6)')

		$("#maxLiftTextBox")
				.css('box-shadow',
						'inset 0 1px 1px rgba(0,0,0,.075), 0 0 8px rgba(255, 71, 26, .6)')

	}
}

function getRandomInt(min, max) {
	return Math.floor(Math.random() * (max - min + 1)) + min;
}
