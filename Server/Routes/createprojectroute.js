const MongoClient = require('mongodb').MongoClient;
const router = require('express').Router();

const url = "mongodb://localhost:27017/";

router.post('/', function (req, res) {
	sess = req.session
	//console.log(req.body)
	var data = req.body
	data['user_id'] = sess.user_database_id
	data['dataset_path'] = ''
	data['preprocessed_dataset_path'] = ''
	data['preprocessing_options'] = {}
	data['data_statistics'] = []
	data['model_file'] = ""
	if (data['domain'] == "Deep Learning") {
		data['model'] = { hyperparameters: {}, layers: [] }
		data['trained_model'] = ""
		
	} else {
		data['model'] = { algorithm: "", algo_type: "", parameters: {}}
	}

	MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
		if (err) {
			res.send({ text: "None", error: err })
		} else {
			var database = client.db("FYP")
			const query = { email: data.email }
			//const query = { $or: [ { userName: data.userName }, { email: data.userName} ] }
			database.collection("Projects").insertOne(data, function (err, result) {
				console.log("Project ID", result.insertedId)
				let dataToReturnInResponse = { text: "Project Created", projectID: result.insertedId }
				if (err) {
					dataToReturnInResponse.text = "Project could not be created"
				}
				sess.projectID = result.insertedId
				client.close();
				res.send(dataToReturnInResponse)
			})
		}
	});
})

module.exports = router;