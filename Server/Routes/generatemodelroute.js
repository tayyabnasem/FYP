const router = require('express').Router();
const MongoClient = require('mongodb').MongoClient;
const ObjectId = require('mongodb').ObjectId

const url = "mongodb://localhost:27017/";

const { spawn } = require('child_process');

router.post('/', function (req, res) {
	var data = req.body
	console.log("Model Received: ",data)
	const python = spawn('python', ['Python Scripts/createmodel.py'])
	python.stdin.write(req.session.preprocessed_data_path + '\n')
	python.stdin.write(JSON.stringify(data))
	python.stdin.end()

	var parsedData = "";
	python.stdout.on('data', function (data) {
		parsedData = data.toString()
		console.log("Data printed", data)
		//req.io.emit('logs', data)
	})

	python.stderr.on('data', function (data) {
		console.log(data.toString())
	});

	python.stdout.on('end', function () {
		var file_path = parsedData.replace("\r\n", "");
		console.log("Updated File path:", file_path)
		const python1 = spawn('python', [file_path])
		python1.stdout.on('data', (data) => {
			data = data.toString()
			if (data.includes("Training Complete...")) {
				//console.log(JSON.stringify(data))
				let temp_var = data.split("\r\n")
				//console.log(temp_var)
				trained_model = temp_var[0]
				data = temp_var[1]
				//console.log(trained_model, "jjj", data)
				req.io.emit('logs', data)
				MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
					if (err) {
						res.send({ text: "None", error: err })
					} else {
						var database = client.db("FYP")
						try {
							console.log("Query model", req.query)
							const query = { _id: new ObjectId(req.query.project) }
							database.collection("Projects").updateOne(query, { $set: { "trained_model": trained_model, "model_file": file_path } }, (err, result) => {
								client.close()
								if (err) {
									console.log(err)
								}
							})
						} catch (Exception) {
							console.log(Exception)
						}
					}
				});

			} else {
				data = data.replace("\r\n", "")
				let temp = data.split(":")
				//console.log(temp)
				if (temp[0] == 'Model Accuracy') {
					req.io.emit('model_accuracy', temp[1].slice(1, -10))
					req.io.emit('model_loss', temp[2].slice(1, -2))
				} else {
					req.io.emit('logs', data)
				}
			}
		})

		python1.stdout.on('end', () => {
			data = data.toString()
			res.send({ text: "OK" })
		})

		python1.stderr.on('data', (data) => {
			//console.log("Error: ",data.toString())
			data = data.toString()
			//data = data.split('\n')
			req.io.emit('logs', data)
		})
	})

	python.on('exit', (code) => {
		console.log("Process quit with code : " + code);
	});
})

module.exports = router