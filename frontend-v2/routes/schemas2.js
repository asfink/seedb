schemas2 = {
	// "test_data1" : 
	// {
	// 	"item" : "string",
	// 	"price" : "number",
	// 	"amount" : "number"
	// },
	// "test_data2" :
	// {
	// 	"city" : "string",
	// 	"population" : "number"
	// },
	"cars" : 
	{
		"mpg" : "number",
		"cylinders" : "number",
		"displacement" : "number",
		"horsepower" : "number",
		"weight" : "number",
		"acceleration" : "number",
		"model-year" : "number",
		"origin" : "number",
		"name" : "string"
	},
	"cars_type" :
	{
		"mpg" : "measure",
		"cylinders" : "dimension",
		"displacement" : "measure",
		"horsepower" : "measure",
		"weight" : "measure",
		"acceleration" : "measure",
		"model-year" : "dimension",
		"origin" : "dimension",
		"name" : "dimension"
	},
	// "cars_subset" : 
	// {
	// 	"mpg" : "number",
	// 	"cylinders" : "number",
	// 	"displacement" : "number",
	// 	"horsepower" : "number",
	// 	"weight" : "number",
	// 	"acceleration" : "number",
	// 	"model-year" : "number",
	// 	"origin" : "number",
	// 	"name" : "string"
	// },
	"movies" : 
	{
		"Title" : "string",
		"US_Gross" : "number",
		"Worldwide_Gross" : "number",
		"US_DVD_Sales" : "number",
		"Production_Budget" : "number",
		//"Release_Date" : "string",
		"MPAA_Rating" : "string",
		"Running_Time_min" : "number",
		"Distributor" : "string",
		"Source" : "string",
		"Major_Genre" : "string",
		"Creative_Type" : "string",
		"Director" : "string",
		"Rotten_Tomatoes_Rating" : "number",
		"IMDB_Rating" : "number",
		"IMDB_Votes" : "number"
	},
	"movies_type" :
	{
		"Title" : "",
		"US_Gross" : "measure",
		"Worldwide_Gross" : "measure",
		"US_DVD_Sales" : "measure",
		"Production_Budget" : "measure",
		"Release_Date" : "",
		"MPAA_Rating" : "dimension",
		"Running_Time_min" : "measure",
		"Distributor" : "dimension",
		"Source" : "dimension",
		"Major_Genre" : "dimension",
		"Creative_Type" : "dimension",
		"Director" : "",
		"Rotten_Tomatoes_Rating" : "measure",
		"IMDB_Rating" : "measure",
		"IMDB_Votes" : "measure"
	},
	"olympics" :
	{
		'Athlete' : "string", 
		'Age' : "number",
		'Country' : "string",
		'Year' : "number",
		'Closing Ceremony Date' : "string",
		'Sport' : "string",
		'Gold Medals' : "number",
		'Silver Medals' : "number",
		'Bronze Medals' : "number",
		'Total Medals' : "number"
	},
	"olympics_type" : {
		'Athlete' : "", 
		'Age' : "measure",
		'Country' : "dimension",
		'Year' : "dimension",
		'Closing Ceremony Date' : "",
		'Sport' : "dimension",
		'Gold Medals' : "measure",
		'Silver Medals' : "measure",
		'Bronze Medals' : "measure",
		'Total Medals' : "measure"
	},
	"birdstrikes" : 
	{
		"Airport__Name": "string",
		"Aircraft__Make_Model": "string",
		"Effect__Amount_of_damage": "string",
		"Flight_Date": "string",
		"Aircraft__Airline_Operator": "string",
		"Origin_State": "string",
		"When__Phase_of_flight": "string",
		"Wildlife__Size":"string",
		"Wildlife__Species": "string",
		"When__Time_of_day":"string",
		"Cost__Other":"number",
		"Cost__Repair":"number",
		"Cost__Total_$":"number",
		"Speed_IAS_in_knots":"number"
	},
	"birdstrikes_type" : {
		"Airport__Name": "",
		"Aircraft__Make_Model": "",
		"Effect__Amount_of_damage": "dimension",
		"Flight_Date": "",
		"Aircraft__Airline_Operator": "dimension",
		"Origin_State": "",
		"When__Phase_of_flight": "dimension",
		"Wildlife__Size":"dimension",
		"Wildlife__Species": "dimension",
		"When__Time_of_day":"dimension",
		"Cost__Other":"measure",
		"Cost__Repair":"measure",
		"Cost__Total_$":"measure",
		"Speed_IAS_in_knots":"measure"
	},
	"census" : 
	{
		"age" : "number",
		"workclass" : "string",
		"education" : "string",
		"education-num" : "number", 
		"marital-status" :"string",
		"occupation" : "string",
		"relationship" : "string",
		"race" : "string",
		"sex" : "string",
		"capital-gain" : "number",
		"capital-loss" : "number",
		"hours-per-week" : "number",
		"native-country" : "string",
		"income-category" : "string"
	},
	"census_type" : {
		"age" : "measure",
		"workclass" : "dimension",
		"education" : "dimension",
		"education-num" : "", 
		"marital-status" :"dimension",
		"occupation" : "",
		"relationship" : "",
		"race" : "dimension",
		"sex" : "dimension",
		"capital-gain" : "measure",
		"capital-loss" : "measure",
		"hours-per-week" : "measure",
		"native-country" : "",
		"income-category" : "dimension"
	},
	"bike_sharing" : 
	{
		"season" : "string",
		"year" : "number",
		"month" : "number",
		"isHoliday" : "number",
		"Weekday" : "number",
		"isWorkingDay" : "number",
		"Weather" : "string",
		"Temp" : "number",
		"Humidity" : "number",
		"windspeed" : "number",
		"Casual_users" : "number",
		"Registered_users" : "number",
		"Total_users" : "number"
	},
	"bike_sharing_type" : {
		"season" : "dimension",
		"year" : "dimension",
		"month" : "",
		"isHoliday" : "dimension",
		"Weekday" : "dimension",
		"isWorkingDay" : "dimension",
		"Weather" : "dimension",
		"Temp" : "measure",
		"Humidity" : "measure",
		"windspeed" : "measure",
		"Casual_users" : "measure",
		"Registered_users" : "measure",
		"Total_users" : "measure"
	},
	"sales" :
	{
		"Order Priority" : "string",
		"Order Quantity" : "number",
		"Sales" : "number",
		"Discount" : "number",
		"Ship Mode" : "string",
		"Profit" : "number",
		"Unit Price" : "number",
		"Shipping Cost" : "number",
		"Province" : "string",
		"Region" : "string",
		"Customer Segment" : "string",
		"Product Category" : "string",
		"Sub-Category" : "string",
		"Container" : "string",
		"Base Margin" : "number"
	},
	"sales_type" : {
		"Order Priority" : "dimension",
		"Order Quantity" : "measure",
		"Sales" : "measure",
		"Discount" : "measure",
		"Ship Mode" : "",
		"Profit" : "measure",
		"Unit Price" : "measure",
		"Shipping Cost" : "",
		"Province" : "dimension",
		"Region" : "dimension",
		"Customer Segment" : "dimension",
		"Product Category" : "dimension",
		"Sub-Category" : "dimension",
		"Container" : "dimension",
		"Base Margin" : "measure"
	},
	"housing" : {
		"crime rate" : "number",
		"zoned land" : "number",
		"industrial zone" : "number",
		"next to charles" : "number",
		"nitric oxide conc" : "number",
		"num rooms" : "number",
		"owner occupied percentage" : "number",
		"distance to employment areas" : "number",
		"accessiblity to highways" : "number",
		"property tax rate" : "number",
		"pipul-teacher ratio" : "number",
		"racial diversity" : "number",
		"lower income percentage" : "number",
		"median value of home" : "number"
	},
	"housing_type" : {
		"crime rate" : "measure",
		"zoned land" : "measure",
		"industrial zone" : "measure",
		"next to charles" : "dimension",
		"nitric oxide conc" : "measure",
		"num rooms" : "dimension",
		"owner occupied percentage" : "measure",
		"distance to employment areas" : "measure",
		"accessiblity to highways" : "",
		"property tax rate" : "measure",
		"pipul-teacher ratio" : "measure",
		"racial diversity" : "measure",
		"lower income percentage" : "measure",
		"median value of home" : "dimension"
	}
};

module.exports=schemas2;