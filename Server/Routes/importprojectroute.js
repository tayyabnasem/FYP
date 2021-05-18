const router = require('express').Router();
const MongoClient = require('mongodb').MongoClient;
const ObjectID = require('mongodb').ObjectID

const url = "mongodb://localhost:27017/";

router.get('/', (req, res) => {
    sess = req.session
    console.log(req.query)
    MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
        if (err) {
            client.close()
            res.send({ text: "None", error: err })
        } else {
            var database = client.db("FYP")
            //try {
                const query = { _id: new ObjectID(req.query.project) }
                const options = { projection: {_id : 0}}
                database.collection("Projects").findOne(query, options,  (err, result) => {
                    if (result) {
                        let result_copy = JSON.parse(JSON.stringify(result));
                        result_copy.preprocessed_dataset_path=""
                        result_copy.dataset_path=""

                        if(result.domain=="Deep Learning"){
                            result_copy.model_file=""
                            result_copy.trained_model=""
                        }
                        result_copy.user_id = sess.user_database_id
                        //result._id = new ObjectID()
                        database.collection("Projects").insertOne(result_copy, (err, result) => {
                            if (result) {
                                console.log(result)
                                res.send({ error: "None", data: result_copy })
                            } else {
                                console.log(err)
                                res.send({ error: "Project Not Found", data: [] })
                            }
                            client.close()
                        })
                        console.log(result_copy)
                    } else {
                        res.send({ error: "Project Not Found", data: [] })
                    }
                })
        }
    });
})

module.exports = router;
