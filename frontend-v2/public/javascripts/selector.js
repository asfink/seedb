//given a database what are the tables in it

	// load dataset schemas
	$.each(schemas, function(key, value){
		console.log(schemas);
		if (key.indexOf("_type") == -1) {
			console.log("in load dataset schemas");
			console.log("key");
			console.log(key);
			console.log(key.indexOf("_type"));
			// populate dataset names
			var dataset_selector = document.getElementById("databaseSelector");
			var el = document.createElement("option");
		    el.textContent = key;
		    el.value = key;
		    dataset_selector.appendChild(el);
		    $("#databaseSelector").val(key);
		    $("#databaseSelector").trigger("change");
		}
	});