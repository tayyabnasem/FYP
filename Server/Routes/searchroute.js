const router = require('express').Router();
const MongoClient = require('mongodb').MongoClient;

const url = "mongodb://localhost:27017/";

router.get('/', (req, res) => {
    sess = req.session
    console.log(req.query)
    MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
        if (err) {
            res.send({ text: "None", error: err })
        } else {
            var database = client.db("FYP")
            const query = { name: new RegExp(req.query.search, 'gi'), user_id: {$not:{$eq:sess.user_database_id}}}
            const options = { projection: { data_statistics: 1, preprocessed_dataset_path: 1, preprocessing_options: 1 } }
            database.collection("Projects").find(query, { projection: { _id: 1, description: 1, domain: 1, name: 1 } }).toArray(function (err, result) {
                client.close()
                res.send(result)
            })

        }
    });
})

module.exports = router;