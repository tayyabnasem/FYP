const router = require('express').Router();
const MongoClient = require('mongodb').MongoClient;
const ObjectId = require('mongodb').ObjectId

const url = "mongodb://localhost:27017/";

router.post('/', (req, res) => {
    MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
        if (err) {
            res.send({ text: "None", error: err })
        } else {
            var database = client.db("FYP")
            try {
                console.log("Query model", req.query)
                const query = { _id: new ObjectId(req.query.project) }
                database.collection("Projects").updateOne(query, { $set: { "model.hyperparameters": req.body } }, (err, result) => {
                    client.close()
                    if (result) {
                        res.send({ error: "None", data: "Successfull" })
                    } else {
                        console.log(err)
                        res.send({ error: "Project Not Found", data: [] })
                    }
                })
            } catch (Exception) {
                console.log(Exception)
                res.send({ error: "Project Not Found", data: [] })
            }
        }
    });
})

module.exports = router;

