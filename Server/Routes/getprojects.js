const MongoClient = require('mongodb').MongoClient;
const router = require('express').Router();

const url = "mongodb://localhost:27017/";

router.get('/', function (req, res) {
	sess = req.session
	console.log("Received Session: ",sess)
	MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
		if (err) {
			res.send({ text: "None", error: err })
		} else {
			var database = client.db("FYP")
			const query = { user_id: sess.user_database_id }
			database.collection("Projects").find(query, { projection: { _id: 1, description: 1, domain: 1, name: 1 } }).toArray(function (err, result) {
				client.close()
				res.send(result)
			})
		}
	});
})

module.exports = router;