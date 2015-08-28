var bugout = new debugout();
bugout.useTimestamps = true;
bugout.logFilename = 'log-10.txt'; // update this

var zoomRecTime = null;
var zoomBookMarkTime = null;
var zoomManualPlotTime=null;
var previousChartInfo = null;
var markTime = null;
var big_width = 700;
var big_height = 300;

var small_width = 250;
var small_height = 200;
var current_database;
var current_dataset;
$(function(){
	$('#bookmarked').hide();
	$('#seeRecommendations').hide();
	$('#big_viz').hide();
	$('#vis_builder').hide();
	$('#rec_settings').hide();
	$('#submitRec').hide();
	$('#attributeFilter').hide();
	$('#comparisonQuery').hide();
	$('#comparisonSelector').hide();
	$('#querySelector').hide();
	$('#dataSetSelector').hide();
	$('#bookmarkButton').hide();
	$('#logger').hide();
	$('#dataHide').hide();
});


function loadDBs(){
	$("#databaseSelector").empty();
	//Use an AJAX call to get the list of databases to select from
	$.ajax({
		url: 'www.meow.meow/getDBS',
		method: 'GET',
		data: {},
		success: function(data) {
			console.log("success");
			$.each(data, function(key,value){
				console.log("KEY");
				console.log(key);
				console.log("VALUE");
				console.log(value);
			});
		},
		error: function(){
			// load dataset schemas
			var dbSelect = document.getElementById("databaseSelector");
			var base1 = document.createElement("option");
			base1.textContent = 'test';
			base1.value = 'test';
			dbSelect.appendChild(base1);
			$("#databaseSelector").val('test');
			var blankOne = document.createElement("option");
			blankOne.textContent = '';
			blankOne.value = '';
			dbSelect.appendChild(blankOne);
			$("#databaseSelector").val('');
		}
	});
}

//prefunction for loading the values of the possible databases into the db list table
loadDBs();
// $(function(){
// });

