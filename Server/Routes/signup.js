const MongoClient = require('mongodb').MongoClient;
const router = require('express').Router();

const url = "mongodb://localhost:27017/";

router.post('/', function (req, res) {
	//console.log(req.body)
	var data = req.body
	MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
		if (err) {
			console.log('error')
			res.send({ text: undefined, error: err })
		} else {
			var database = client.db("FYP")
			database.collection("Users").findOne({ email: data.email }, function (err, result) {
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

module.exports = router;