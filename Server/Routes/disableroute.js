const router = require('express').Router();
const MongoClient = require('mongodb').MongoClient;
const ObjectId = require('mongodb').ObjectId

const url = "mongodb://localhost:27017/";

router.get('/', (req, res) => {
    sess = req.session
    //console.log(req.query)
    MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
        if (err) {
            res.send({ text: "None", error: err })
        } else {
            var database = client.db("FYP")
            try {
                const query = { _id: new ObjectId(req.query.project) }
                const options = { projection: { user_id: 1 } }
                database.collection("Projects").findOne(query, options, (err, result) => {
                    client.close()
                    if (result) {
                        console.log("UserId: ",result.user_id)
                        console.log("Sess User ID: ", sess.user_database_id)
                        console.log(sess)
                        res.send({ error: "", data: result.user_id == sess.user_database_id})
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
