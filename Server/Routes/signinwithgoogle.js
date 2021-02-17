const MongoClient = require('mongodb').MongoClient;
const router = require('express').Router();

const url = "mongodb://localhost:27017/";

router.post('/', function (req, res) {
	sess = req.session
	if (sess.email) {
		res.send({ text: "Already Logged in", error: "None" })
	} else {
		console.log(req.body)
		var data = req.body
		MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
			if (err) {
				res.send({ text: "None", error: err })
			} else {
				var database = client.db("FYP")
				const query = { email: data.email }
				database.collection("Users").findOne(query, function (err, result) {
					console.log(result)
					dataToReturnInResponse = { text: "", error: "None" }
					if (result) {
						dataToReturnInResponse.text = "Logged in"
						sess.email = data.email
						sess.user_database_id = result._id
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

module.exports = router