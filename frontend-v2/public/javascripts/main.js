$(function(){

	var big_width = 700;
	var big_height = 300;

	var small_width = 250;
	var small_height = 200;

	var bugout = new debugout();
	bugout.useTimestamps = true;
	bugout.logFilename = 'log-10.txt'; // update this

	var slider = $('.slider1').bxSlider();
	var slider_options = {
	    controls : true,
	    slideWidth: 270,
	    minSlides: 6,
	    maxSlides: 6,
	    slideMargin: 10,
	    infiniteLoop: false,
  		hideControlOnEnd: true
	  };

	$("#getLog").on('click', function (e) {
		bugout.downloadLog();
	});

	$("#datasetSelector").change(function (e) {
		var curr_dataset = e.target[e.target.selectedIndex].value;
		$(".attributes").empty();
		$(".attributes").each(function (i, obj){
			var measures = false;
			if ($(obj).hasClass("measures")) measures = true;
			$.each(schemas[curr_dataset], function (item, value) {
				if (measures && schemas[curr_dataset + "_type"][item] == "measure") {	
					var el = document.createElement("option");
				    el.textContent = item;
				    el.value = item;
				    obj.appendChild(el);
				} else if (!measures) {
					var el = document.createElement("option");
				    el.textContent = item;
				    el.value = item;
				    obj.appendChild(el);
				}
			});
		});

		// fill in settings
		$(".rec_settings").each(function(i, obj) {
			// add stuff
		});
	});

	$("#setQuery").on('click', function (e) {
		var rec_type = $(this).attr('rec_type');
		var params = {};
		$("#recs_div").show();

		dataset = $("#datasetSelector option:selected").text();
		hasComparison = $('#addComparison').is(":checked");
		filters = [];
		
		// get the filters
		for (var i = 1; i < 3; i++) {
			var tmp = [];
			$(".attributeFilter"+i).each(function() {
				attribute = $(this).find(".attribute option:selected").text();
				operator = $(this).find(".operator option:selected").text();
				value = $(this).find(".value").val();
				if (schemas[dataset][attribute] == "number") {
					value = parseFloat(value);
				}
				tmp.push([attribute, operator, value]);
			});
			filters.push(tmp);
		}
		
		var params = {
			dataset : dataset,
			hasComparison : hasComparison,
			filters : JSON.stringify(filters),
			rec_type : rec_type
		};

		$("#recs_div .real_rec").remove();
		bugout.log({"getRec" : params});

		console.log(params);
		// make ajax request
		$.post('/getRecommendations', params, function(ret) {
			console.log(ret);
			var recs = JSON.parse(ret);

			for (var i = 0; i < recs.length; i++) {
				var rec = recs[i];
				console.log(rec);
				var data = new google.visualization.DataTable();
				// populate metadata from schema
				data.addColumn("string", rec.x);
				if (rec.type == "single") {
					data.addColumn("number", "Query 1");
				} else {
					if (hasComparison) {
						data.addColumn("number", "Query 1");
						data.addColumn("number", "Query 2");
					} else {
						data.addColumn("number", "Query");
						data.addColumn("number", "Full");
					}
				}
				
				// populate data from results
				data.addRows(rec.dist);
				data.sort([{column: 0}])

				// compute normalized data
				var old_data = rec.dist;
				var new_data = [];
				for (var row = 0; row < old_data.length; row++) {
					var tmp = []
	        		for (var col = 0; col < old_data[row].length; col++) {
	        			tmp.push(old_data[row][col]);
	        		}
	        		new_data.push(tmp);
	        	}

	        	var totals = [];
	        	for (var row = 0; row < old_data.length; row++) {
	        		for (var col = 1; col < old_data[row].length; col++) {
	        			if (row == 0) {
	        				totals.push(0);
	        			}
	        			totals[col-1] = totals[col-1] + old_data[row][col];
	        		}
	        	}
	        	for (var row = 0; row < old_data.length; row++) {
	        		for (var col = 1; col < old_data[row].length; col++) {
	        			new_data[row][col] = old_data[row][col] / totals[col-1];
	        		}
	        	}

	        	var title;
				var vAxis;
				if (rec.agg == "COUNT") {
					title = rec.x + " vs. COUNT(*)";
					vAxis = "COUNT(*)";
				} else {
					title = rec.x + " vs. " + rec.agg + '(' + rec.y + ")";
					vAxis = rec.agg + '(' + rec.y + ")" ;
				}

				// Set chart options
	        	var options = {'title': title,
	                    	'height':small_height,
	                   		'width' : small_width,
	                   		vAxis: { title: vAxis },
	                   		hAxis: {title : rec.x},
	                   		agg : rec.agg,
	                   		titleFontSize:12, 
	                   		orig_data : rec.dist,
	                   		norm_data : new_data,
	                   		x : rec.x,
	                   		y : rec.y
	                   };
               	bugout.log({"rec" : options['title']});

               	var el = $(".template_rec").clone();
				el.removeClass("template_rec");
				el.addClass("real_rec");
				
				var b = ($("#recs").data("num") + 1);
				$("#recs").data("num", b);
				var rec_id = "rec_" + b;
				
				el.find(".rec_viz").last().attr("id", rec_id);
				el.data('image_options', options);
				el.data('image_raw_data', data);
				el.addClass('slide');

				el.find(".zoom").on('click', function (e) {
					var el = $(this).parent();
					var data = el.data('image_raw_data');
			    	var options = el.data('image_options');
			    	options['height'] = big_height;
			    	options['width'] = big_width;
			    	bugout.log({"rec_zoom" : options['title']});
			    	$('#x_axis option[value="' + options.x + '"]').attr('selected', 'selected');
        			$('#y_axis option[value="' + options.y + '"]').attr('selected', 'selected');
        			$('#y_aggregate option[value="' + options.agg + '"]').attr('selected', 'selected');
			    	
			    	var chart;
			    	if (options['agg'] == "NONE") {
			    		chart = new google.visualization.ScatterChart(document.getElementById("big_viz"));
			    	} else {
			    		chart = new google.visualization.ColumnChart(document.getElementById("big_viz"));
			    	}
			    	chart.draw(data, options);
			    	$('#big_viz').data('image_raw_data', data);
			    	$('#big_viz').data('image_options', options);
			    	$(".bookmark").removeClass('bookmarked');
			    	$(".normalize").removeClass('normalized');
				});

				//$("#recs").append(el);
				$(".slider1").append(el);

				var chart = new google.visualization.ColumnChart(document.getElementById(rec_id));
	        	chart.draw(data, options);
			}
			slider.reloadSlider(slider_options);	
		});
	});

	$(".normalize").on("click", function (e) {
		$(this).toggleClass('normalized');
		var data = $("#big_viz").data('image_raw_data');
    	var options = $("#big_viz").data('image_options');
    	bugout.log({"normalize" : options['title']});
		if ($(this).hasClass('normalized')) {
        	data.removeRows(0, data.getNumberOfRows());
        	data.addRows(options.norm_data);
        } else {
        	data.removeRows(0, data.getNumberOfRows());
        	data.addRows(options.orig_data);
        }
    	// MANASI
    	options['height'] = big_height;
    	options['width'] = big_width;
    	
    	var chart;
    	if (options['agg'] == "NONE") {
    		chart = new google.visualization.ScatterChart(document.getElementById("big_viz"));
    	} else {
    		chart = new google.visualization.ColumnChart(document.getElementById("big_viz"));
    	}
    	chart.draw(data, options);
    	$('#big_viz').data('image_raw_data', data);
    	$('#big_viz').data('image_options', options);
    	$(".bookmark").removeClass('bookmarked');
	});

	$(".bookmark").on('click', function (e) {
		// flip background color of button
		$(this).toggleClass('bookmarked');
		if ($(this).hasClass('bookmarked')) {
			var data = $("#big_viz").data('image_raw_data');
        	var options = $("#big_viz").data('image_options');
        	bugout.log({"bookmark_add" : options['title']});

        	options['height'] = small_height;
        	options['width'] = small_width;

			var el = $(".template_bookmark").clone();
			el.removeClass("template_bookmark");
			
			var b = ($("#bookmarks").data("num") + 1);
			$("#bookmarks").data("num", b);
			var bookmark_id = "bookmark_" + b;
			
			el.find(".rec_viz").last().attr("id", bookmark_id);
			el.data('image_options', options);
			el.data('image_raw_data', data);

			el.find(".zoom").on('click', function (e) {
				var el = $(this).parent();
				var data = el.data('image_raw_data');
		    	var options = el.data('image_options');
		    	options['height'] = big_height;
		    	options['width'] = big_width;
		    	bugout.log({"bookmark_zoom" : options['title']});
		    	
		    	var chart;
		    	if (options['agg'] == "NONE") {
		    		chart = new google.visualization.ScatterChart(document.getElementById("big_viz"));
		    	} else {
		    		chart = new google.visualization.ColumnChart(document.getElementById("big_viz"));
		    	}
		    	$('#x_axis option[value="' + options.x + '"]').attr('selected', 'selected');
    			$('#y_axis option[value="' + options.y + '"]').attr('selected', 'selected');
    			$('#y_aggregate option[value="' + options.agg + '"]').attr('selected', 'selected');
			    	
		    	chart.draw(data, options);
		    	$('#big_viz').data('image_raw_data', data);
		    	$('#big_viz').data('image_options', options);
		    	$(".bookmark").removeClass('bookmarked');
		    	$(".normalize").removeClass('normalized');
			});
			el.find(".delete").on('click', function (e) {
				bugout.log({"bookmark_delete" : options['title']});
				$(this).parent().remove();
			});

			$("#bookmarks").append(el);
			var chart;
			if (options['agg'] == "NONE") {
	    		chart = new google.visualization.ScatterChart(document.getElementById(bookmark_id));
	    	} else {
	    		chart = new google.visualization.ColumnChart(document.getElementById(bookmark_id));
	    	}
	    	chart.draw(data, options);		
		}
	});

	$('#addComparison').change(function(e){
        if ($(this).is(':checked')) {
            $("#comparisonQuery").show();
        } else {
        	$("#comparisonQuery").hide();
        }
    });

	// load dataset schemas
	$.each(schemas, function(key, value){
		if (key.indexOf("_type") == -1) {
			// populate dataset names
			var dataset_selector = document.getElementById("datasetSelector");
			var el = document.createElement("option");
		    el.textContent = key;
		    el.value = key;
		    dataset_selector.appendChild(el);
		    $("#datasetSelector").val(key);
		    $("#datasetSelector").trigger("change");
		}
	});

	// load visualization library
	google.load('visualization', '1.0', {'packages':['corechart'], callback: function() {}});

	// implement manual plotting
	$("#manualPlot").submit(function (e) {
		// get the x axis, get the y axis and aggregate function
		dataset = $("#datasetSelector option:selected").text();
		x = $("#x_axis option:selected").text();
		y = $("#y_axis option:selected").text(); 
		agg = $("#y_aggregate option:selected").text();
		hasComparison = $('#addComparison').is(":checked");
		filters = [];
		
		// get the filters
		for (var i = 1; i < 3; i++) {
			var tmp = [];
			$(".attributeFilter"+i).each(function() {
				attribute = $(this).find(".attribute option:selected").text();
				operator = $(this).find(".operator option:selected").text();
				value = $(this).find(".value").val();
				if (schemas[dataset][attribute] == "number") {
					value = parseFloat(value);
				}
				tmp.push([attribute, operator, value]);
			});
			filters.push(tmp);
		}
		
		var params = {
			dataset : dataset,
			x : x,
			y : y,
			agg : agg,
			hasComparison : hasComparison,
			filters : JSON.stringify(filters)
		};
		console.log(params);
		bugout.log({"manualPlot" : params});
		
		$.post('/manualPlot', params, function(ret) {
			// do the processing and create the chart
			ret = JSON.parse(ret);
			console.log(ret);
			
			var data = new google.visualization.DataTable();
			// populate metadata from schema
			
			if (agg == "NONE") {
				data.addColumn("number", x);
			} else {
				data.addColumn(schemas[dataset][x], x);
			}
			
			if (hasComparison) {
				data.addColumn(schemas[dataset][y], "Query 1");
				data.addColumn(schemas[dataset][y], "Query 2");
			} else {
				data.addColumn(schemas[dataset][y], y);
			}
			if (schemas[dataset][x] == "number") {
				ret["rows2"] = ret["rows2"].map(function(obj) {
					obj[0] = parseFloat(obj[0]);
					return obj;
				});
			}

			// populate data from results
			data.addRows(ret["rows2"]);
			data.sort([{column: 0}])

			// compute normalized data
			var old_data = ret["rows2"];
			var new_data = [];
			for (var row = 0; row < old_data.length; row++) {
				var tmp = []
        		for (var col = 0; col < old_data[row].length; col++) {
        			tmp.push(old_data[row][col]);
        		}
        		new_data.push(tmp);
        	}

        	var totals = [];
        	for (var row = 0; row < old_data.length; row++) {
        		for (var col = 1; col < old_data[row].length; col++) {
        			if (row == 0) {
        				totals.push(0);
        			}
        			totals[col-1] = totals[col-1] + old_data[row][col];
        		}
        	}
        	for (var row = 0; row < old_data.length; row++) {
        		for (var col = 1; col < old_data[row].length; col++) {
        			new_data[row][col] = old_data[row][col] / totals[col-1];
        		}
        	}

			var title;
			var vAxis;
			if (agg == "NONE") {
				title = x + " vs. " + y;
				vAxis = y;
			} else if (agg == "COUNT") {
				title = x + " vs. " + agg;
				vAxis = agg;
			} else {
				title = x + " vs. " + agg + '(' + y + ")";
				vAxis = agg + '(' + y + ")" ;
			}

			// Set chart options
        	var options = {'title': title,
                    	'height':big_height,
                   		'width' : big_width,
                   		vAxis: { title: vAxis},
                   		hAxis: {title : x},
                   		agg : agg,
                   		orig_data : ret["rows2"],
                   		norm_data : new_data,
                   		x : x,
                   		y : y
                   };
			var chart;
			if (agg == "NONE") {
				chart = new google.visualization.ScatterChart(document.getElementById('big_viz'));
			} else {
				chart = new google.visualization.ColumnChart(document.getElementById('big_viz'));
			}
			chart.draw(data, options);
        	$('#big_viz').data('image_raw_data', data);
        	$('#big_viz').data('image_options', options);
        	$(".bookmark").removeClass('bookmarked');
        	$(".normalize").removeClass('normalized');
		});
		e.preventDefault();
	});

	$(".addAttributeFilter").on("click", function (e) {
		var idx = $(this).data('idx');
		var el = $(".templateAttributeFilter");
		var el2 = el.clone();
		el2.addClass("attributeFilter" + idx);
		el2.removeClass("templateAttributeFilter");
		el2.find(".removeFilter").data('idx', idx);
		el2.insertAfter($(".dummyRow" + idx));
		$(".removeFilter").on("click", function (e) {
			var idx = $(this).data('idx');
			//alert(idx);
			$(this).closest('.attributeFilter' + idx).remove();
			e.preventDefault();
		});
		e.preventDefault();
	});

	
});