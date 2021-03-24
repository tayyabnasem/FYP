const router = require('express').Router();
const MongoClient = require('mongodb').MongoClient;
const ObjectId = require('mongodb').ObjectId

const url = "mongodb://localhost:27017/";

router.get('/', (req, res) => {
    sess = req.session
    console.log(req.query)
    MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
        if (err) {
            res.send({ text: "None", error: err })
        } else {
            var database = client.db("FYP")
            try {
                const query = { _id: new ObjectId(req.query.project) }
                const options = { projection: { model: 1 } }
                database.collection("Projects").findOne(query, options, (err, result) => {
                    client.close()
                    if (result) {
                        res.send({ error: "None", data: result.model })
                    } else {
                        res.send({ error: "Project Not Found", data: [] })
                    }
                })
            } catch (Exception) {
                res.send({ error: "Project Not Found", data: [] })
            }


        }
    });
})

module.exports = router;
