var express = require('express');
var router = express.Router();
var datasets = require('./datasets')
var schemas = require('./schemas2')
/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express'});
});

/* GET Hello World page. */
router.get('/seedb', function(req, res) {
	// read in data and store
    res.render('seedb', { title: 'SeeDB', schemas: schemas})
});

router.get('/seedb_manual', function(req, res) {
	res.render('seedb-manual-only', {title: 'SeeDB'})
});

router.get('/seedb_rr', function(req, res) {
	res.render('seedb-random', {title: 'SeeDB'})
});

router.get('/seedb_single', function(req, res) {
	res.render('seedb-single', {title: 'SeeDB'})
});


router.get('/seedb_all', function(req, res) {
	res.render('seedb-all', {title: 'SeeDB'})
});

router.post('/getRecommendations', function(req, res) {
	//rec_type = req.body.rec_type;
	table = datasets.datasets[req.body.dataset].data;
	//console.log(table);
	order = datasets.datasets[req.body.dataset].order;
	console.log(order);
	col_types = datasets.datasets[req.body.dataset].type;

	//Removes the uninterested attributes for recommendation making
	var to_remove = JSON.parse(req.body.remove_attrs);
	//TO HOOK UP ATTRIBUTE SELECTIONS, MUST REMOVE FROM THE COLUMN TYPE OBJECT
	to_remove.forEach(function(attr){
		delete col_types[attr];
		//console.log(attr);
	});

	hasComparison = (req.body.hasComparison == 'true');
	agg = req.body.agg;
	filters = JSON.parse(req.body.filters);
	//console.log(filters);
	rec_type = req.body.rec_type;
	// filter has format: [[q1_filters], [q2_filters]]
	// q_filters is an array of format [attr_name, operator, value]

	var attrs = {};

	for (var i = 0; i < filters.length; i++) {
		for (var j = 0; j < filters[i].length; j++) {
			filters[i][j][0] = order[filters[i][j][0]];
			attrs[filters[i][j][0]] = true;
		}	
	}
	//console.log(attrs);

	// collect all measures and dimensions
	var measures = [];
	var dimensions = [];
	for (t in col_types) {
		var tmp = order[t];
		if (tmp in attrs) {
			continue;
		}
		if (col_types[t] == "dimension") {
			dimensions.push(order[t]);
		} else if (col_types[t] == "measure") {
			measures.push(order[t]);
		}
	}
	//console.log(measures);
	//console.log(dimensions);

	var views = {};
	// go through each row
	for (var i = 0; i < table.length; i++) {
		var processRow = [true, true];
		// apply filters
		for (var k = 0; k < filters.length; k++) {
			for (var j = 0; j < filters[k].length; j++) {
				if (!passesFilter(filters[k][j][1], filters[k][j][2], 
					table[i][filters[k][j][0]])) {
					processRow[k] = false;
					break;
				}
			}
		}

		//console.log(processRow);

		// views has form dimension__measure --> dict [dim : [measure_sum, measure_count]]
		for (var m = 0; m < measures.length; m++) {
			for (var d = 0; d < dimensions.length; d++) {
				var viewKey = dimensions[d] + "__" + measures[m];
				if (!(viewKey in views)) {
					views[viewKey] = {};
				}

				var key = table[i][dimensions[d]];
				var value = table[i][measures[m]];

				if (!(key in views[viewKey])) {
					//console.log(Object.keys(views));
					//console.log("inserted: " + viewKey + "::" + key);
					views[viewKey][key] =  [[0, 0], [0, 0]];
				}
				for (var p = 0; p < processRow.length; p++) {
					if (processRow[p]) {
						var old = views[viewKey][key][p];
						views[viewKey][key][p][0] = old[0] + value;
						views[viewKey][key][p][1] = old[1] + 1; 
						//console.log([viewKey, key, views[viewKey][key][p][0], 
						//	views[viewKey][key][p][1]]);
					}
				}
			}
		}
	}


	// get the view distributions into the right form
	var intermediates = [];
	// format is dimension__measure__agg --> [[key,a1, a2] [,,] ...]

	var count_done = {};
	for (viewKey in views) {
		var aggs = ["COUNT", "AVG", "SUM"]; // removed sum. users can ask for sum separately
		for (var i = 0; i < aggs.length; i++) {
			// get dimension
			dim = viewKey.split('_')[0];
			console.log(dim);
			
			if (i == 0) {
				if (dim in count_done) {
					continue;
				} else {
					count_done[dim] = true;
				}
				var finalKey = dim + "_*" + "__" + aggs[i];	
			} else {
				var finalKey = viewKey + "__" + aggs[i];
			}
			console.log(finalKey);
			var dist = [];
			for (key in views[viewKey]) {
				dist.push([key, 
					applyAggregate(aggs[i], views[viewKey][key][0]),
					applyAggregate(aggs[i], views[viewKey][key][1])]);
			}

			// compute utility based on distributions
			// normalize distributions
			var dist_tmp = dist.slice(0);	
			var total = [0, 0];
			for (var j = 0; j < dist.length; j++) {
				total[0] = total[0] + dist[j][1];
				total[1] = total[1] + dist[j][2];
			}
			
			// EMD
			var utility = 0;
			for (var j = 0; j < dist.length; j++) {
				utility += Math.abs(dist_tmp[j][1]/total[0] - dist_tmp[j][2]/total[1]);
			}
			
			var x, y;
			for (o in order) {
				if (order[o] == parseInt(viewKey.split("__")[0])) {
					x = o;
				} else if (order[o] == parseInt(viewKey.split("__")[1])) {
					y = o;
				}
			}

			var ret = {
				x : x, // turn this into a string
				y : y,
				agg : aggs[i],
				dist : dist,
				utility : utility,
				type : "comparative"
			};
			intermediates.push(ret);				
		}
	}

	if (rec_type == "seedb") {
		intermediates.sort(function (a, b) {
			if (a.utility > b.utility) {
				return -1;
			} else if (a.utility < b.utility) {
				return 1;
			} else {
				return 0;
			}
		});
	} else if (rec_type == "seedb_random") {
		intermediates.sort(function (a, b) {
			n1 = Math.random();
			n2 = Math.random();
			if (n1.utility > n2.utility) {
				return -1;
			} else if (n1.utility < n2.utility) {
				return 1;
			} else {
				return 0;
			}
		});

	} else if (rec_type == "seedb_single") {
		intermediates.sort(function (a, b) {
			if (a.utility > b.utility) {
				return -1;
			} else if (a.utility < b.utility) {
				return 1;
			} else {
				return 0;
			}
		});

		// get some single dimensional graphs
		for (var m = 0; m < intermediates.length; m++) {
			if (m % 2 == 0) {
				tmp = intermediates[m].dist;
				for (var n = 0; n < tmp.length; n++) {
					tmp[n] = tmp[n].slice(0, 2);
				}
				intermediates[m].type = "single";
			}
		}
	} else if (rec_type == "all") {
		intermediates.sort(function (a, b) {
			n1 = Math.random();
			n2 = Math.random();
			if (n1.utility > n2.utility) {
				return -1;
			} else if (n1.utility < n2.utility) {
				return 1;
			} else {
				return 0;
			}
		});
	}

	var tmp;
	if (rec_type == "all") {
		tmp = intermediates;
	} else {
		var tmp = intermediates.slice(0, 15);
		for (var m = 0; m < tmp.length; m++) {
			console.log([intermediates[m].utility, intermediates[m].type]);
		}
	}

	tmp = JSON.stringify(Array.prototype.slice.call(tmp));
	
	res.send(tmp); 
});

