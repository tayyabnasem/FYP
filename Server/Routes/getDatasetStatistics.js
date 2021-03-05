const router = require('express').Router();
const MongoClient = require('mongodb').MongoClient;
const ObjectId = require('mongodb').ObjectId

const url = "mongodb://localhost:27017/";

router.get('/', (req, res) => {
    console.log(req.query)
    MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
        if (err) {
            res.send({ text: "None", error: err })
        } else {
            var database = client.db("FYP")
            try {
                const query = { _id: new ObjectId(req.query.project) }
                const options = { projection: { data_statistics: 1, preprocessing_options: 1 } }
                database.collection("Projects").findOne(query, options, (err, result) => {
                    client.close()
                    if (result) {
                        //console.log(result.data_statistics)
                        res.send({ error: "None", data_statistics: result.data_statistics , preprocessing_options: result.preprocessing_options})
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
