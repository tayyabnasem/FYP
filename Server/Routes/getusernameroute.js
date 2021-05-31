const router = require('express').Router();
const MongoClient = require('mongodb').MongoClient;
const ObjectId = require('mongodb').ObjectId

const url = "mongodb://localhost:27017/";

router.get('/', (req, res) => {
    sess = req.session
    //console.log("USERCHECK: ",sess)
    MongoClient.connect(url, { useUnifiedTopology: true }, function (err, client) {
        if (err) {
            res.send({ text: "None", error: err })
        } else {
            var database = client.db("FYP")
            try {
                const query = { _id: new ObjectId(sess.user_database_id) }
                const options = { projection: { fullName: 1, email: 1 } }
                database.collection("Users").findOne(query, options, (err, result) => {
                    client.close()
                    if (result) {
                        //console.log(result)
                        res.send({error: "None", data:result})
                    } else {
                        res.send({ error: "User not found", data: {} })
                    }
                })
            } catch (Exception) {
                res.send({ error: "Invalid User ID", data: {} })
            }
        }
    });
})

module.exports = router;