router.post('/manualPlot', function(req, res) {
    // technically get data
    var data = {};
    
    table = datasets.datasets[req.body.dataset].data;
	order = datasets.datasets[req.body.dataset].order;
	col_types = datasets.datasets[req.body.dataset].type;
	x_idx = order[req.body.x];
	y_idx = order[req.body.y];
	hasComparison = (req.body.hasComparison == 'true');
	agg = req.body.agg;
	filters = JSON.parse(req.body.filters);
	// filter has format: [[q1_filters], [q2_filters]]
	// q_filters is an array of format [attr_name, operator, value]

	for (var i = 0; i < filters.length; i++) {
		for (var j = 0; j < filters[i].length; j++) {
			filters[i][j][0] = order[filters[i][j][0]];
		}	
	}

	console.log(filters);
	console.log(req.body);
	
	// format of answer is key: [[q1_sum, q1_count], [q2_sum, q2_count]]
	var answer = {};
	var answer_no_agg = [];
	
	// go through each row
	for (var i = 0; i < table.length; i++) {
		var key = table[i][x_idx];

		// if key is not in hash, initialize
		if (!(key in answer)) {
			answer[key] = [[0, 0], [0, 0]];
		}

		var processRow = [true, true];
		// apply filters
		for (var k = 0; k < filters.length; k++) {
			for (var j = 0; j < filters[k].length; j++) {
				if (!passesFilter(filters[k][j][1], filters[k][j][2], 
					table[i][filters[k][j][0]])) {
					processRow[k] = false;
					break;
				}
			}
		}

		var key = table[i][x_idx];
		var value = parseFloat(table[i][y_idx]);

		var no_agg = [key,null , null];
		for (var k = 0; k <filters.length; k++) {
			if (processRow[k]) {
				if (agg == "NONE") {
					no_agg[k+1] = value;
					continue;
				}
				var old = answer[key][k];
				answer[key][k][0] = old[0] + value;
				answer[key][k][1] = old[1] + 1; 
			}
		}
		answer_no_agg.push(no_agg);
	}
	
	var ret= [];
	if (agg != "NONE") {
		for (var key in answer) {
			if (hasComparison) {
				ret.push([key, applyAggregate(agg, answer[key][0]), 
					applyAggregate(agg, answer[key][1])]);
			} else {
				ret.push([key, applyAggregate(agg, answer[key][0])]);
			}
		}
	} else {
		for (var k = 0; k < answer_no_agg.length; k++) {
			if (hasComparison) {
				ret.push(answer_no_agg[k]);
			} else {
				ret.push([answer_no_agg[k][0], answer_no_agg[k][1]]);
			}
		}
	}
	
	data["rows2"] = ret;
	// then populate the correct div
    res.send(JSON.stringify(data));   
});

function applyAggregate(agg, values) {
	if (agg == "COUNT") {
		return values[1];
	} else if (agg == "SUM") {
		return values[0];
	} else if (agg == "AVG") {
		if ((values[0] == 0) || (values[1] == 0)) return 0;
		return values[0]/values[1];
	}
}

function passesFilter(operator, expected, actual) {
	//console.log([operator, expected, actual]);
	//console.log(expected != actual);
	if (operator == "=") {
		return (actual == expected);
	} else if (operator == "<=") {
		return (actual <= expected);
	} else if (operator == ">=") {
		return (actual >= expected);
	} else if (operator == "!=") {
		return (actual != expected);
	} else if (operator == "<") {
		return (actual < expected);
	} else if (operator == ">") {
		return (actual > expected);
	}
}

module.exports = router;
