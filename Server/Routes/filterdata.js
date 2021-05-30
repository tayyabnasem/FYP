const router = require('express').Router();
const MongoClient = require('mongodb').MongoClient;
const ObjectId = require('mongodb').ObjectId

const url = "mongodb://localhost:27017/";
const { spawn } = require('child_process');

router.post('/', async function (req, res) {
	let sess = req.session
	data = req.body
	values_to_fill = []
	sess.filePath = ""

	const python = spawn('python', ['Python Scripts/filterData.py'])

	if (!sess.filePath) {
		await MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
			if (err) {
				res.send({ text: "None", error: err })
			} else {
				var database = client.db("FYP")
				try {
					const query = { _id: new ObjectId(req.query.project) }
					database.collection("Projects").findOneAndUpdate(query, { $set: { preprocessing_options: data } }, (err, result) => {
						client.close()
						if (result) {
							//console.log(result)
							sess.filePath = result.value.dataset_path
							console.log('Filepath mongo:', sess.filePath)
							python.stdin.write(sess.filePath + '\n')
							python.stdin.write(JSON.stringify(data))
							python.stdin.end()
						}
					})
				} catch (Exception) {
					res.send({ error: "Project Not Found", data: [] })
				}
			}
		});
	} else {
		MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
			if (err) {
				res.send({ text: "None", error: err })
			} else {
				var database = client.db("FYP")
				try {
					const query = { _id: new ObjectId(req.query.project) }
					database.collection("Projects").updateOne(query, { $set: { preprocessing_options: data } }, (err, result) => {
						client.close()
						if (result) {
							python.stdin.write(sess.filePath + '\n')
							python.stdin.write(JSON.stringify(data))
							python.stdin.end()
						}
					})
				} catch (Exception) {
					res.send({ error: "Project Not Found", data: [] })
				}
			}
		});
	}

	var parsedData = "";
	python.stdout.on('data', function (data) {
		console.log("Data printed", data.toString())
		parsedData += data.toString()
	})

	python.stderr.on('data', function (data) {
		console.log(data.toString())
	});

	python.stdout.on('end', function () {
		parsedData.replace('/r/n', '')
		console.log(parsedData)
		sess.preprocessed_data_path = sess.filePath.split(".csv")[0] + "_preprocessed.csv"
		MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
			if (err) {
				res.send({ text: "None", error: err })
			} else {
				var database = client.db("FYP")
				const query = { _id: new ObjectId(req.query.project) }
				database.collection("Projects").updateOne(query, { $set: { preprocessed_dataset_path: sess.preprocessed_data_path } }, (err, result) => {
					client.close()
					console.log('Preprocess: ', sess.preprocessed_data_path)
					res.send({ text: "Success", err: "None" })
				})
			}
		});
	})

	python.on('exit', (code) => {
		console.log("Process quit with code : " + code);
	});
})

module.exports = router