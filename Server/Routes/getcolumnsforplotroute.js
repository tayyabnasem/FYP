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
                const options = { projection: { data_statistics: 1, preprocessed_dataset_path: 1 } }
                database.collection("Projects").findOne(query, options, (err, result) => {
                    client.close()
                    if (result) {
                        sess.preprocessed_data_path = result.preprocessed_dataset_path
                        columns = []
                        console.log(result)
                        for (let i = 0; i < result.data_statistics.length; i++) {
                            columns.push(result.data_statistics[i].name)
                        }
                        res.send({ error: "None", columns: columns })
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
