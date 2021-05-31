const router = require('express').Router();

router.get('/', (req, res) => {
    var sess = req.session
    sess.email = ''
    sess.user_database_id = ''
    sess.filePath = ''
    sess.preprocessed_data_path = ''
    res.send({ text: "Succesfully Logged out" })
})

module.exports = router