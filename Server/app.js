const express = require('express')
const cors = require('cors')
const { spawn } = require('child_process')
const multer = require('multer')
const bodyParser = require('body-parser')
const MongoClient = require('mongodb').MongoClient;
const dfd = require('danfojs-node')
const session = require('express-session');

var app = express()
var jsonParser = bodyParser.json()
var url = "mongodb://localhost:27017/";
var sess

app.use(cors({ origin: "http://localhost:4200", credentials: true }))
app.use(session({ resave: true, secret: '123456', saveUninitialized: true }));
app.use(jsonParser)

const DIR = './uploads';

const storage = multer.diskStorage({
	destination: (req, file, cb) => {
		cb(null, DIR);
	},
	filename: (req, file, cb) => {
		cb(null, file.originalname);
	}
});

app.post('/api/uploadFile', function (req, res) {
	sess = req.session;
	var uploadPost = multer({ storage: storage }).single('file');
	uploadPost(req, res, function (err) {
		if (err) {
			return res.end("error uploading file");
		}

		let filePath = req.file.path
		sess.filePath = filePath
		toSend = []

		dfd.read_csv(filePath)
			.then(df => {
				columns = df.column_names
				dataTypes = df.ctypes.data
				uniqueValues = df.nunique().data
				missingValues = df.isna().sum().data
				descriptiveStats = df.describe().col_data
				for (let i = 0, j = 0; i < columns.length; i++) {
					if (dataTypes[i] === "string") {
						j++
						toSend.push({
							name: columns[i],
							type: dataTypes[i],
							unique: uniqueValues[i],
							missing: missingValues[i],
							mean: '',
							std: '',
							min: '',
							max: '',
							labels: df[columns[i]].unique().data
						})
					} else {
						toSend.push({
							name: columns[i],
							type: dataTypes[i],
							unique: uniqueValues[i],
							missing: missingValues[i],
							mean: descriptiveStats[i - j][1],
							std: descriptiveStats[i - j][2],
							min: descriptiveStats[i - j][3],
							max: descriptiveStats[i - j][5],
							labels: []
						})
					}
				}
				res.send(JSON.stringify(toSend))
			}).catch(err => {
				console.log(err);
			})
	});
});

app.post('/api/filterData', function (req, res) {
	sess = req.session
	data = req.body
	var columns_to_select = []
	values_to_fill = []
	console.log(sess.filePath)

	dfd.read_csv(sess.filePath)
		.then(df => {
			for (let key of Object.keys(data)) {
				if (data[key][0] == true) {
					columns_to_select.push(key)
					if (data[key][2] !== "string") {
						if (data[key][1] == "mean")
							values_to_fill.push(df[key].mean())
						else
							values_to_fill.push(df[key].median())
					} else {
						values_to_fill.push("None")
					}
				}
			}
			if (columns_to_select.length != 0) {
				df = df.loc({ columns: columns_to_select })
				df = df.fillna({ columns: columns_to_select, values: values_to_fill })
				console.log(df.isna().sum().data)
				toReturn = {}
				for (let i = 0; i < columns_to_select.length; i++) {
					toReturn[columns_to_select[i]] = df[columns_to_select[i]].data
				}
				res.send(toReturn)
			} else {
				res.send({ msg: "No coulmn selected" })
			}
		}).catch(err => {
			console.log(err);
			res.send({ response: 'Error' })
		})
})

app.post('/runScript', function (req, res) {
	dataToPass = req.body
	//var dataToPass = { file_name: 'COVID-19 Data.csv', columns_to_select: ['day', 'month', 'year'] }
	const python = spawn('python', ['Python/script.py'])

	python.stdin.write(JSON.stringify(dataToPass))
	python.stdin.end()

	var parsedData = "";
	python.stdout.on('data', function (data) {
		//console.log("Data printed", data.toString())
		parsedData += data.toString()
	})

	python.stderr.on('data', function (data) {
		console.log(data.toString())
	});

	python.stdout.on('end', function () {
		console.log(parsedData, "END")
		res.send({ imagePath: parsedData })
	})

	python.on('exit', (code) => {
		console.log("Process quit with code : " + code);
	});
})

app.post('/signup', function (req, res) {
	console.log(req.body)
	var data = req.body
	MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
		if (err) {
			console.log('error')
			res.send({ text: undefined, error: err })
		} else {
			var database = client.db("FYP")
			database.collection("Users").findOne({userName: data.userName}, function (err, result) {
				console.log(result)
				if (!result) {
					database.collection("Users").insertOne(data, function (err, result) {
						if (err) {
							console.log(err)
						}
						client.close();
						res.send({ text: "OK", error: "None" })
					});
				} else {
					res.send({ text: "Username already exists", error: "None" })
				}
			})
		}
	});
})

app.post('/signin', function (req, res) {
	sess = req.session
	if (sess.userName) {
		res.send({ text: "Already Logged in" , error: "None"})
	} else {
		console.log(req.body)
		var data = req.body
		MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
			if (err) {
				res.send({text:"None", error: err })
			} else {
				var database = client.db("FYP")
				const query = {userName: data.userName}
				database.collection("Users").findOne(query, function (err, result) {
					console.log(result)
					dataToReturnInResponse = { text: "" , error: "None"}
					if (result) {
						if (result.password == data.password) {
							dataToReturnInResponse.text = "Logged in"
							sess.userName = data.userName
							sess.user_database_id = result._id
						} else
							dataToReturnInResponse.text = "Wrong Password"
					} else {
						dataToReturnInResponse.text = "User does not exist"
					}
					client.close()
					res.send(dataToReturnInResponse)
				})
			}
		});
	}
})

app.use(express.static(__dirname));

app.listen(3000, function () {
	console.log("server running")
})