$(function(){
	$("#reloadA").on("click", function(eventObj){
		$('#bookmarked').hide();
		$('#seeRecommendations').hide();
		$('#big_viz').hide();
		$('#vis_builder').hide();
		$('#rec_settings').hide();
		$('#submitRec').hide();
		$('#attributeFilter').hide();
		$('#comparisonQuery').hide();
		$('#comparisonSelector').hide();
		$('#querySelector').hide();
		$('#dataSetSelector').hide();
		$('#bookmarkButton').hide();
		$('#logger').hide();
		$('#dataHide').hide();
		loadDBs();
		zoomRecTime = null;
		zoomBookMarkTime = null;
		zoomManualPlotTime=null;
		previousChartInfo = null;
		markTime = null;
		bugout.log({"action": "reload", "time": Date.now()});
	});
	$('#databaseSelector').change(function(eventObj){	
		//gets the name of the selected DB
		current_database = eventObj.target[eventObj.target.selectedIndex].value;
		$('#dataHide').show("slow");
		$('#datasetSelector').empty();

		//Use an AJAX call to get the list of tables from the database
		$.ajax({
			url: 'www.meow.meow/getTables',
			method: 'GET',
			data: {},
			success: function(data) {
				console.log("success");
			},
			error: function(){
				console.log("failure!");
				// load dataset schemas
				var lastV = null;
				var dataset_selector = document.getElementById("datasetSelector");
				$.each(schemas, function(key, value){
					if (key.indexOf("_type") == -1) {
						// populate dataset names
						var el = document.createElement("option");
						el.textContent = key;
						el.value = key;
						dataset_selector.appendChild(el);
						lastV = key;
					}
				});
				$("#datasetSelector").val(lastV);
				$("#datasetSelector").trigger("change");

				$('#querySelector').show("slow");
				$('#attributeFilter').show('slow');
				$('#comparisonSelector').show('slow');
				$('#vis_builder').show('slow');
				$('#submitRec').show('slow');
				$('#big_viz').show();
				$('#bookmarked').show();
				$('#seeRecommendations').show();
				$('#querySelector').show();
				$('#bookmarkButton').show("slow");
				bugout.log({"action": "load_database","database": current_database, "time": Date.now()});
			}
		});
	});

	$("#datasetSelector").change(function (e) {
		// console.log("#datasetSelector");
		current_dataset = e.target[e.target.selectedIndex].value;

		$(".attributes").empty();
		$("#attributeFilter").empty();

		$(".attributes").each(function (i, obj){
			var measures = false;
			if ($(obj).hasClass("measures")) measures = true;
			// console.log($(obj));
			$.each(schemas[current_dataset], function (item, value) {
				if (measures && schemas[current_dataset + "_type"][item] == "measure") {	
					var el = document.createElement("option");
					el.textContent = item;
					el.value = item;
					obj.appendChild(el);
				    // console.log("measurement");
				    // console.log(obj);
				} else if (!measures) {
					var el = document.createElement("option");
					el.textContent = item;
					el.value = item;
					obj.appendChild(el);
				    // console.log("not measurement");
				    // console.log(obj);
				}
			});
		});
		$.each(schemas[current_dataset],function(item,value){
			var attrCheckHolder = document.createElement("span");
			var attribute_selector = document.getElementById("attributeFilter");
			var attrf = document.createElement("input");
			attrf.type = "checkbox";
			attrf.checked = "checked";
			attrf.className = "attrfBox";
			attrf.value = item;
			attrCheckHolder.appendChild(attrf);
			attrCheckHolder.appendChild(document.createTextNode(item+" "));
			attrCheckHolder.className = "attrHolder";
			// attribute_selector.appendChild(attrf);
			// attribute_selector.appendChild(document.createTextNode(item+" "));

			//the buttons are still broken.

			var grouping = document.createElement("div");
			grouping.className="btn-group btn-group-xs btnGroup";
			grouping.role="group";

			var sumbutton = document.createElement("button");
			sumbutton.type="button";
			sumbutton.value = item;
			sumbutton.className="sumButton btn btn-primary";
			//$(".sumButton").addClass('active');
			sumbutton.appendChild(document.createTextNode("sum"));

			var countbutton = document.createElement("button");
			countbutton.type="button";
			countbutton.value = item;
			countbutton.className="countButton btn btn-primary";
			//$(".sumButton").addClass('active');
			countbutton.appendChild(document.createTextNode("count"));

			var avgbutton = document.createElement("button");
			avgbutton.type="button";
			avgbutton.value = item;
			avgbutton.className="avgButton btn btn-primary";
			//$(".sumButton").addClass('active');
			avgbutton.appendChild(document.createTextNode("avg"));

			grouping.appendChild(sumbutton);
			grouping.appendChild(countbutton);
			grouping.appendChild(avgbutton);

			var groupingHolder = document.createElement("span");
			groupingHolder.appendChild(grouping);
			groupingHolder.className = "attrButtonHolder";

			// attribute_selector.appendChild(grouping);
			var getItTogether = document.createElement("div");
			getItTogether.className="getItTogether";
			getItTogether.value=item;

			getItTogether.appendChild(attrCheckHolder);
			getItTogether.appendChild(groupingHolder);

			attribute_selector.appendChild(getItTogether);

			// attribute_selector.appendChild(attrCheckHolder);
			// attribute_selector.appendChild(groupingHolder);

			attribute_selector.appendChild(document.createElement("br"));
		});
		bugout.log({"action": "load_dataset","database": current_dataset, "time": Date.now()});
	});
});

$(function(){
	//Event Binding for dynamically created elements -> because dynamically created, must use a delegated events
	$("#attributeFilter").on("click",".sumButton",function(obj){
		$(obj.toElement).toggleClass("btn-primary");
	});
	$("#attributeFilter").on("click",".avgButton",function(obj){
		$(obj.toElement).toggleClass("btn-primary");
	});
	$("#attributeFilter").on("click", ".countButton", function(obj){
		$(obj.toElement).toggleClass("btn-primary");
	}); 
});

