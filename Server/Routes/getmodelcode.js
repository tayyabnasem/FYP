const router = require('express').Router();
const MongoClient = require('mongodb').MongoClient;
const ObjectId = require('mongodb').ObjectId
const fs = require('fs');

const url = "mongodb://localhost:27017/";

router.get('/', (req, res) => {
    MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
        if (err) {
            res.send({ text: "None", error: err })
        } else {
            var database = client.db("FYP")
            try {
                const query = { _id: new ObjectId(req.query.project) }
                const options = { projection: { model_file: 1 } }
                database.collection("Projects").findOne(query, options, (err, result) => {
                    client.close()
                    if (result) {
                        fs.readFile(result.model_file, "utf8", function (err, data) {
                            if (!err) {
                                res.send({ error: "None", data: data})
                            }
                        });
                        
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

