const multer = require('multer')
const fs = require('fs')
const { spawn } = require('child_process')
const router = require('express').Router();
const MongoClient = require('mongodb').MongoClient;
const ObjectId = require('mongodb').ObjectId

const url = "mongodb://localhost:27017/";
const DIR = './Uploads/project_';

const storage = multer.diskStorage({
	destination: (req, file, cb) => {
		let dir = DIR + req.query.project
		if (!fs.existsSync(dir)) {
			fs.mkdirSync(dir);
		}
		cb(null, dir);
	},
	filename: (req, file, cb) => {
		cb(null, file.originalname);
	}
});

router.post('/', function (req, res) {
	sess = req.session;
	var uploadPost = multer({ storage: storage }).single('file');
	uploadPost(req, res, function (err) {
		if (err) {
			console.log(err)
			return res.end("error uploading file");
		}
		let projectID = req.query.project
		let plat = req.query.plat
		console.log("Req Query :", req.query)
		let filePath = req.file.path

		const python = spawn('python', ['Python Scripts/getStats.py'])

		python.stdin.write(filePath)
		python.stdin.end()

		var parsedData = "";
		var data_error = undefined;
		python.stdout.on('data', function (data) {
			console.log("Data printed", data.toString())
			parsedData += data.toString()
		})

		python.stderr.on('data', function (data) {
			let error_str = data.toString()
			error_str.replace('/r/n', '')
			console.log(error_str.lastIndexOf('Exception'))
			data_error = error_str.substr(error_str.lastIndexOf('Exception') + 11)
		});

		python.stdout.on('end', function () { })

		python.on('exit', (code) => {
			if (data_error) {
				res.send({ error: data_error, data: [] })
			} else {
				// dataToSend = JSON.parse(parsedData)
				// console.log(dataToSend)
				MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
					if (err) {
						res.send({ text: "None", error: err })
					} else {
						model = {}
						dataToSend = JSON.parse(parsedData)
						for (let i = 0; i < dataToSend.length; i++) {
							model[dataToSend[i].name] = {
								include: true,
								impute_int_with: 'mean',
								impute_str_with: 'Blank',
								type: this.dataToSend[i].type,
								replacevalue: "",
								replacewith: ""
							}
						};
						preprocessing_options = { over_all_dataset_options: { drop_rows: true }, column_wise_options: model }
						var database = client.db("FYP")
						const query = { _id: new ObjectId(projectID) }
						const options = {
							$set: {
								dataset_path: filePath, data_statistics: dataToSend, preprocessed_dataset_path: '',
								preprocessing_options: preprocessing_options, "model.hyperparameters":{}
							}
						}
						database.collection("Projects").updateOne(query, options, (err, result) => {
							if (err) {
								console.log("Error:", err)
							}
							client.close()
							console.log(result)

							sess.filePath = ""
							res.send({ error: "None", data: dataToSend })
						})
					}
				});
			}
		});
	});
});

module.exports = router;