$(function(){
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
		//gets all unchecked boxes for identifying uninterested attributes
		var unchecked = [];
		$('.attrfBox').each(function(index, obj){
			if(!$(obj).is(':checked')){
				unchecked.push(obj.value);
			}
		});
		
		var params = {
			dataset : dataset,
			hasComparison : hasComparison,
			filters : JSON.stringify(filters),
			rec_type : rec_type,
			remove_attrs  : JSON.stringify(unchecked)
		};
		var rec_titles = [];

		$("#recs_div .real_rec").remove();
		bugout.log({"action": "getRecommendations", "parameters" : params, "time": Date.now()});

		// make ajax request
		$.post('/getRecommendations', params, function(ret) {
			//console.log(ret);
			var recs = JSON.parse(ret);
			var blueString = null;
			var redString = null;
			console.log(filters);
			if (filters[0].length>0){
				var blueQ = filters[0];
				for (var i=0; i<blueQ.length; i++){
					if(blueQ[i].length>0){
						if (!isNaN(blueQ[i][2])){
							var filterQ = blueQ[i][0]+" "+blueQ[i][1]+" "+blueQ[i][2];

							if(blueString === null){
								blueString=filterQ;
							}
							else 
							{
								blueString = blueString + " & " + filterQ;
							}
						}
					}
				}
			}
			if (filters[1].length>0){
				var redQ = filters[1];
				for (var i=0; i<redQ.length; i++){
					if(redQ[i].length>0){
						if (!isNaN(redQ[i][2])){
						var filterQ = redQ[i][0]+" "+redQ[i][1]+" "+redQ[i][2];
						if(redString === null){
							redString=filterQ;
						}
						else 
						{
							redString = redString + " & " + filterQ;
						}
					}
					}
				}
			}

			for (var i = 0; i < recs.length; i++) {
				var rec = recs[i];
				var data = new google.visualization.DataTable();
				// populate metadata from schema
				data.addColumn("string", rec.x);
				if (rec.type == "single") {
					data.addColumn("number", "Query 1");
				} else {
					if (hasComparison) {
						if(blueString!==null&&redString!==null){
							data.addColumn("number", blueString);
							data.addColumn("number", redString);		
						}
						else if (blueString===null&redString!==null)
						{
							data.addColumn("number", "Full");
							data.addColumn("number", redString);
						}
						else
						{
							data.addColumn("number", "Query");
							data.addColumn("number", "Full");	
						}
					} else {
						if(blueString===null&&redString!==null){
							data.addColumn("number", "Full");
							data.addColumn("number", RedString);
						}
						else if (redString===null&&blueString!==null){
							data.addColumn("number", blueString);
							data.addColumn("number", "Full");
						}
						else
						{
							data.addColumn("number", "Query");
							data.addColumn("number", "Full");	
						}
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

				//in zoomRecTime;
				// Set chart options
				var options = {
					'title': title,
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
			//all recommended thigns
			rec_titles.push( options['title'])
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

				//in zoomRecTime;
				var nowTime = Date.now();
				switch (markTime){
					case "zoomRecTime":
						var diff = nowTime-zoomRecTime;
						bugout.log({"action": "view_recommendation","chart_title" : previousChartInfo,"viewtime":diff, "time":Date.now()});
						zoomRecTime = nowTime;
						previousChartInfo=options['title'];
						break;
					case "zoomBookMarkTime":
						var diff = nowTime - zoomBookMarkTime;
						bugout.log({"action": "view_bookmark", "chart_title":previousChartInfo,"viewtime":diff, "time":Date.now()});
						zoomBookMarkTime=null;
						markTime = "zoomRecTime";
						previousChartInfo = options['title'];
						zoomRecTime = nowTime;
						break;
					case "zoomManualPlotTime":
						var diff = nowTime - zoomManualPlotTime;
						bugout.log({"action": "view_manualPlot","chart_title":previousChartInfo,"viewtime":diff, "time": Date.now()});
						zoomManualPlotTime=null;
						markTime = "zoomRecTime";
						previousChartInfo = options['title'];
						zoomRecTime = nowTime;
						break;
					default:
						previousChartInfo=options['title'];
						markTime = "zoomRecTime";
						zoomRecTime=Date.now();
				};

				$('#x_axis option[value="' + options.x + '"]').attr('selected', 'selected');
				$('#y_axis option[value="' + options.y + '"]').attr('selected', 'selected');
				$('#y_aggregate option[value="' + options.agg + '"]').attr('selected', 'selected');


				var chart;
				if (options['agg'] == "NONE") {
					chart = new google.visualization.ScatterChart(document.getElementById("big_viz"));
				} else {
					chart = new google.visualization.ColumnChart(document.getElementById("big_viz"));
				}

				//draw large
				chart.draw(data, options);
			    	// console.log(chart);
			    	$('#big_viz').data('image_raw_data', data);
			    	$('#big_viz').data('image_options', options);
			    	$(".bookmark").removeClass('bookmarked');
			    	$(".normalize").removeClass('normalized');
			    });

				//$("#recs").append(el);
				$(".slider1").append(el);

				//draw small
				var chart = new google.visualization.ColumnChart(document.getElementById(rec_id));
				chart.draw(data, options);
			}
			slider.reloadSlider(slider_options);
			bugout.log({"action": "list_recommendations","list": rec_titles, "time": Date.now()});
		});
});

$(".normalize").on("click", function (e) {
	$(this).toggleClass('normalized');
	var data = $("#big_viz").data('image_raw_data');
	var options = $("#big_viz").data('image_options');
	bugout.log({"action": "normalize_chart","chart_title" : options['title'],"time":Date.now()});
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
			bugout.log({"action": "add_bookmark","chart_title" : options['title'],"time":Date.now()});

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

				//in zoomBookMarkTime
				var nowTime = Date.now();
				switch (markTime){
					case "zoomRecTime":
						var diff = nowTime - zoomRecTime;
						bugout.log({"action": "view_recommendation","chart_title" : previousChartInfo,"viewtime":diff, "time":Date.now()});
						zoomRecTime=null;
						markTime = "zoomBookMarkTime";
						previousChartInfo = options['title'];
						zoomBookMarkTime = nowTime;
						break;
					case "zoomBookMarkTime":
						var diff = nowTime-zoomBookMarkTime;
						bugout.log({"action": "view_bookmark", "chart_title":previousChartInfo,"viewtime":diff, "time":Date.now()});
						zoomBookMarkTime = nowTime;
						previousChartInfo=options['title'];
						break;
					case "zoomManualPlotTime":
						var diff = nowTime - zoomManualPlotTime;
						bugout.log({"action": "view_manualPlot","chart_title":previousChartInfo,"viewtime":diff, "time": Date.now()});
						zoomManualPlotTime=null;
						markTime = "zoomBookMarkTime";
						previousChartInfo = options['title'];
						zoomBookMarkTime = nowTime;
						break;
					default:
						previousChartInfo=options['title'];
						markTime = "zoomBookMarkTime";
						zoomBookMarkTime=Date.now();
					};

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
				bugout.log({"action":"bookmark_delete", "chart_title" : options['title'],"time":Date.now()});
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
		//console.log(params);
		bugout.log({"action":"manual_plot", "parameters" : params, "time": Date.now()});
		
		$.post('/manualPlot', params, function(ret) {
			// do the processing and create the chart
			ret = JSON.parse(ret);
			//console.log(ret);
			// console.log("params");
			// console.log(params.data);
			// console.log("ret");
			// console.log(ret);
			var data = new google.visualization.DataTable();
			// populate metadata from schema
			
			var blueString = null;
			var redString = null;
			// console.log(filters);
			// do the processing and create the chart
			// console.log("params");
			// console.log(params.data);
			// console.log("ret");
			// console.log(ret);
			var data = new google.visualization.DataTable();

			if (filters[0].length>0){
				var blueQ = filters[0];
				for (var i=0; i<blueQ.length; i++){
					if(blueQ[i].length>0){
						// console.log(";lidksfma");
						// console.log(blueQ[i][2]);
						// console.log(isNaN(blueQ[i][2]));
						if (!isNaN(blueQ[i][2])){
							var filterQ = blueQ[i][0]+" "+blueQ[i][1]+" "+blueQ[i][2];
							// console.log("WE IN");
							if(blueString === null){
								blueString=filterQ;
							}
							else 
							{
								blueString = blueString + " & " + filterQ;
							}
						}
					}
				}
			if (filters[1].length>0){
				var redQ = filters[1];
				for (var i=0; i<redQ.length; i++){
					if(redQ[i].length>0){
						if (!isNaN(redQ[i][2])){
						var filterQ = redQ[i][0]+" "+redQ[i][1]+" "+redQ[i][2];
						if(redString === null){
							redString=filterQ;
						}
						else 
						{
							redString = redString + " & " + filterQ;
						}
					}
					}
				}
			}
		}

			// for (var i = 0; i < recs.length; i++) {
			// 	var rec = recs[i];
			// 	var data = new google.visualization.DataTable();
			// 	// populate metadata from schema
			// 	data.addColumn("string", rec.x);
			// 	if (rec.type == "single") {
			// 		data.addColumn("number", "Query 1");
			// 	} else {
			// 		if (hasComparison) {
			// 			if(blueString!==null&&redString!==null){
			// 				data.addColumn("number", blueString);
			// 				data.addColumn("number", redString);		
			// 			}
			// 			else if (blueString===null&redString!==null)
			// 			{
			// 				data.addColumn("number", "Full");
			// 				data.addColumn("number", redString);
			// 			}
			// 			else
			// 			{
			// 				data.addColumn("number", "Query");
			// 				data.addColumn("number", "Full");	
			// 			}
			// 		} else {
			// 			if(blueString===null&&redString!==null){
			// 				data.addColumn("number", "Full");
			// 				data.addColumn("number", RedString);
			// 			}
			// 			else if (redString===null&&blueString!==null){
			// 				data.addColumn("number", blueString);
			// 				data.addColumn("number", "Full");
			// 			}
			// 			else
			// 			{
			// 				data.addColumn("number", "Query");
			// 				data.addColumn("number", "Full");	
			// 			}
			// 		}
			// 	}
			
			// populate metadata from schema

			if (agg == "NONE") {
				data.addColumn("number", x);
				// console.log("agg = NONE");
				// console.log("number added is ");
				// console.log(x);

			} else {
				data.addColumn(schemas[dataset][x], x);
				// console.log("agg = none else");
				// console.log("number added is "+schemas[dataset][x]);
				// console.log('haskd;lmf');
			}
			// console.log("schemas[dataset][y]");
			// console.log(schemas[dataset][y]);
			if (hasComparison) {
				if(blueString!==null&&redString!==null){
					data.addColumn(schemas[dataset][y], blueString);
					data.addColumn(schemas[dataset][y], redString);	
					// console.log("A");	
				}
				else if (blueString===null&redString!==null)
				{
					data.addColumn(schemas[dataset][y], "Full");
					data.addColumn(schemas[dataset][y], redString);
					// console.log("B");
				}
				else
				{
					data.addColumn(schemas[dataset][y], "Query");
					data.addColumn(schemas[dataset][y], "Full");	
					// console.log("C");
				}

				// data.addColumn(schemas[dataset][y], "Query 1");
				// data.addColumn(schemas[dataset][y], "Query 2");
				// console.log("HAS COMPARISON");
				// console.log(data);
				// console.log(schemas[dataset][y]);
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

			// console.log("New normalized data");
			// console.log(new_data);

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
			// console.log("agg value");
			// console.log(agg);
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

		// console.log(options.orig_data);

		//new data is the list of x and y data.

		// console.log("normdata");
		// console.log(new_data);

		var chart;
		if (agg == "NONE") {
			chart = new google.visualization.ScatterChart(document.getElementById('big_viz'));
		} else {
			chart = new google.visualization.ColumnChart(document.getElementById('big_viz'));
		}
		// console.log(chart);

		// console.log("plotted chart is");
		// console.log(chart);

		//in manualPlot
		var nowTime = Date.now();
		switch (markTime){
			case "zoomRecTime":
				var diff = nowTime - zoomRecTime;
				bugout.log({"action": "view_recommendation","chart_title" : previousChartInfo,"viewtime":diff, "time":Date.now()});
				zoomRecTime=null;
				markTime = "zoomManualPlotTime";
				previousChartInfo = options['title'];
				zoomBookMarkTime = nowTime;
				break;
			case "zoomBookMarkTime":
				var diff = nowTime - zoomBookMarkTime;
				bugout.log({"action": "view_bookmark", "chart_title":previousChartInfo,"viewtime":diff, "time":Date.now()});
				zoomBookMarkTime=null;
				markTime = "zoomManualPlotTime";
				previousChartInfo = options['title'];
				zoomRecTime = nowTime;
				break;
			case "zoomManualPlotTime":
				var diff = nowTime-zoomManualPlotTime;
				bugout.log({"action": "view_manualPlot","chart_title":previousChartInfo,"viewtime":diff, "time": Date.now()});
				zoomManualPlotTime = nowTime;
				previousChartInfo=options['title'];
				break;
			default:
				previousChartInfo=options['title'];
				markTime = "zoomManualPlotTime";
				zoomManualPlotTime=Date.now();
			};

		chart.draw(data, options);

		// console.log("THE DATA PASSED TO GCHART IS");
		// console.log(data);

		var vega_data = [];
		var data_to_parse = ret["rows2"];
		// console.log(data_to_parse);
		$.each(data_to_parse, function(index, entry){
				var group = {"x": entry[0], "y": entry[1]};
				vega_data.push(group);	
		});

		var vega = vega_builder(data_to_parse, options, hasComparison);
		// console.log("vega");
		// console.log(JSON.stringify(vega));
		// console.log(JSON.stringify(vega_data));
		var vegaMOO =
			{
			  "width": big_width,
			  "height": big_height,
			  "data": [
			    {
			      "name": "table",
			      "values":  vega_data
			    }
			  ],
			  //   "legends": [
			  //   {
			  //     "fill": "x",
			  //     "title": "things"
			  //   }
			  // ],
			  "signals": [
			    {
			      "name": "tooltip",
			      "init": {},
			      "streams": [
			        {
			          "type": "rect:mouseover",
			          "expr": "datum"
			        },
			        {
			          "type": "rect:mouseout",
			          "expr": "{}"
			        }
			      ]
			    }
			  ],
			  "predicates": [
			    {
			      "name": "tooltipP",
			      "type": "==",
			      "operands": [
			        {
			          "signal": "tooltip._id"
			        },
			        {
			          "arg": "id"
			        }
			      ]
			    }
			  ],
			  "scales": [
			    {
			      "name": "x",
			      "type": "linear",
			      "range": "width",
			      "domain": {
			        "data": "table",
			        "field": "x"
			      }
			    },
			    {
			      "name": "xscale",
			      "type": "ordinal",
			      "range": "width",
			      "domain": {
			        "data": "table",
			        "field": "x"
			      }
			    },
			    {
			      "name": "y",
			      "type": "linear",
			      "range": "height",
			      "domain": {
			        "data": "table",
			        "field": "y"
			      },
			      "nice": true
			    }
			  ],
			  "axes": [
			    {
			      "type": "x",
			      "scale": "x",
			      "title": options.x
			    },
			    {
			      "type": "y",
			      "scale": "y",
			      "title": options.y
			    }
			  ],
			  "marks": [
			    {
			      "type": "rect",
			      "from": {
			        "data": "table"
			      },
			      "properties": {
			        "enter": {
			          "x": {
			            "scale": "x",
			            "field": "x"
			          },
			          "width": {
			            // "value": 1
			            "scale":"xscale",
			            "band":"true",
			            "mult":"0.50"
			            // "value":0
			          },
			          "y": {
			            "scale": "y",
			            "field": "y"
			          },
			          "y2": {
			            "scale": "y",
			            "value": 0
			          }
			        },
			        "update": {
			          "fill": {
			            "rule": [
			              {
			                "predicate": {
			                  "name": "tooltipP",
			                  "id": {
			                    "field": "_id"
			                  }
			                },
			                "value": "red"
			              },
			              {
			                "value": "steelblue"
			              }
			            ]
			          }
			        }
			      }
			    },
			    {
			      "type": "text",
			      "properties": {
			        "enter": {
			          "align": {
			            "value": "center"
			          },
			          "fill": {
			            "value": "#333"
			          }
			        },
			        "update": {
			          "x": {
			            "scale": "x",
			            "signal": "tooltip.x"
			          },
			          "y": {
			            "scale": "y",
			            "signal": "tooltip.y",
			            "offset": -5
			          },
			          "text": {
			            "signal": "tooltip.y",
			            "template": "{{datum.x}} and {{datum.y}}"
			          },
			          "fillOpacity": {
			            "rule": [
			              {
			                "predicate": {
			                  "name": "tooltipP",
			                  "id": {
			                    "value": null
			                  }
			                },
			                "value": 0
			              },
			              {
			                "value": 1
			              }
			            ]
			          }
			        }
			      }
			    }
			  ]
			};
		// console.log(vegaMOO);

		// if (agg=="NONE"){
		// 	vega["signals"]=[
		// 	    {
		// 	      "name": "tooltip",
		// 	      "init": {},
		// 	      "streams": [
		// 	        {"type": "rect:mouseover", "expr": "datum"},
		// 	        {"type": "rect:mouseout", "expr": "{}"}
		// 	      ]
		// 	    }
		// 	  ];
		// 	vega["predicates"]=[
		// 	    {
		// 	      "name": "tooltip", "type": "==", 
		// 	      "operands": [{"signal": "tooltip._id"}, {"arg": "id"}]
		// 	    }
		// 	];
		// 	vega["marks"]=
		// 	[
		// 	    {
		// 	      "type": "symbol",
		// 	      "from": {"data": "table"},
		// 	      "properties": {
		// 	        "enter": {
		// 	          "x": {"scale": "x", "field": "x"},
		// 	          "y": {"scale": "y", "field": "y"},
		// 	          "y2": {"scale": "y", "value": 0}
		// 	        },
		// 	        "update": {
		// 	          "fill": {
		// 	            "rule": [
		// 	              {
		// 	                "predicate": {
		// 	                  "name": "tooltip",
		// 	                  "id": {"field": "_id"}
		// 	                },
		// 	                "value": "red"
		// 	              },
		// 	              {"value": "steelblue"}
		// 	            ]
		// 	          }
		// 	        }
		// 	      }
		// 	    },
		// 	    {
		// 	      "type": "text",
		// 	      "properties": {
		// 	        "enter": {
		// 	          "align": {"value": "center"},
		// 	          "fill": {"value": "#333"}
		// 	        },
		// 	        "update": {
		// 	          "x": {"scale": "x", "signal": "tooltip.x"},
		// 	          "y": {"scale": "y", "signal": "tooltip.y", "offset": -5},
		// 	          "text": {"signal": "tooltip.y"},
		// 	          "fillOpacity": {
		// 	            "rule": [
		// 	              {
		// 	                "predicate": {
		// 	                  "name": "tooltip",
		// 	                  "id": {"value": null}
		// 	                },
		// 	                "value": 0
		// 	              },
		// 	              {"value": 1}
		// 	            ]
		// 	          }
		// 	        }
		// 	      }
		// 	    }
		// 	  ];
		// } else {
		// 	vega["signals"]=[
		// 	    {
		// 	      "name": "tooltip",
		// 	      "init": {},
		// 	      "streams": [
		// 	        {"type": "rect:mouseover", "expr": "datum"},
		// 	        {"type": "rect:mouseout", "expr": "{}"}
		// 	      ]
		// 	    }
		// 	  ];
		// 	vega["predicates"]=[
		// 	    {
		// 	      "name": "tooltip", "type": "==", 
		// 	      "operands": [{"signal": "tooltip._id"}, {"arg": "id"}]
		// 	    }
		// 	];
		// 	vega["marks"]=
		// 	[
		// 	    {
		// 	      "type": "rect",
		// 	      "from": {"data": "table"},
		// 	      "properties": {
		// 	        "enter": {
		// 	          "x": {"scale": "x", "field": "x"},
		// 	          "width": {"value":1},
		// 	          "y": {"scale": "y", "field": "y"},
		// 	          "y2": {"scale": "y", "value": 0}
		// 	        },
		// 	        "update": {
		// 	          "fill": {
		// 	            "rule": [
		// 	              {
		// 	                "predicate": {
		// 	                  "name": "tooltip",
		// 	                  "id": {"field": "_id"}
		// 	                },
		// 	                "value": "red"
		// 	              },
		// 	              {"value": "steelblue"}
		// 	            ]
		// 	          }
		// 	        }
		// 	      }
		// 	    },
		// 	    {
		// 	      "type": "text",
		// 	      "properties": {
		// 	        "enter": {
		// 	          "align": {"value": "center"},
		// 	          "fill": {"value": "#333"}
		// 	        },
		// 	        "update": {
		// 	          "x": {"scale": "x", "signal": "tooltip.x"},
		// 	          "y": {"scale": "y", "signal": "tooltip.y", "offset": -5},
		// 	          "text": {"signal": "tooltip.y"},
		// 	          "fillOpacity": {
		// 	            "rule": [
		// 	              {
		// 	                "predicate": {
		// 	                  "name": "tooltip",
		// 	                  "id": {"value": null}
		// 	                },
		// 	                "value": 0
		// 	              },
		// 	              {"value": 1}
		// 	            ]
		// 	          }
		// 	        }
		// 	      }
		// 	    }
		// 	  ];
		// 	}

		// console.log(vega);

		parse(vega);
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
		$(this).closest('.attributeFilter' + idx).remove();
		e.preventDefault();
	});
	e.preventDefault();


});

function parse(spec) {
	vg.parse.spec(spec, function(chart) { chart({el:"#vis"}).update(); });
};

/*
MANUAL:
var options = {
	'title': title,
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

Get Recommendations:
// Set chart options
var options = {
	'title': title,
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


				//in rec time;, call for large and small
				// Set chart options
				var options = {
					'title': title,
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

							var options = $("#big_viz").data('image_options');
bookmark zoom and store
*/
function vega_builder(data, options, hasComparison){
	var vega = {};
	vega["width"]=options.width;
	vega["height"]=options.height;
	var vega_data=[];

	//converting data to vega data reading format
	if (hasComparison){
		$.each(data, function(index, entry){
				var group = {"position": entry[0], "category": "y1", "value": entry[1]};
				vega_data.push(group);	
				var group2 = {"position": entry[0], "category": "y2", "value": entry[2]};
				vega_data.push(group2);	

		});
	}else {
		$.each(data, function(index, entry){
				var group = {"category": entry[0], "position": "y1", "value": entry[1]};
				vega_data.push(group);	
		});
	}
	console.log("alksdjfa;dfjs;alskdjf;aljkf;LKDATSATATATTT");
	console.log(JSON.stringify(vega_data));

	//grouping data for vega json and adding it in
	var data_grouping = [];
	data_grouping.push({
		"name":"table",
		"values":vega_data
	});
	vega["data"]=data_grouping;


		
	vega["signals"] = [{
		  "name": "tooltip",
		  "init": {},
		  "streams": [
		    {
		      "type": "rect:mouseover",
		      "expr": "datum"
		    },
		    {
		      "type": "rect:mouseout",
		      "expr": "{}"
		    }
		  ]
		}];

	vega["predicates"] = [{
      "name": "tooltipP",
      "type": "==",
      "operands": [
        {
          "signal": "tooltip._id"
        },
        {
          "arg": "id"
        }
      ]
    }];
	vega["scales"] = [
		    {
		      "name": "x1",
		      "type": "linear",
		      "range": "width",
		      "domain": {
		        "data": "table",
		        "field": "x1"
		      }
		    },
		    {
		      "name": "x2",
		      "type": "linear",
		      "range": "width",
		      "domain": {
		        "data": "table",
		        "field": "x2"
		      }
		    },
		    {
		      "name": "y",
		      "type": "linear",
		      "range": "height",
		      "domain": {
		        "data": "table",
		        "field": "y"
		      },
		      "nice": true
		    }
		  ];

	vega["axes"] = [
		    {
		      "type": "x",
		      "scale": "x1",
		      "title": options.x
		    },
		    {
		      "type": "y",
		      "scale": "y",
		      "title": options.y
		    }
		  ];
	if(hasComparison){
		var m = vega["axes"];
		m.push({
			"type":"x",
			"scale":"x2",
		});
		vega["axes"]=m;
	}

	vega["marks"] = [
		    {
		      "type": "rect",
		      "from": {
		        "data": "table"
		      },
		      "properties": {
		        "enter": {
		          "x": {
		            "scale": "x",
		            "field": "x"
		          },
		          "width": {
		            "value": 1
		          },
		          "y": {
		            "scale": "y",
		            "field": "y"
		          },
		          "y2": {
		            "scale": "y",
		            "value": 0
		          }
		        },
		        "update": {
		          "fill": {
		            "rule": [
		              {
		                "predicate": {
		                  "name": "tooltipP",
		                  "id": {
		                    "field": "_id"
		                  }
		                },
		                "value": "red"
		              },
		              {
		                "value": "steelblue"
		              }
		            ]
		          }
		        }
		      }
		    },
		    {
		      "type": "text",
		      "properties": {
		        "enter": {
		          "align": {
		            "value": "center"
		          },
		          "fill": {
		            "value": "#333"
		          }
		        },
		        "update": {
		          "x": {
		            "scale": "x",
		            "signal": "tooltip.x"
		          },
		          "y": {
		            "scale": "y",
		            "signal": "tooltip.y",
		            "offset": -5
		          },
		          "text": {
		            "signal": "tooltip.y",
		            "template": "{{datum.x}} and {{datum.y}}"
		          },
		          "fillOpacity": {
		            "rule": [
		              {
		                "predicate": {
		                  "name": "tooltipP",
		                  "id": {
		                    "value": null
		                  }
		                },
		                "value": 0
		              },
		              {
		                "value": 1
		              }
		            ]
		          }
		        }
		      }
		    }
		  ];
	// console.log(JSON.stringify(vega));

	return vega;

}
	

});















