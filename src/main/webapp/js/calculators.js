$(document).ready(function() {

	// jQuery methods go here...

	// alert("jquery is ready");

	// Event handlers

	$("#maxLiftTextBox").on("change paste keyup", function() {
		// alert($(this).val());
		update_percentages($(this).val())
	});

});

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

		var maxLift98 = max * 0.98
		var maxLift97 = max * 0.97
		var maxLift95 = max * 0.95
		var maxLift92 = max * 0.92
		var maxLift90 = max * 0.90
		var maxLift87 = max * 0.87
		var maxLift85 = max * 0.85
		var maxLift82 = max * 0.82
		var maxLift80 = max * 0.80
		var maxLift75 = max * 0.75
		var maxLift70 = max * 0.7
		var maxLift60 = max * 0.6

		$('#maxLift98').html(maxLift98)
		$('#maxLift97').html(maxLift97)
		$('#maxLift95').html(maxLift95)
		$('#maxLift92').html(maxLift92)
		$('#maxLift90').html(maxLift90)

		$('#maxLift87').html(maxLift87)
		$('#maxLift85').html(maxLift85)
		$('#maxLift82').html(maxLift82)
		$('#maxLift80').html(maxLift80)

		$('#maxLift75').html(maxLift75)

		$('#maxLift70').html(maxLift70)

		$('#maxLift60').html(maxLift60)

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
